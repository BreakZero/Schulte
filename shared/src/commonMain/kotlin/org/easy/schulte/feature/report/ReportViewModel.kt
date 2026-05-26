package org.easy.schulte.feature.report

import org.easy.schulte.core.model.AiAnalysisState
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.model.Screen

internal class ReportViewModel(
    private val state: () -> SchulteState,
    private val updateState: (((SchulteState) -> SchulteState) -> Unit),
    private val restartTraining: () -> Unit,
    private val generateAiAnalysis: () -> Unit,
) {
    fun onAction(action: ReportAction) {
        when (action) {
            ReportAction.RestartTraining -> restartTraining()
            ReportAction.BackToConfig -> updateState { it.copy(screen = Screen.Config, lastFeedback = null) }
            ReportAction.OpenSettings -> updateState { it.copy(screen = Screen.Settings, settingsMessage = null) }
            ReportAction.GenerateAiAnalysis -> {
                if (state().aiSettings.isConfigured) {
                    generateAiAnalysis()
                } else {
                    updateState { it.copy(aiAnalysisState = AiAnalysisState.NeedsSettings) }
                }
            }
        }
    }
}
