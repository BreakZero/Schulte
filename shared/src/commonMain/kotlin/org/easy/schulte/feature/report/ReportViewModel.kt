package org.easy.schulte.feature.report

import org.easy.schulte.core.model.AiAnalysisState
import org.easy.schulte.core.model.SchulteState

internal class ReportViewModel(
  private val state: () -> SchulteState,
  private val updateState: (((SchulteState) -> SchulteState) -> Unit),
  private val restartTraining: () -> Unit,
  private val generateAiAnalysis: () -> Unit,
  private val backToConfig: () -> Unit,
  private val openSettings: () -> Unit,
) {
  fun onAction(action: ReportAction) {
    when (action) {
      ReportAction.RestartTraining -> restartTraining()

      ReportAction.BackToConfig -> {
        updateState { it.copy(lastFeedback = null) }
        backToConfig()
      }

      ReportAction.OpenSettings -> {
        updateState { it.copy(settingsMessage = null) }
        openSettings()
      }

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
