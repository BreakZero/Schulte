package org.easy.schulte.core.model.runtime

import org.easy.schulte.core.model.ai.AiAnalysis
import org.easy.schulte.core.model.ai.enums.AiAnalysisState
import org.easy.schulte.core.model.records.ProgressComparison
import org.easy.schulte.core.model.report.TrainingReport

data class ReportRuntimeState(
  val report: TrainingReport? = null,
  val aiAnalysis: AiAnalysis? = null,
  val aiAnalysisState: AiAnalysisState = AiAnalysisState.Idle,
  val progressComparison: ProgressComparison? = null,
)
