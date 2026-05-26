package org.easy.schulte.core.model

enum class ScoreLevel(val title: String) {
    Excellent("优"),
    Good("良"),
    Pass("及格"),
    Below("未达标"),
    Practice("练习参考"),
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
