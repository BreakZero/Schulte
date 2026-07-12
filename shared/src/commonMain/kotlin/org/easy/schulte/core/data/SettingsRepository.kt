package org.easy.schulte.core.data

import org.easy.schulte.core.model.ai.AiSettings
import org.easy.schulte.core.model.settings.enums.SettingsMessage

internal interface SettingsRepository : FeatureStateRepository {
  suspend fun updateAiSettings(block: AiSettings.() -> AiSettings)
  suspend fun updateAiApiKey(value: String)
  fun toggleApiKeyVisibility()
  suspend fun clearAiSettings()
  suspend fun saveSettings()
  fun setSettingsMessage(message: SettingsMessage)
  fun clearSettingsMessage()
  suspend fun isAiConfigured(): Boolean
  fun requestClearTrainingRecords()
  fun cancelClearTrainingRecords()
  suspend fun clearTrainingRecords()
}
