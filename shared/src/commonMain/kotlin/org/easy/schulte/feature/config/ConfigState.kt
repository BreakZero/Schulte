package org.easy.schulte.feature.config

import org.easy.schulte.core.model.records.TrainingRecord
import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.MarkMode

internal data class ConfigState(
  val selectedGrid: GridSpec = GridSpec.Five,
  val selectedAgeGroup: AgeGroup = AgeGroup.Adult,
  val selectedMarkMode: MarkMode = MarkMode.BriefFeedbackOnly,
  val latestRecord: TrainingRecord? = null,
  val isLoggedIn: Boolean = false,
  val hasRecords: Boolean = false,
)
