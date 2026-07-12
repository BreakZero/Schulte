package org.easy.schulte.feature.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.easy.schulte.core.data.AiAnalysisRepository
import org.easy.schulte.core.data.SettingsRepository
import org.easy.schulte.core.data.TrainingRepository
import org.easy.schulte.state.reportStateIn

internal class ReportViewModel(
  private val trainingRepository: TrainingRepository,
  private val settingsRepository: SettingsRepository,
  private val aiAnalysisRepository: AiAnalysisRepository,
) : ViewModel() {
  val state = trainingRepository.reportStateIn(viewModelScope)

  private val _events = Channel<ReportEvent>()
  val events = _events.receiveAsFlow()

  fun onAction(action: ReportAction) {
    when (action) {
      ReportAction.RestartTraining -> sendEvent(ReportEvent.RestartTraining)

      ReportAction.BackToConfig -> {
        viewModelScope.launch {
          trainingRepository.exitTraining()
          sendEvent(ReportEvent.BackToConfig)
        }
      }

      ReportAction.OpenSettings -> {
        settingsRepository.clearSettingsMessage()
        sendEvent(ReportEvent.OpenSettings)
      }

      ReportAction.OpenProfile -> sendEvent(ReportEvent.OpenProfile)

      ReportAction.OpenRecords -> sendEvent(ReportEvent.OpenRecords)

      ReportAction.GenerateAiAnalysis -> viewModelScope.launch {
        if (aiAnalysisRepository.isAiConfigured()) {
          sendEvent(ReportEvent.GenerateAiAnalysis)
        } else {
          aiAnalysisRepository.markAiAnalysisNeedsSettings()
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
