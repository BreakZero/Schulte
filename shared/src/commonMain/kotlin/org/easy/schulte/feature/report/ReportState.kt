package org.easy.schulte.feature.report

import org.easy.schulte.core.model.AiAnalysisState
import org.easy.schulte.core.model.ProgressComparison
import org.easy.schulte.core.model.TrainingRecordSummary
import org.easy.schulte.core.model.TrainingReport

internal data class ReportState(
  val report: TrainingReport? = null,
  val isLoggedIn: Boolean = false,
  val currentUserNickname: String = "",
  val progressComparison: ProgressComparison? = null,
  val recordSummary: TrainingRecordSummary = TrainingRecordSummary(),
  val aiConfigured: Boolean = false,
  val aiAnalysisState: AiAnalysisState = AiAnalysisState.Idle,
)
