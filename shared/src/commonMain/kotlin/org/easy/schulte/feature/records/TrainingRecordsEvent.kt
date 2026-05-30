package org.easy.schulte.feature.records

internal sealed interface TrainingRecordsEvent {
  data object Back : TrainingRecordsEvent
  data object StartTraining : TrainingRecordsEvent
}
