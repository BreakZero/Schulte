package org.easy.schulte.feature.report

sealed interface ReportAction {
  data object RestartTraining : ReportAction
  data object BackToConfig : ReportAction
  data object OpenSettings : ReportAction
  data object OpenProfile : ReportAction
  data object OpenRecords : ReportAction
  data object GenerateAiAnalysis : ReportAction
}
