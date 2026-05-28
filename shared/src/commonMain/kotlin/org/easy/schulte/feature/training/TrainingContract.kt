package org.easy.schulte.feature.training

sealed interface TrainingAction {
  data class CellClick(val value: Int) : TrainingAction
  data object RestartTraining : TrainingAction
  data object ExitTraining : TrainingAction
}
