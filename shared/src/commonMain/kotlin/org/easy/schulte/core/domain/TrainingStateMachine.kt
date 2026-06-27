package org.easy.schulte.core.domain

import org.easy.schulte.core.model.runtime.TrainingRuntimeState
import org.easy.schulte.core.model.training.CellFeedback
import org.easy.schulte.core.model.training.enums.LayoutMode
import org.easy.schulte.core.model.training.enums.MarkMode

internal class TrainingStateMachine(
  private val boardShuffler: BoardShuffler,
) {
  fun createInitialBoard(
    numberCount: Int,
    previousBoard: List<Int>,
  ): List<Int> = boardShuffler.createInitialBoard(
    numberCount = numberCount,
    previousBoard = previousBoard.takeIf { it.size == numberCount },
  )

  fun onCellTap(
    current: TrainingRuntimeState,
    value: Int,
    totalCount: Int,
    markMode: MarkMode,
    layoutMode: LayoutMode,
  ): TrainingTapResult? {
    if (current.numbers.isEmpty()) return null

    if (value != current.currentTarget) {
      return TrainingTapResult(
        numbers = current.numbers,
        currentTarget = current.currentTarget,
        completedNumbers = current.completedNumbers,
        errorCount = current.errorCount + 1,
        feedback = CellFeedback(value, isCorrect = false),
        isCompleted = false,
      )
    }

    val isCompleted = value == totalCount
    val nextNumbers = if (
      layoutMode == LayoutMode.ShuffleAfterCorrectTap && !isCompleted
    ) {
      boardShuffler.reshuffle(current.numbers)
    } else {
      current.numbers
    }
    val nextCompletedNumbers = if (markMode == MarkMode.AssistedMarking) {
      current.completedNumbers + value
    } else {
      current.completedNumbers
    }

    return TrainingTapResult(
      numbers = nextNumbers,
      currentTarget = current.currentTarget + 1,
      completedNumbers = nextCompletedNumbers,
      errorCount = current.errorCount,
      feedback = CellFeedback(value, isCorrect = true),
      isCompleted = isCompleted,
    )
  }
}

internal data class TrainingTapResult(
  val numbers: List<Int>,
  val currentTarget: Int,
  val completedNumbers: Set<Int>,
  val errorCount: Int,
  val feedback: CellFeedback,
  val isCompleted: Boolean,
)
