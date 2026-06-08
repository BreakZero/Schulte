package org.easy.schulte.core.model.records

import org.easy.schulte.core.model.records.enums.ImprovementStatus

data class ProgressComparison(
  val currentRecord: TrainingRecord,
  val previousRecord: TrainingRecord?,
  val bestRecord: TrainingRecord?,
  val recentRecords: List<TrainingRecord>,
  val improvementStatus: ImprovementStatus,
  val summaryText: String,
  val nextGoalText: String,
)
