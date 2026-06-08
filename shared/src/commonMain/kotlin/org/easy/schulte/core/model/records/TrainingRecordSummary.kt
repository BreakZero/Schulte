package org.easy.schulte.core.model.records

data class TrainingRecordSummary(
  val totalCount: Int = 0,
  val recent7DaysCount: Int = 0,
  val bestRecord: TrainingRecord? = null,
  val latestRecord: TrainingRecord? = null,
  val recentAverageTimeMillis: Long? = null,
  val recentAverageErrorCount: Double? = null,
)
