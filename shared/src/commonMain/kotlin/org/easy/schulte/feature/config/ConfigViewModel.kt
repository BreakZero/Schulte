package org.easy.schulte.feature.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.easy.schulte.core.data.SchulteRepository

internal class ConfigViewModel(
  private val repository: SchulteRepository,
) : ViewModel() {
  val state = repository.state

  private val _events = Channel<ConfigEvent>()
  val events = _events.receiveAsFlow()

  fun onAction(action: ConfigAction) {
    when (action) {
      is ConfigAction.SelectGrid -> repository.selectGrid(action.spec)

      is ConfigAction.SelectAgeGroup -> repository.selectAgeGroup(action.ageGroup)

      is ConfigAction.SelectMarkMode -> repository.selectMarkMode(action.markMode)

      ConfigAction.StartTraining -> sendEvent(ConfigEvent.StartTraining)

      ConfigAction.OpenRecords -> sendEvent(ConfigEvent.OpenRecords)

      ConfigAction.OpenSettings -> {
        repository.clearSettingsMessage()
        sendEvent(ConfigEvent.OpenSettings)
      }
    }
  }

  private fun sendEvent(event: ConfigEvent) {
    viewModelScope.launch {
      _events.send(event)
    }
  }
}
