package org.easy.schulte.feature.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.easy.schulte.core.data.TrainingRepository
import org.easy.schulte.core.domain.TrainingReportCalculator
import org.easy.schulte.core.domain.TrainingReportInput
import org.easy.schulte.core.domain.TrainingStateMachine
import org.easy.schulte.state.trainingStateIn

internal class TrainingViewModel(
  private val repository: TrainingRepository,
  private val reportCalculator: TrainingReportCalculator,
  private val stateMachine: TrainingStateMachine,
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
    val configuration = repository.currentConfiguration()
    val previousBoard = repository.currentTrainingState().numbers
    timerJob?.cancel()
    repository.startTraining(
      stateMachine.createInitialBoard(
        numberCount = configuration.selectedGrid.count,
        previousBoard = previousBoard,
      ),
    )
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
    val current = repository.currentTrainingState()
    val configuration = repository.currentConfiguration()
    val result = stateMachine.onCellTap(
      current = current,
      value = value,
      totalCount = configuration.selectedGrid.count,
      markMode = configuration.selectedMarkMode,
      layoutMode = configuration.selectedLayoutMode,
    ) ?: return

    repository.applyCellTap(result)
    clearFeedbackLater(value)
    if (result.isCompleted) {
      completeTraining()
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
    val current = repository.currentTrainingState()
    val configuration = repository.currentConfiguration()
    val report = reportCalculator.createReport(
      TrainingReportInput(
        gridSpec = configuration.selectedGrid,
        ageGroup = configuration.selectedAgeGroup,
        markMode = configuration.selectedMarkMode,
        layoutMode = configuration.selectedLayoutMode,
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
