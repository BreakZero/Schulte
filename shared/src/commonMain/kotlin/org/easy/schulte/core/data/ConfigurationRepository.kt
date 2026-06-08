package org.easy.schulte.core.data

import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.MarkMode

internal interface ConfigurationRepository : FeatureStateRepository {
  fun selectGrid(spec: GridSpec)
  fun selectAgeGroup(ageGroup: AgeGroup)
  fun selectMarkMode(markMode: MarkMode)
}
