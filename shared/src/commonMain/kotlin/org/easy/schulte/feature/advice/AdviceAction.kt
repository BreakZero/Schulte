package org.easy.schulte.feature.advice

sealed interface AdviceAction {
  data object RestartTraining : AdviceAction
  data object BackToReport : AdviceAction
}
