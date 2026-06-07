package org.easy.schulte.feature.config

import org.easy.schulte.core.model.AgeGroup
import org.easy.schulte.core.model.GridSpec
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.TrainingRecord

internal data class ConfigState(
  val selectedGrid: GridSpec = GridSpec.Five,
  val selectedAgeGroup: AgeGroup = AgeGroup.Adult,
  val selectedMarkMode: MarkMode = MarkMode.BriefFeedbackOnly,
  val latestRecord: TrainingRecord? = null,
  val isLoggedIn: Boolean = false,
  val hasRecords: Boolean = false,
)
