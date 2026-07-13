package org.easy.schulte.feature.settings

import org.easy.schulte.core.model.ai.AiSettings
import org.easy.schulte.core.model.settings.enums.SettingsMessage
import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.MarkMode

internal data class SettingsState(
  val selectedGrid: GridSpec = GridSpec.Five,
  val selectedAgeGroup: AgeGroup = AgeGroup.Adult,
  val selectedMarkMode: MarkMode = MarkMode.BriefFeedbackOnly,
  val aiSettings: AiSettings = AiSettings(),
  val apiKeyInput: String = "",
  val hasApiKey: Boolean = false,
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
