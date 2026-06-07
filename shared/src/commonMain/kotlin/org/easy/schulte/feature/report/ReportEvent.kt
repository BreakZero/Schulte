package org.easy.schulte.feature.report

internal sealed interface ReportEvent {
  data object RestartTraining : ReportEvent
  data object GenerateAiAnalysis : ReportEvent
  data object BackToConfig : ReportEvent
  data object OpenSettings : ReportEvent
  data object OpenProfile : ReportEvent
  data object OpenRecords : ReportEvent
}
