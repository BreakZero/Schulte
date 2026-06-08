package org.easy.schulte.core.data

import org.easy.schulte.core.model.AiAnalysis

internal interface AiAnalysisRepository : FeatureStateRepository {
  fun markAiAnalysisNeedsSettings()
  fun markAiAnalysisLoading()
  fun setAiAnalysis(analysis: AiAnalysis)
}
