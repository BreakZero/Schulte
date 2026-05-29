package org.easy.schulte.feature.advice

internal sealed interface AdviceEvent {
  data object RestartTraining : AdviceEvent
  data object BackToReport : AdviceEvent
  data object AiAnalysisCompleted : AdviceEvent
}
