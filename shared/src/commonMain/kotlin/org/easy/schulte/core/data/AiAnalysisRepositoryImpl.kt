package org.easy.schulte.core.data

import kotlinx.coroutines.flow.update
import org.easy.schulte.core.model.ai.AiAnalysis
import org.easy.schulte.core.model.ai.enums.AiAnalysisState

internal class AiAnalysisRepositoryImpl(
  private val sharedState: SchulteSharedState,
) : AiAnalysisRepository,
  FeatureStateRepository by sharedState {
  override suspend fun isAiConfigured(): Boolean {
    val settings = sharedState.currentConfiguration().aiSettings
    return settings.aiEnabled && settings.baseUrl.isNotBlank() && settings.modelName.isNotBlank() &&
      sharedState.aiApiKeyStore.hasApiKey()
  }

  override fun markAiAnalysisNeedsSettings() {
    sharedState.mutableReportState.update { it.copy(aiAnalysisState = AiAnalysisState.NeedsSettings) }
  }

  override fun markAiAnalysisLoading() {
    sharedState.mutableReportState.update { it.copy(aiAnalysisState = AiAnalysisState.Loading) }
  }

  override fun setAiAnalysis(analysis: AiAnalysis) {
    sharedState.mutableReportState.update {
      it.copy(aiAnalysisState = AiAnalysisState.Success, aiAnalysis = analysis)
    }
  }
}
