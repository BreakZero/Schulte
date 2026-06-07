package org.easy.schulte.core.model

enum class ImprovementStatus {
  FirstRecord,
  ImprovedSpeed,
  ImprovedAccuracy,
  PersonalBest,
  Stable,
  Mixed,
  Declined,
}

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

data class TrainingRecordSummary(
  val totalCount: Int = 0,
  val recent7DaysCount: Int = 0,
  val bestRecord: TrainingRecord? = null,
  val latestRecord: TrainingRecord? = null,
  val recentAverageTimeMillis: Long? = null,
  val recentAverageErrorCount: Double? = null,
)

data class ProgressComparison(
  val currentRecord: TrainingRecord,
  val previousRecord: TrainingRecord?,
  val bestRecord: TrainingRecord?,
  val recentRecords: List<TrainingRecord>,
  val improvementStatus: ImprovementStatus,
  val summaryText: String,
  val nextGoalText: String,
)

enum class RecordGridFilter(val gridSpec: GridSpec?) {
  All(null),
  Three(GridSpec.Three),
  Four(GridSpec.Four),
  Five(GridSpec.Five),
  Seven(GridSpec.Seven),
}

enum class RecordModeFilter(val markMode: MarkMode?) {
  All(null),
  Standard(MarkMode.BriefFeedbackOnly),
  Assisted(MarkMode.AssistedMarking),
}

enum class RecordTimeFilter(val days: Int?) {
  All(null),
  Last7Days(7),
  Last30Days(30),
}
