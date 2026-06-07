package org.easy.schulte.feature.settings

sealed interface SettingsAction {
  data object BackFromSettings : SettingsAction
  data object OpenProfile : SettingsAction
  data class ToggleAiEnabled(val enabled: Boolean) : SettingsAction
  data class ToggleAssistSetting(val enabled: Boolean) : SettingsAction
  data class UpdateApiKey(val value: String) : SettingsAction
  data class UpdateBaseUrl(val value: String) : SettingsAction
  data class UpdateModelName(val value: String) : SettingsAction
  data object ToggleApiKeyVisibility : SettingsAction
  data object TestAiConnection : SettingsAction
  data object ClearAiSettings : SettingsAction
  data object RequestClearTrainingRecords : SettingsAction
  data object CancelClearTrainingRecords : SettingsAction
  data object ConfirmClearTrainingRecords : SettingsAction
  data object SaveSettings : SettingsAction
}
