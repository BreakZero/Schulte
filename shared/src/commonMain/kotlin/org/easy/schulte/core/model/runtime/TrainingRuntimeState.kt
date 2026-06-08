package org.easy.schulte.core.model.runtime

import org.easy.schulte.core.model.training.CellFeedback

data class TrainingRuntimeState(
  val numbers: List<Int> = emptyList(),
  val currentTarget: Int = 1,
  val completedNumbers: Set<Int> = emptySet(),
  val elapsedMillis: Long = 0L,
  val errorCount: Int = 0,
  val lastFeedback: CellFeedback? = null,
)
