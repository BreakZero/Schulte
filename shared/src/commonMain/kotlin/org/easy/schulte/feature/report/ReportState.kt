package org.easy.schulte.feature.report

import org.easy.schulte.core.model.ai.enums.AiAnalysisState
import org.easy.schulte.core.model.records.ProgressComparison
import org.easy.schulte.core.model.records.TrainingRecordSummary
import org.easy.schulte.core.model.report.TrainingReport

internal data class ReportState(
  val report: TrainingReport? = null,
  val isLoggedIn: Boolean = false,
  val currentUserNickname: String = "",
  val progressComparison: ProgressComparison? = null,
  val recordSummary: TrainingRecordSummary = TrainingRecordSummary(),
  val aiConfigured: Boolean = false,
  val aiAnalysisState: AiAnalysisState = AiAnalysisState.Idle,
)
