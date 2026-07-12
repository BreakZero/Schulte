package org.easy.schulte.feature.advice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.easy.schulte.core.data.AiAnalysisRepository
import org.easy.schulte.core.domain.AiAnalysisGenerator
import org.easy.schulte.state.adviceStateIn

internal class AdviceViewModel(
  private val repository: AiAnalysisRepository,
  private val aiAnalysisGenerator: AiAnalysisGenerator,
) : ViewModel() {
  val state = repository.adviceStateIn(viewModelScope)

  private val _events = Channel<AdviceEvent>()
  val events = _events.receiveAsFlow()

  fun onAction(action: AdviceAction) {
    when (action) {
      AdviceAction.RestartTraining -> sendEvent(AdviceEvent.RestartTraining)
      AdviceAction.BackToReport -> sendEvent(AdviceEvent.BackToReport)
    }
  }

  fun generateAiAnalysis() = viewModelScope.launch {
    val report = repository.currentReportState().report ?: return@launch
    if (!repository.isAiConfigured()) {
      repository.markAiAnalysisNeedsSettings()
      return@launch
    }

    repository.markAiAnalysisLoading()
    delay(700)
    val analysis = aiAnalysisGenerator.createLocalAiAnalysis(report)
    repository.setAiAnalysis(analysis)
    sendEvent(AdviceEvent.AiAnalysisCompleted)
  }

  private fun sendEvent(event: AdviceEvent) {
    viewModelScope.launch {
      _events.send(event)
    }
  }
}
