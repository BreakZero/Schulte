package org.easy.schulte.core.model.configuration

import org.easy.schulte.core.model.ai.AiSettings
import org.easy.schulte.core.model.records.enums.RecordGridFilter
import org.easy.schulte.core.model.records.enums.RecordModeFilter
import org.easy.schulte.core.model.records.enums.RecordTimeFilter
import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.LayoutMode
import org.easy.schulte.core.model.training.enums.MarkMode

data class AppConfiguration(
  val selectedGrid: GridSpec = GridSpec.Five,
  val selectedAgeGroup: AgeGroup = AgeGroup.Adult,
  val selectedMarkMode: MarkMode = MarkMode.BriefFeedbackOnly,
  val selectedLayoutMode: LayoutMode = LayoutMode.Static,
  val aiSettings: AiSettings = AiSettings(),
  val recordGridFilter: RecordGridFilter = RecordGridFilter.All,
  val recordModeFilter: RecordModeFilter = RecordModeFilter.All,
  val recordTimeFilter: RecordTimeFilter = RecordTimeFilter.All,
)
