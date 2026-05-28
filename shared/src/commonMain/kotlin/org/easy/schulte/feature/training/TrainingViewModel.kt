package org.easy.schulte.feature.training

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.easy.schulte.core.domain.createReport
import org.easy.schulte.core.model.AiAnalysisState
import org.easy.schulte.core.model.CellFeedback
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.model.TrainingReport
import kotlin.random.Random

internal class TrainingViewModel(
  private val state: () -> SchulteState,
  private val updateState: (((SchulteState) -> SchulteState) -> Unit),
  private val scope: CoroutineScope,
  private val onTrainingCompleted: (TrainingReport) -> Unit,
  private val onTrainingStarted: () -> Unit,
  private val onTrainingExited: () -> Unit,
) {
  private var timerJob: Job? = null

  fun onAction(action: TrainingAction) {
    when (action) {
      is TrainingAction.CellClick -> onCellClick(action.value)
      TrainingAction.RestartTraining -> startTraining()
      TrainingAction.ExitTraining -> stopTraining()
    }
  }

  fun startTraining() {
    val current = state()
    timerJob?.cancel()
    updateState {
      it.copy(
        numbers = (1..current.selectedGrid.count).shuffled(Random.Default),
        currentTarget = 1,
        completedNumbers = emptySet(),
        elapsedMillis = 0L,
        errorCount = 0,
        lastFeedback = null,
        report = null,
        aiAnalysis = null,
        aiAnalysisState = AiAnalysisState.Idle,
      )
    }
    onTrainingStarted()
    timerJob = scope.launch {
      val startedAt = kotlin.time.TimeSource.Monotonic.markNow()
      while (true) {
        updateState { it.copy(elapsedMillis = startedAt.elapsedNow().inWholeMilliseconds) }
        delay(33)
      }
    }
  }

  fun stopTraining() {
    timerJob?.cancel()
    updateState { it.copy(lastFeedback = null) }
    onTrainingExited()
  }

  private fun onCellClick(value: Int) {
    val current = state()
    if (current.numbers.isEmpty()) return

    if (value == current.currentTarget) {
      val nextTarget = current.currentTarget + 1
      val nextCompleted = if (current.selectedMarkMode == MarkMode.AssistedMarking) {
        current.completedNumbers + value
      } else {
        current.completedNumbers
      }
      updateState {
        it.copy(
          currentTarget = nextTarget,
          completedNumbers = nextCompleted,
          lastFeedback = CellFeedback(value, isCorrect = true),
        )
      }
      clearFeedbackLater(value)

      if (value == current.selectedGrid.count) {
        completeTraining()
      }
    } else {
      updateState {
        it.copy(
          errorCount = it.errorCount + 1,
          lastFeedback = CellFeedback(value, isCorrect = false),
        )
      }
      clearFeedbackLater(value)
    }
  }

  private fun clearFeedbackLater(value: Int) {
    scope.launch {
      delay(180)
      updateState { current ->
        if (current.lastFeedback?.value == value) current.copy(lastFeedback = null) else current
      }
    }
  }

  private fun completeTraining() {
    timerJob?.cancel()
    val report = createReport(state())
    updateState {
      it.copy(
        report = report,
        elapsedMillis = report.elapsedMillis,
        lastFeedback = null,
      )
    }
    onTrainingCompleted(report)
  }
}
