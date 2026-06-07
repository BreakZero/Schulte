package org.easy.schulte.feature.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.easy.schulte.core.data.SchulteRepository
import org.easy.schulte.state.reportStateIn

internal class ReportViewModel(
  private val repository: SchulteRepository,
) : ViewModel() {
  val state = repository.reportStateIn(viewModelScope)

  private val _events = Channel<ReportEvent>()
  val events = _events.receiveAsFlow()

  fun onAction(action: ReportAction) {
    when (action) {
      ReportAction.RestartTraining -> sendEvent(ReportEvent.RestartTraining)

      ReportAction.BackToConfig -> {
        repository.exitTraining()
        sendEvent(ReportEvent.BackToConfig)
      }

      ReportAction.OpenSettings -> {
        repository.clearSettingsMessage()
        sendEvent(ReportEvent.OpenSettings)
      }

      ReportAction.OpenProfile -> sendEvent(ReportEvent.OpenProfile)

      ReportAction.OpenRecords -> sendEvent(ReportEvent.OpenRecords)

      ReportAction.GenerateAiAnalysis -> {
        if (repository.currentState().aiSettings.isConfigured) {
          sendEvent(ReportEvent.GenerateAiAnalysis)
        } else {
          repository.markAiAnalysisNeedsSettings()
        }
      }
    }
  }

  private fun sendEvent(event: ReportEvent) {
    viewModelScope.launch {
      _events.send(event)
    }
  }
}
