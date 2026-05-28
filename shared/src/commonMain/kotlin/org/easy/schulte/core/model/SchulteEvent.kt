package org.easy.schulte.core.model

sealed interface SchulteEvent {
  data class TrainingCompleted(val report: TrainingReport) : SchulteEvent
  data object AiAnalysisCompleted : SchulteEvent
  data class Navigate(val route: AppRoute, val mode: NavigationMode = NavigationMode.Push) : SchulteEvent
  data object NavigateUp : SchulteEvent
}

enum class NavigationMode {
  Push,
  ReplaceTop,
  ResetToRoot,
}
