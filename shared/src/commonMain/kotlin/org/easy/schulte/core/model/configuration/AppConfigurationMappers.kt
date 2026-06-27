package org.easy.schulte.core.model.configuration

import org.easy.schulte.core.model.configuration.enums.ConfigurationFeature

fun AppConfiguration.toFeatureConfiguration(feature: ConfigurationFeature): FeatureConfiguration = when (feature) {
  ConfigurationFeature.Config,
  ConfigurationFeature.Training,
  -> FeatureConfiguration(
    feature = feature,
    selectedGrid = selectedGrid,
    selectedAgeGroup = selectedAgeGroup,
    selectedMarkMode = selectedMarkMode,
    selectedLayoutMode = selectedLayoutMode,
  )

  ConfigurationFeature.Report,
  ConfigurationFeature.Advice,
  -> FeatureConfiguration(
    feature = feature,
    aiSettings = aiSettings,
  )

  ConfigurationFeature.Settings -> FeatureConfiguration(
    feature = feature,
    selectedGrid = selectedGrid,
    selectedAgeGroup = selectedAgeGroup,
    selectedMarkMode = selectedMarkMode,
    selectedLayoutMode = selectedLayoutMode,
    aiSettings = aiSettings,
  )

  ConfigurationFeature.Records -> FeatureConfiguration(
    feature = feature,
    recordGridFilter = recordGridFilter,
    recordModeFilter = recordModeFilter,
    recordLayoutFilter = recordLayoutFilter,
    recordTimeFilter = recordTimeFilter,
  )

  ConfigurationFeature.Account -> FeatureConfiguration(feature = feature)
}
