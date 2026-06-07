package org.easy.schulte.feature.config

internal sealed interface ConfigEvent {
  data object StartTraining : ConfigEvent
  data object OpenRecords : ConfigEvent
  data object OpenSettings : ConfigEvent
}
