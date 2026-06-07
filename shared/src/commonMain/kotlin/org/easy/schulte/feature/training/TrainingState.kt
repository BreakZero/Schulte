package org.easy.schulte.feature.training

import org.easy.schulte.core.model.CellFeedback
import org.easy.schulte.core.model.GridSpec
import org.easy.schulte.core.model.MarkMode

internal data class TrainingState(
  val selectedGrid: GridSpec = GridSpec.Five,
  val selectedMarkMode: MarkMode = MarkMode.BriefFeedbackOnly,
  val numbers: List<Int> = emptyList(),
  val currentTarget: Int = 1,
  val completedNumbers: Set<Int> = emptySet(),
  val elapsedMillis: Long = 0L,
  val errorCount: Int = 0,
  val lastFeedback: CellFeedback? = null,
)
