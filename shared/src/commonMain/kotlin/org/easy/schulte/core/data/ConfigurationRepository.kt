package org.easy.schulte.core.data

import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.MarkMode

internal interface ConfigurationRepository : FeatureStateRepository {
  suspend fun selectGrid(spec: GridSpec)
  suspend fun selectAgeGroup(ageGroup: AgeGroup)
  suspend fun selectMarkMode(markMode: MarkMode)
}
