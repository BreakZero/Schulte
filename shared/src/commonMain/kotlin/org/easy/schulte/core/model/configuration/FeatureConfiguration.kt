package org.easy.schulte.core.model.configuration

import org.easy.schulte.core.model.ai.AiSettings
import org.easy.schulte.core.model.configuration.enums.ConfigurationFeature
import org.easy.schulte.core.model.records.enums.RecordGridFilter
import org.easy.schulte.core.model.records.enums.RecordModeFilter
import org.easy.schulte.core.model.records.enums.RecordTimeFilter
import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.MarkMode

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
