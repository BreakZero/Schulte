package org.easy.schulte.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.easy.schulte.core.data.SchulteRepository
import org.easy.schulte.core.model.AiSettings
import org.easy.schulte.core.model.SettingsMessage
import org.easy.schulte.state.settingsStateIn

internal class SettingsViewModel(
  private val repository: SchulteRepository,
) : ViewModel() {
  val state = repository.settingsStateIn(viewModelScope)

  private val _events = Channel<SettingsEvent>()
  val events = _events.receiveAsFlow()

  fun onAction(action: SettingsAction) {
    when (action) {
      SettingsAction.BackFromSettings -> sendEvent(SettingsEvent.CloseSettings)
      SettingsAction.OpenProfile -> sendEvent(SettingsEvent.OpenProfile)
      is SettingsAction.ToggleAiEnabled -> updateSettings { copy(aiEnabled = action.enabled) }
      is SettingsAction.ToggleAssistSetting -> updateSettings { copy(assistedMarkingEnabled = action.enabled) }
      is SettingsAction.UpdateApiKey -> updateSettings { copy(apiKey = action.value) }
      is SettingsAction.UpdateBaseUrl -> updateSettings { copy(baseUrl = action.value) }
      is SettingsAction.UpdateModelName -> updateSettings { copy(modelName = action.value) }
      SettingsAction.ToggleApiKeyVisibility -> repository.toggleApiKeyVisibility()
      SettingsAction.TestAiConnection -> testAiConnection()
      SettingsAction.ClearAiSettings -> repository.clearAiSettings()
      SettingsAction.RequestClearTrainingRecords -> repository.requestClearTrainingRecords()
      SettingsAction.CancelClearTrainingRecords -> repository.cancelClearTrainingRecords()
      SettingsAction.ConfirmClearTrainingRecords -> repository.clearTrainingRecords()
      SettingsAction.SaveSettings -> repository.saveSettings()
    }
  }

  private fun updateSettings(block: AiSettings.() -> AiSettings) {
    repository.updateAiSettings(block)
  }

  private fun testAiConnection() {
    val settings = repository.currentConfiguration().aiSettings
    val message = if (!settings.aiEnabled) {
      SettingsMessage.EnableAiFirst
    } else if (settings.apiKey.isBlank() || settings.baseUrl.isBlank()) {
      SettingsMessage.MissingApiConfig
    } else if (settings.modelName.isBlank()) {
      SettingsMessage.MissingModel
    } else {
      SettingsMessage.ConnectionAvailable
    }
    repository.setSettingsMessage(message)
  }

  private fun sendEvent(event: SettingsEvent) {
    viewModelScope.launch {
      _events.send(event)
    }
  }
}
