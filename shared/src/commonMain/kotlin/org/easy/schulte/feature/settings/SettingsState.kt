package org.easy.schulte.feature.settings

import org.easy.schulte.core.model.AgeGroup
import org.easy.schulte.core.model.AiSettings
import org.easy.schulte.core.model.GridSpec
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.SettingsMessage

internal data class SettingsState(
  val selectedGrid: GridSpec = GridSpec.Five,
  val selectedAgeGroup: AgeGroup = AgeGroup.Adult,
  val selectedMarkMode: MarkMode = MarkMode.BriefFeedbackOnly,
  val aiSettings: AiSettings = AiSettings(),
  val apiKeyVisible: Boolean = false,
  val settingsMessage: SettingsMessage? = null,
  val showClearRecordsDialog: Boolean = false,
  val totalRecordCount: Int = 0,
  val currentAccountRecordCount: Int = 0,
  val unlinkedLocalRecordCount: Int = 0,
  val isLoggedIn: Boolean = false,
  val currentUserNickname: String = "",
  val currentUserRegisterId: String = "",
)
