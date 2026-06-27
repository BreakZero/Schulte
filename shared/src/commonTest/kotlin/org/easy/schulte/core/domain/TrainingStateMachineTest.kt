package org.easy.schulte.core.domain

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.easy.schulte.core.model.runtime.TrainingRuntimeState
import org.easy.schulte.core.model.training.CellFeedback
import org.easy.schulte.core.model.training.enums.LayoutMode
import org.easy.schulte.core.model.training.enums.MarkMode

class TrainingStateMachineTest {
  @Test
  fun staticModeCorrectTapKeepsBoardAndUpdatesRelatedState() {
    val board = listOf(3, 1, 4, 2)
    val result = stateMachine(seed = 1).onCellTap(
      current = state(numbers = board),
      value = 1,
      totalCount = board.size,
      markMode = MarkMode.AssistedMarking,
      layoutMode = LayoutMode.Static,
    )!!

    assertEquals(board, result.numbers)
    assertEquals(2, result.currentTarget)
    assertEquals(setOf(1), result.completedNumbers)
    assertEquals(CellFeedback(1, isCorrect = true), result.feedback)
    assertFalse(result.isCompleted)
  }

  @Test
  fun dynamicModeCorrectTapChangesBoard() {
    val machine = TrainingStateMachine(BoardShuffler(ConstantRandom))
    val board = machine.createInitialBoard(numberCount = 9, previousBoard = emptyList())

    val result = machine.onCellTap(
      current = state(numbers = board),
      value = 1,
      totalCount = board.size,
      markMode = MarkMode.BriefFeedbackOnly,
      layoutMode = LayoutMode.ShuffleAfterCorrectTap,
    )!!

    assertNotEquals(board, result.numbers)
    assertEquals((1..9).toList(), result.numbers.sorted())
  }

  @Test
  fun incorrectTapOnlyUpdatesErrorAndFeedback() {
    val board = listOf(3, 1, 4, 2)
    val current = state(
      numbers = board,
      currentTarget = 2,
      completedNumbers = setOf(1),
      errorCount = 4,
    )

    val result = stateMachine(seed = 2).onCellTap(
      current = current,
      value = 4,
      totalCount = board.size,
      markMode = MarkMode.AssistedMarking,
      layoutMode = LayoutMode.ShuffleAfterCorrectTap,
    )!!

    assertEquals(board, result.numbers)
    assertEquals(current.currentTarget, result.currentTarget)
    assertEquals(current.completedNumbers, result.completedNumbers)
    assertEquals(5, result.errorCount)
    assertEquals(CellFeedback(4, isCorrect = false), result.feedback)
  }

  @Test
  fun finalCorrectTapCompletesWithoutReshuffling() {
    val board = listOf(3, 1, 4, 2)
    val result = stateMachine(seed = 3).onCellTap(
      current = state(numbers = board, currentTarget = 4),
      value = 4,
      totalCount = board.size,
      markMode = MarkMode.BriefFeedbackOnly,
      layoutMode = LayoutMode.ShuffleAfterCorrectTap,
    )!!

    assertEquals(board, result.numbers)
    assertEquals(5, result.currentTarget)
    assertTrue(result.isCompleted)
  }

  @Test
  fun reshuffleContainsEveryNumberExactlyOnce() {
    val shuffler = BoardShuffler(Random(42))
    val previous = (1..49).toList()

    val result = shuffler.reshuffle(previous)

    assertEquals(49, result.size)
    assertEquals(49, result.toSet().size)
    assertEquals(previous.toSet(), result.toSet())
  }

  @Test
  fun constantRandomUsesBoundedFallbackAndStillChangesBoard() {
    val shuffler = BoardShuffler(ConstantRandom)
    val previous = shuffler.createInitialBoard(numberCount = 25)

    val result = shuffler.reshuffle(previous)

    assertNotEquals(previous, result)
    assertEquals((1..25).toList(), result.sorted())
  }

  @Test
  fun restartCreatesAnewInitialBoardEvenWithConstantRandom() {
    val machine = TrainingStateMachine(BoardShuffler(ConstantRandom))
    val first = machine.createInitialBoard(numberCount = 9, previousBoard = emptyList())

    val restarted = machine.createInitialBoard(numberCount = 9, previousBoard = first)

    assertNotEquals(first, restarted)
    assertEquals(first.toSet(), restarted.toSet())
  }

  private fun stateMachine(seed: Int) = TrainingStateMachine(BoardShuffler(Random(seed)))

  private fun state(
    numbers: List<Int>,
    currentTarget: Int = 1,
    completedNumbers: Set<Int> = emptySet(),
    errorCount: Int = 0,
  ) = TrainingRuntimeState(
    numbers = numbers,
    currentTarget = currentTarget,
    completedNumbers = completedNumbers,
    errorCount = errorCount,
  )
}

private object ConstantRandom : Random() {
  override fun nextBits(bitCount: Int): Int = 0
}
