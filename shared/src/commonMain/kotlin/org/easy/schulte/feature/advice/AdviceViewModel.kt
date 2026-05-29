package org.easy.schulte.feature.advice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.easy.schulte.core.data.SchulteRepository
import org.easy.schulte.core.domain.AiAnalysisGenerator

internal class AdviceViewModel(
  private val repository: SchulteRepository,
  private val aiAnalysisGenerator: AiAnalysisGenerator,
) : ViewModel() {
  val state = repository.state

  private val _events = Channel<AdviceEvent>()
  val events = _events.receiveAsFlow()

  fun onAction(action: AdviceAction) {
    when (action) {
      AdviceAction.RestartTraining -> sendEvent(AdviceEvent.RestartTraining)
      AdviceAction.BackToReport -> sendEvent(AdviceEvent.BackToReport)
    }
  }

  fun generateAiAnalysis() {
    val report = repository.currentState().report ?: return
    val settings = repository.currentState().aiSettings
    if (!settings.isConfigured) {
      repository.markAiAnalysisNeedsSettings()
      return
    }

    repository.markAiAnalysisLoading()
    viewModelScope.launch {
      delay(700)
      val analysis = aiAnalysisGenerator.createLocalAiAnalysis(report)
      repository.setAiAnalysis(analysis)
      sendEvent(AdviceEvent.AiAnalysisCompleted)
    }
  }

  private fun sendEvent(event: AdviceEvent) {
    viewModelScope.launch {
      _events.send(event)
    }
  }
}
