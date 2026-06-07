package org.easy.schulte.feature.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.easy.schulte.core.data.SchulteRepository
import org.easy.schulte.core.domain.TrainingReportCalculator
import org.easy.schulte.core.domain.TrainingReportInput
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.state.trainingStateIn
import kotlin.random.Random

internal class TrainingViewModel(
  private val repository: SchulteRepository,
  private val reportCalculator: TrainingReportCalculator,
) : ViewModel() {
  val state = repository.trainingStateIn(viewModelScope)

  private val _events = Channel<TrainingEvent>()
  val events = _events.receiveAsFlow()

  private var timerJob: Job? = null

  fun onAction(action: TrainingAction) {
    when (action) {
      is TrainingAction.CellClick -> onCellClick(action.value)
      TrainingAction.RestartTraining -> startTraining()
      TrainingAction.ExitTraining -> stopTraining()
    }
  }

  fun startTraining() {
    val current = repository.currentState()
    timerJob?.cancel()
    repository.startTraining((1..current.selectedGrid.count).shuffled(Random.Default))
    timerJob = viewModelScope.launch {
      val startedAt = kotlin.time.TimeSource.Monotonic.markNow()
      while (true) {
        repository.updateElapsedMillis(startedAt.elapsedNow().inWholeMilliseconds)
        delay(33)
      }
    }
  }

  fun stopTraining() {
    timerJob?.cancel()
    repository.exitTraining()
    sendEvent(TrainingEvent.Exited)
  }

  fun disposeTraining() {
    timerJob?.cancel()
    repository.exitTraining()
  }

  private fun onCellClick(value: Int) {
    val current = repository.currentState()
    if (current.numbers.isEmpty()) return

    if (value == current.currentTarget) {
      val nextTarget = current.currentTarget + 1
      val nextCompleted = if (current.selectedMarkMode == MarkMode.AssistedMarking) {
        current.completedNumbers + value
      } else {
        current.completedNumbers
      }
      repository.recordCorrectCell(
        value = value,
        completedNumbers = nextCompleted,
        nextTarget = nextTarget,
      )
      clearFeedbackLater(value)

      if (value == current.selectedGrid.count) {
        completeTraining()
      }
    } else {
      repository.recordIncorrectCell(value)
      clearFeedbackLater(value)
    }
  }

  private fun clearFeedbackLater(value: Int) {
    viewModelScope.launch {
      delay(180)
      repository.clearFeedbackIfMatches(value)
    }
  }

  private fun completeTraining() {
    timerJob?.cancel()
    val current = repository.currentState()
    val report = reportCalculator.createReport(
      TrainingReportInput(
        gridSpec = current.selectedGrid,
        ageGroup = current.selectedAgeGroup,
        markMode = current.selectedMarkMode,
        elapsedMillis = current.elapsedMillis,
        errorCount = current.errorCount,
      ),
    )
    repository.finishTraining(report)
    sendEvent(TrainingEvent.Completed(report))
  }

  private fun sendEvent(event: TrainingEvent) {
    viewModelScope.launch {
      _events.send(event)
    }
  }

  override fun onCleared() {
    disposeTraining()
    super.onCleared()
  }
}
