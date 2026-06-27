package org.easy.schulte.core.model.report

import org.easy.schulte.core.model.report.enums.ScoreLevel
import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.LayoutMode
import org.easy.schulte.core.model.training.enums.MarkMode

data class TrainingReport(
  val gridSpec: GridSpec,
  val ageGroup: AgeGroup,
  val markMode: MarkMode,
  val layoutMode: LayoutMode,
  val elapsedMillis: Long,
  val errorCount: Int,
  val scoreLevel: ScoreLevel,
  val isOfficialScore: Boolean,
  val nextTargetSeconds: Int?,
) {
  val elapsedSeconds: Double get() = elapsedMillis / 1000.0
}
