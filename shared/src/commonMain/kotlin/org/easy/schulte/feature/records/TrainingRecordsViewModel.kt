package org.easy.schulte.feature.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.easy.schulte.core.data.SchulteRepository

internal class TrainingRecordsViewModel(
  private val repository: SchulteRepository,
) : ViewModel() {
  val state = repository.state

  private val _events = Channel<TrainingRecordsEvent>()
  val events = _events.receiveAsFlow()

  fun onAction(action: TrainingRecordsAction) {
    when (action) {
      TrainingRecordsAction.Back -> sendEvent(TrainingRecordsEvent.Back)
      TrainingRecordsAction.StartTraining -> sendEvent(TrainingRecordsEvent.StartTraining)
      is TrainingRecordsAction.SelectGridFilter -> repository.selectRecordGridFilter(action.filter)
      is TrainingRecordsAction.SelectModeFilter -> repository.selectRecordModeFilter(action.filter)
      is TrainingRecordsAction.SelectTimeFilter -> repository.selectRecordTimeFilter(action.filter)
    }
  }

  private fun sendEvent(event: TrainingRecordsEvent) {
    viewModelScope.launch {
      _events.send(event)
    }
  }
}
