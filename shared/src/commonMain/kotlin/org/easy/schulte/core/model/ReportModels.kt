package org.easy.schulte.core.model

enum class ScoreLevel {
  Excellent,
  Good,
  Pass,
  Below,
  Practice,
}

data class TrainingReport(
  val gridSpec: GridSpec,
  val ageGroup: AgeGroup,
  val markMode: MarkMode,
  val elapsedMillis: Long,
  val errorCount: Int,
  val scoreLevel: ScoreLevel,
  val isOfficialScore: Boolean,
  val nextTargetSeconds: Int?,
) {
  val elapsedSeconds: Double get() = elapsedMillis / 1000.0
}
