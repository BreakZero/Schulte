package org.easy.schulte.feature.training

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.easy.schulte.core.data.TrainingRepository
import org.easy.schulte.core.domain.TrainingReportCalculator
import org.easy.schulte.core.domain.TrainingReportInput
import org.easy.schulte.core.model.training.enums.MarkMode
import org.easy.schulte.state.trainingStateIn

internal open class TrainingViewModel(
  private val repository: TrainingRepository,
  private val reportCalculator: TrainingReportCalculator,
  private val executionContext: TrainingExecutionContext,
) : ViewModel() {
  private val scope = CoroutineScope(SupervisorJob() + executionContext.dispatcher)

  val state = repository.trainingStateIn(scope)

  private val _events = Channel<TrainingEvent>(Channel.BUFFERED)

  /**
   * One-shot navigation effects. Effects are buffered until one active collector consumes them;
   * they are not replayed after consumption, so a recreated screen must derive durable UI from state.
   */
  val events = _events.receiveAsFlow()

  private var timerJob: Job? = null
  private val feedbackJobs = mutableSetOf<Job>()

  fun onAction(action: TrainingAction) {
    when (action) {
      is TrainingAction.CellClick -> onCellClick(action.value)
      TrainingAction.RestartTraining -> startTraining()
      TrainingAction.ExitTraining -> stopTraining()
    }
  }

  fun startTraining() = scope.launch {
    val configuration = repository.currentConfiguration()
    cancelTrainingJobs()
    repository.startTraining(executionContext.random.shuffle((1..configuration.selectedGrid.count).toList()))
    startTimer()
  }

  fun stopTraining() = scope.launch {
    cancelTrainingJobs()
    repository.exitTraining()
    _events.trySend(TrainingEvent.Exited)
  }

  fun disposeTraining() {
    cancelTrainingJobs()
  }

  private fun startTimer(elapsedOffsetMillis: Long = 0L) {
    timerJob?.cancel()
    val startedAtMillis = executionContext.clock.nowMillis() - elapsedOffsetMillis
    timerJob = scope.launch {
      while (true) {
        repository.updateElapsedMillis(executionContext.clock.nowMillis() - startedAtMillis)
        delay(TIMER_TICK_MILLIS)
      }
    }
  }

  private fun onCellClick(value: Int) = scope.launch {
    val current = repository.currentTrainingState()
    val configuration = repository.currentConfiguration()
    if (current.numbers.isEmpty()) return@launch

    if (value == current.currentTarget) {
      val nextTarget = current.currentTarget + 1
      val nextCompleted = if (configuration.selectedMarkMode == MarkMode.AssistedMarking) {
        current.completedNumbers + value
      } else {
        current.completedNumbers
      }
      repository.recordCorrectCell(value, nextCompleted, nextTarget)
      clearFeedbackLater(value)

      if (value == configuration.selectedGrid.count) completeTraining()
    } else {
      repository.recordIncorrectCell(value)
      clearFeedbackLater(value)
    }
  }

  private fun clearFeedbackLater(value: Int) {
    val job = scope.launch {
      delay(FEEDBACK_CLEAR_MILLIS)
      repository.clearFeedbackIfMatches(value)
    }
    feedbackJobs += job
    job.invokeOnCompletion { feedbackJobs -= job }
  }

  private suspend fun completeTraining() {
    cancelTrainingJobs()
    val current = repository.currentTrainingState()
    val configuration = repository.currentConfiguration()
    val report = reportCalculator.createReport(
      TrainingReportInput(
        gridSpec = configuration.selectedGrid,
        ageGroup = configuration.selectedAgeGroup,
        markMode = configuration.selectedMarkMode,
        elapsedMillis = current.elapsedMillis,
        errorCount = current.errorCount,
      ),
    )
    repository.finishTraining(report)
    _events.trySend(TrainingEvent.Completed(report))
  }

  private fun cancelTrainingJobs() {
    timerJob?.cancel()
    timerJob = null
    feedbackJobs.toList().forEach(Job::cancel)
    feedbackJobs.clear()
  }

  internal fun clearForTest() = onCleared()

  override fun onCleared() {
    disposeTraining()
    _events.close()
    scope.cancel()
    super.onCleared()
  }

  private companion object {
    const val TIMER_TICK_MILLIS = 33L
    const val FEEDBACK_CLEAR_MILLIS = 180L
  }
}
