package org.easy.schulte.feature.settings

import org.easy.schulte.core.model.AiAnalysisState
import org.easy.schulte.core.model.AiSettings
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.model.SettingsMessage

internal class SettingsViewModel(
  private val state: () -> SchulteState,
  private val updateState: (((SchulteState) -> SchulteState) -> Unit),
  private val closeSettings: () -> Unit,
) {
  fun onAction(action: SettingsAction) {
    when (action) {
      SettingsAction.BackFromSettings -> closeSettings()

      is SettingsAction.ToggleAiEnabled -> updateSettings { copy(aiEnabled = action.enabled) }

      is SettingsAction.ToggleAssistSetting -> updateSettings { copy(assistedMarkingEnabled = action.enabled) }

      is SettingsAction.UpdateApiKey -> updateSettings { copy(apiKey = action.value) }

      is SettingsAction.UpdateBaseUrl -> updateSettings { copy(baseUrl = action.value) }

      is SettingsAction.UpdateModelName -> updateSettings { copy(modelName = action.value) }

      SettingsAction.ToggleApiKeyVisibility -> updateState { it.copy(apiKeyVisible = !it.apiKeyVisible) }

      SettingsAction.TestAiConnection -> testAiConnection()

      SettingsAction.ClearAiSettings -> updateState {
        it.copy(
          aiSettings = it.aiSettings.copy(aiEnabled = false, apiKey = "", baseUrl = "", modelName = "gpt-4o-mini"),
          settingsMessage = SettingsMessage.AiCleared,
          aiAnalysisState = AiAnalysisState.Idle,
          aiAnalysis = null,
        )
      }

      SettingsAction.SaveSettings -> updateState {
        it.copy(
          settingsMessage = SettingsMessage.Saved,
          selectedMarkMode = if (it.aiSettings.assistedMarkingEnabled) {
            MarkMode.AssistedMarking
          } else {
            MarkMode.BriefFeedbackOnly
          },
        )
      }
    }
  }

  private fun updateSettings(block: AiSettings.() -> AiSettings) {
    updateState { it.copy(aiSettings = it.aiSettings.block(), settingsMessage = null) }
  }

  private fun testAiConnection() {
    val settings = state().aiSettings
    val message = if (!settings.aiEnabled) {
      SettingsMessage.EnableAiFirst
    } else if (settings.apiKey.isBlank() || settings.baseUrl.isBlank()) {
      SettingsMessage.MissingApiConfig
    } else if (settings.modelName.isBlank()) {
      SettingsMessage.MissingModel
    } else {
      SettingsMessage.ConnectionAvailable
    }
    updateState { it.copy(settingsMessage = message) }
  }
}
