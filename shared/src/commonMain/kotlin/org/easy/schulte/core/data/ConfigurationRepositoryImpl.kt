package org.easy.schulte.core.data

import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.MarkMode

internal class ConfigurationRepositoryImpl(
  private val sharedState: SchulteSharedState,
) : ConfigurationRepository,
  FeatureStateRepository by sharedState {
  override fun selectGrid(spec: GridSpec) {
    updateConfiguration { copy(selectedGrid = spec) }
  }

  override fun selectAgeGroup(ageGroup: AgeGroup) {
    updateConfiguration { copy(selectedAgeGroup = ageGroup) }
  }

  override fun selectMarkMode(markMode: MarkMode) {
    updateConfiguration { copy(selectedMarkMode = markMode) }
  }

  private fun updateConfiguration(block: org.easy.schulte.core.model.configuration.AppConfiguration.() -> org.easy.schulte.core.model.configuration.AppConfiguration) {
    sharedState.configurationStore.updateConfiguration(sharedState.currentConfiguration().block())
  }
}
