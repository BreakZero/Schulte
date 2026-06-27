package org.easy.schulte.core.domain

import kotlin.random.Random

internal class BoardShuffler(
  private val random: Random = Random.Default,
) {
  fun createInitialBoard(
    numberCount: Int,
    previousBoard: List<Int>? = null,
  ): List<Int> = shuffledDistinctFrom(
    numbers = (1..numberCount).toList(),
    previousBoard = previousBoard,
  )

  fun reshuffle(previousBoard: List<Int>): List<Int> = shuffledDistinctFrom(
    numbers = (1..previousBoard.size).toList(),
    previousBoard = previousBoard,
  )

  private fun shuffledDistinctFrom(
    numbers: List<Int>,
    previousBoard: List<Int>?,
  ): List<Int> {
    val shuffled = numbers.shuffled(random)
    if (previousBoard == null || shuffled != previousBoard || shuffled.size < 2) {
      return shuffled
    }

    // Board values are unique, so one rotation is a bounded, reliable fallback.
    return shuffled.drop(1) + shuffled.first()
  }
}
