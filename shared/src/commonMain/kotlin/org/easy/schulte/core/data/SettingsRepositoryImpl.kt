package org.easy.schulte.core.data

import kotlinx.coroutines.flow.update
import org.easy.schulte.core.model.ai.AiSettings
import org.easy.schulte.core.model.ai.enums.AiAnalysisState
import org.easy.schulte.core.model.settings.enums.SettingsMessage
import org.easy.schulte.core.model.training.enums.MarkMode

internal class SettingsRepositoryImpl(
  private val sharedState: SchulteSharedState,
) : SettingsRepository,
  FeatureStateRepository by sharedState {
  override fun updateAiSettings(block: AiSettings.() -> AiSettings) {
    val nextConfiguration = sharedState.currentConfiguration().let {
      it.copy(aiSettings = it.aiSettings.block())
    }
    sharedState.configurationStore.updateConfiguration(nextConfiguration)
    sharedState.mutableSettingsState.update { it.copy(settingsMessage = null) }
  }

  override fun updateAiApiKey(value: String) {
    sharedState.aiApiKeyStore.save(value)
    sharedState.mutableSettingsState.update {
      it.copy(apiKeyInput = value, hasApiKey = sharedState.aiApiKeyStore.hasApiKey(), settingsMessage = null)
    }
    sharedState.configurationStore.updateConfiguration(sharedState.currentConfiguration())
  }

  override fun toggleApiKeyVisibility() {
    sharedState.mutableSettingsState.update { it.copy(apiKeyVisible = !it.apiKeyVisible) }
  }

  override fun clearAiSettings() {
    sharedState.aiApiKeyStore.clear()
    sharedState.configurationStore.updateConfiguration(
      sharedState.currentConfiguration().copy(
        aiSettings = AiSettings(aiEnabled = false, baseUrl = "", modelName = "gpt-4o-mini"),
      ),
    )
    sharedState.mutableSettingsState.update {
      it.copy(apiKeyInput = "", hasApiKey = false, settingsMessage = SettingsMessage.AiCleared)
    }
    sharedState.mutableReportState.update {
      it.copy(aiAnalysisState = AiAnalysisState.Idle, aiAnalysis = null)
    }
  }

  override fun saveSettings() {
    val currentConfiguration = sharedState.currentConfiguration()
    val nextConfiguration = currentConfiguration.copy(
      selectedMarkMode = if (currentConfiguration.aiSettings.assistedMarkingEnabled) {
        MarkMode.AssistedMarking
      } else {
        MarkMode.BriefFeedbackOnly
      },
    )
    sharedState.configurationStore.updateConfiguration(nextConfiguration)
    sharedState.mutableSettingsState.update { it.copy(settingsMessage = SettingsMessage.Saved) }
  }

  override fun setSettingsMessage(message: SettingsMessage) {
    sharedState.mutableSettingsState.update { it.copy(settingsMessage = message) }
  }

  override fun clearSettingsMessage() {
    sharedState.mutableSettingsState.update { it.copy(settingsMessage = null) }
  }

  override fun isAiConfigured(): Boolean {
    val settings = sharedState.currentConfiguration().aiSettings
    return settings.aiEnabled && settings.baseUrl.isNotBlank() && settings.modelName.isNotBlank() &&
      sharedState.aiApiKeyStore.hasApiKey()
  }

  override fun requestClearTrainingRecords() {
    sharedState.mutableSettingsState.update { it.copy(showClearRecordsDialog = true) }
  }

  override fun cancelClearTrainingRecords() {
    sharedState.mutableSettingsState.update { it.copy(showClearRecordsDialog = false) }
  }

  override fun clearTrainingRecords() {
    sharedState.recordStore.clearRecords()
    sharedState.mutableReportState.update { it.copy(progressComparison = null) }
    sharedState.mutableSettingsState.update {
      it.copy(settingsMessage = SettingsMessage.RecordsCleared, showClearRecordsDialog = false)
    }
  }
}
