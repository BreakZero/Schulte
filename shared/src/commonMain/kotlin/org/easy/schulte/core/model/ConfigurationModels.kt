package org.easy.schulte.core.model

enum class ConfigurationFeature {
  Config,
  Training,
  Report,
  Advice,
  Settings,
  Records,
  Account,
}

data class AppConfiguration(
  val selectedGrid: GridSpec = GridSpec.Five,
  val selectedAgeGroup: AgeGroup = AgeGroup.Adult,
  val selectedMarkMode: MarkMode = MarkMode.BriefFeedbackOnly,
  val aiSettings: AiSettings = AiSettings(),
  val recordGridFilter: RecordGridFilter = RecordGridFilter.All,
  val recordModeFilter: RecordModeFilter = RecordModeFilter.All,
  val recordTimeFilter: RecordTimeFilter = RecordTimeFilter.All,
)

data class FeatureConfiguration(
  val feature: ConfigurationFeature,
  val selectedGrid: GridSpec? = null,
  val selectedAgeGroup: AgeGroup? = null,
  val selectedMarkMode: MarkMode? = null,
  val aiSettings: AiSettings? = null,
  val recordGridFilter: RecordGridFilter? = null,
  val recordModeFilter: RecordModeFilter? = null,
  val recordTimeFilter: RecordTimeFilter? = null,
)

fun AppConfiguration.toFeatureConfiguration(feature: ConfigurationFeature): FeatureConfiguration = when (feature) {
  ConfigurationFeature.Config,
  ConfigurationFeature.Training,
  -> FeatureConfiguration(
    feature = feature,
    selectedGrid = selectedGrid,
    selectedAgeGroup = selectedAgeGroup,
    selectedMarkMode = selectedMarkMode,
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
    aiSettings = aiSettings,
  )

  ConfigurationFeature.Records -> FeatureConfiguration(
    feature = feature,
    recordGridFilter = recordGridFilter,
    recordModeFilter = recordModeFilter,
    recordTimeFilter = recordTimeFilter,
  )

  ConfigurationFeature.Account -> FeatureConfiguration(feature = feature)
}
