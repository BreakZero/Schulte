package org.easy.schulte.feature.config

import org.easy.schulte.core.model.AgeGroup
import org.easy.schulte.core.model.GridSpec
import org.easy.schulte.core.model.MarkMode

sealed interface ConfigAction {
  data class SelectGrid(val spec: GridSpec) : ConfigAction
  data class SelectAgeGroup(val ageGroup: AgeGroup) : ConfigAction
  data class SelectMarkMode(val markMode: MarkMode) : ConfigAction
  data object StartTraining : ConfigAction
  data object OpenSettings : ConfigAction
}
