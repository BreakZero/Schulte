package org.easy.schulte.core.model.records

import org.easy.schulte.core.model.records.enums.ImprovementStatus
import org.easy.schulte.core.model.report.enums.ScoreLevel
import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.MarkMode

data class TrainingRecord(
  val id: String,
  val ownerUserId: String? = null,
  val createdAt: Long,
  val gridSpec: GridSpec,
  val ageGroup: AgeGroup,
  val markMode: MarkMode,
  val elapsedTimeMillis: Long,
  val errorCount: Int,
  val scoreLevel: ScoreLevel,
  val isPersonalBest: Boolean,
  val previousRecordId: String?,
  val improvementStatus: ImprovementStatus,
  val timeDeltaMillis: Long?,
  val errorDelta: Int?,
) {
  val elapsedSeconds: Double get() = elapsedTimeMillis / 1000.0
}
