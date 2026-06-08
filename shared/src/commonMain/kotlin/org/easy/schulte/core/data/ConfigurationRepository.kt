package org.easy.schulte.core.data

import org.easy.schulte.core.model.AgeGroup
import org.easy.schulte.core.model.GridSpec
import org.easy.schulte.core.model.MarkMode

internal interface ConfigurationRepository : FeatureStateRepository {
  fun selectGrid(spec: GridSpec)
  fun selectAgeGroup(ageGroup: AgeGroup)
  fun selectMarkMode(markMode: MarkMode)
}
