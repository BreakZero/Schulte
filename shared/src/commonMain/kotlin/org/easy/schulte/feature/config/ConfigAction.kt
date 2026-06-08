package org.easy.schulte.feature.config

import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.MarkMode

sealed interface ConfigAction {
  data class SelectGrid(val spec: GridSpec) : ConfigAction
  data class SelectAgeGroup(val ageGroup: AgeGroup) : ConfigAction
  data class SelectMarkMode(val markMode: MarkMode) : ConfigAction
  data object StartTraining : ConfigAction
  data object OpenRecords : ConfigAction
  data object OpenSettings : ConfigAction
}
