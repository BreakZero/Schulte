package org.easy.schulte.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.easy.schulte.core.data.SettingsRepository
import org.easy.schulte.core.model.ai.AiSettings
import org.easy.schulte.core.model.settings.enums.SettingsMessage
import org.easy.schulte.state.settingsStateIn

internal class SettingsViewModel(
  private val repository: SettingsRepository,
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
      is SettingsAction.UpdateApiKey -> viewModelScope.launch { repository.updateAiApiKey(action.value) }
      is SettingsAction.UpdateBaseUrl -> updateSettings { copy(baseUrl = action.value) }
      is SettingsAction.UpdateModelName -> updateSettings { copy(modelName = action.value) }
      SettingsAction.ToggleApiKeyVisibility -> repository.toggleApiKeyVisibility()
      SettingsAction.TestAiConnection -> testAiConnection()
      SettingsAction.ClearAiSettings -> viewModelScope.launch { repository.clearAiSettings() }
      SettingsAction.RequestClearTrainingRecords -> repository.requestClearTrainingRecords()
      SettingsAction.CancelClearTrainingRecords -> repository.cancelClearTrainingRecords()
      SettingsAction.ConfirmClearTrainingRecords -> viewModelScope.launch { repository.clearTrainingRecords() }
      SettingsAction.SaveSettings -> viewModelScope.launch { repository.saveSettings() }
    }
  }

  private fun updateSettings(block: AiSettings.() -> AiSettings) {
    viewModelScope.launch { repository.updateAiSettings(block) }
  }

  private fun testAiConnection() = viewModelScope.launch {
    val settings = repository.currentConfiguration().aiSettings
    val message = if (!settings.aiEnabled) {
      SettingsMessage.EnableAiFirst
    } else if (!repository.currentSettingsState().hasApiKey || settings.baseUrl.isBlank()) {
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
