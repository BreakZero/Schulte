package org.easy.schulte.core.data

import org.easy.schulte.core.model.ai.AiSettings
import org.easy.schulte.core.model.settings.enums.SettingsMessage

internal interface SettingsRepository : FeatureStateRepository {
  fun updateAiSettings(block: AiSettings.() -> AiSettings)
  fun updateAiApiKey(value: String)
  fun toggleApiKeyVisibility()
  fun clearAiSettings()
  fun saveSettings()
  fun setSettingsMessage(message: SettingsMessage)
  fun clearSettingsMessage()
  fun isAiConfigured(): Boolean
  fun requestClearTrainingRecords()
  fun cancelClearTrainingRecords()
  fun clearTrainingRecords()
}
