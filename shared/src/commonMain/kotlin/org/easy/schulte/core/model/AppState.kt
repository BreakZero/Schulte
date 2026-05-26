package org.easy.schulte.core.model

data class SchulteState(
    val screen: Screen = Screen.Config,
    val selectedGrid: GridSpec = GridSpec.Five,
    val selectedAgeGroup: AgeGroup = AgeGroup.Adult,
    val selectedMarkMode: MarkMode = MarkMode.BriefFeedbackOnly,
    val numbers: List<Int> = emptyList(),
    val currentTarget: Int = 1,
    val completedNumbers: Set<Int> = emptySet(),
    val elapsedMillis: Long = 0L,
    val errorCount: Int = 0,
    val lastFeedback: CellFeedback? = null,
    val report: TrainingReport? = null,
    val aiSettings: AiSettings = AiSettings(),
    val apiKeyVisible: Boolean = false,
    val settingsMessage: String? = null,
    val aiAnalysis: AiAnalysis? = null,
    val aiAnalysisState: AiAnalysisState = AiAnalysisState.Idle,
)
