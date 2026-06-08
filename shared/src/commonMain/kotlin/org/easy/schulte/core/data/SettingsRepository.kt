package org.easy.schulte.core.data

import org.easy.schulte.core.model.AiSettings
import org.easy.schulte.core.model.SettingsMessage

internal interface SettingsRepository : FeatureStateRepository {
  fun updateAiSettings(block: AiSettings.() -> AiSettings)
  fun toggleApiKeyVisibility()
  fun clearAiSettings()
  fun saveSettings()
  fun setSettingsMessage(message: SettingsMessage)
  fun clearSettingsMessage()
  fun requestClearTrainingRecords()
  fun cancelClearTrainingRecords()
  fun clearTrainingRecords()
}
