package org.easy.schulte.feature.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.easy.schulte.core.data.TrainingRepository
import org.easy.schulte.core.domain.TrainingReportCalculator
import org.easy.schulte.core.domain.TrainingReportInput
import org.easy.schulte.core.domain.TrainingStateMachine
import org.easy.schulte.core.model.training.enums.LayoutMode
import org.easy.schulte.state.trainingStateIn

internal class TrainingViewModel(
  private val repository: TrainingRepository,
  private val reportCalculator: TrainingReportCalculator,
  private val stateMachine: TrainingStateMachine,
) : ViewModel() {
  private val repositoryState = repository.trainingStateIn(viewModelScope)
  private val boardPresentation = MutableStateFlow(BoardPresentationState())
  val state = combine(repositoryState, boardPresentation) { training, presentation ->
    training.copy(
      numbers = presentation.transitionNumbers ?: training.numbers,
      boardRevision = presentation.boardRevision,
      feedbackBoardRevision = presentation.feedbackBoardRevision,
      isBoardTransitioning = presentation.isBoardTransitioning,
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = repositoryState.value,
  )

  private val _events = Channel<TrainingEvent>()
  val events = _events.receiveAsFlow()

  private var timerJob: Job? = null
  private var boardTransitionJob: Job? = null
  private var feedbackClearJob: Job? = null
  private var trainingSessionId = 0L
  private var isTrainingActive = false

  fun onAction(action: TrainingAction) {
    when (action) {
      is TrainingAction.CellClick -> onCellClick(action.value)
      TrainingAction.RestartTraining -> startTraining()
      TrainingAction.ExitTraining -> stopTraining()
    }
  }

  fun startTraining() {
    cancelTransientJobs()
    val sessionId = ++trainingSessionId
    isTrainingActive = true
    val configuration = repository.currentConfiguration()
    val previousBoard = repository.currentTrainingState().numbers
    timerJob?.cancel()
    boardPresentation.update {
      it.copy(
        feedbackBoardRevision = it.boardRevision,
        isBoardTransitioning = false,
      )
    }
    repository.startTraining(
      stateMachine.createInitialBoard(
        numberCount = configuration.selectedGrid.count,
        previousBoard = previousBoard,
      ),
    )
    timerJob = viewModelScope.launch {
      val startedAt = kotlin.time.TimeSource.Monotonic.markNow()
      while (isTrainingActive && trainingSessionId == sessionId) {
        repository.updateElapsedMillis(startedAt.elapsedNow().inWholeMilliseconds)
        delay(33)
      }
    }
  }

  fun stopTraining() {
    if (!isTrainingActive) return
    endTrainingSession()
    repository.exitTraining()
    sendEvent(TrainingEvent.Exited)
  }

  fun disposeTraining() {
    endTrainingSession()
    repository.exitTraining()
  }

  private fun onCellClick(value: Int) {
    if (!isTrainingActive || boardPresentation.value.isBoardTransitioning) return

    val current = repository.currentTrainingState()
    val configuration = repository.currentConfiguration()
    val result = stateMachine.onCellTap(
      current = current,
      value = value,
      totalCount = configuration.selectedGrid.count,
      markMode = configuration.selectedMarkMode,
      layoutMode = configuration.selectedLayoutMode,
    ) ?: return

    val shouldTransitionBoard = result.feedback.isCorrect &&
      configuration.selectedLayoutMode == LayoutMode.ShuffleAfterCorrectTap &&
      !result.isCompleted

    if (shouldTransitionBoard) {
      startBoardTransition(result.numbers)
    } else {
      boardPresentation.update {
        it.copy(feedbackBoardRevision = it.boardRevision)
      }
    }
    repository.applyCellTap(result)
    if (result.isCompleted) {
      completeTraining()
    } else {
      clearFeedbackLater(value)
    }
  }

  private fun clearFeedbackLater(value: Int) {
    val sessionId = trainingSessionId
    feedbackClearJob?.cancel()
    feedbackClearJob = viewModelScope.launch {
      delay(180)
      if (isTrainingActive && trainingSessionId == sessionId) {
        repository.clearFeedbackIfMatches(value)
      }
    }
  }

  private fun startBoardTransition(numbers: List<Int>) {
    val sessionId = trainingSessionId
    boardTransitionJob?.cancel()
    boardPresentation.update {
      it.copy(
        boardRevision = it.boardRevision + 1,
        feedbackBoardRevision = it.boardRevision,
        isBoardTransitioning = true,
        transitionNumbers = numbers,
      )
    }
    boardTransitionJob = viewModelScope.launch {
      delay(BoardTransitionDurationMillis.toLong())
      if (isTrainingActive && trainingSessionId == sessionId) {
        boardPresentation.update { it.copy(isBoardTransitioning = false) }
      }
    }
  }

  private fun completeTraining() {
    timerJob?.cancel()
    timerJob = null
    isTrainingActive = false
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

  private fun endTrainingSession() {
    isTrainingActive = false
    trainingSessionId += 1
    timerJob?.cancel()
    timerJob = null
    cancelTransientJobs()
  }

  private fun cancelTransientJobs() {
    boardTransitionJob?.cancel()
    boardTransitionJob = null
    feedbackClearJob?.cancel()
    feedbackClearJob = null
    boardPresentation.update {
      it.copy(
        feedbackBoardRevision = it.boardRevision,
        isBoardTransitioning = false,
        transitionNumbers = null,
      )
    }
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

internal const val BoardTransitionDurationMillis = 120

private data class BoardPresentationState(
  val boardRevision: Long = 0L,
  val feedbackBoardRevision: Long = 0L,
  val isBoardTransitioning: Boolean = false,
  val transitionNumbers: List<Int>? = null,
)
