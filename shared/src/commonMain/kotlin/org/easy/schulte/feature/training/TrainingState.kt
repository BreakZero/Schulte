package org.easy.schulte.feature.training

import org.easy.schulte.core.model.training.CellFeedback
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.LayoutMode
import org.easy.schulte.core.model.training.enums.MarkMode

internal data class TrainingState(
  val selectedGrid: GridSpec = GridSpec.Five,
  val selectedMarkMode: MarkMode = MarkMode.BriefFeedbackOnly,
  val selectedLayoutMode: LayoutMode = LayoutMode.Static,
  val numbers: List<Int> = emptyList(),
  val currentTarget: Int = 1,
  val completedNumbers: Set<Int> = emptySet(),
  val elapsedMillis: Long = 0L,
  val errorCount: Int = 0,
  val lastFeedback: CellFeedback? = null,
  val boardRevision: Long = 0L,
  val feedbackBoardRevision: Long = 0L,
  val isBoardTransitioning: Boolean = false,
)
