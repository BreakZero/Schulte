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
