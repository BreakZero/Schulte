package org.easy.schulte.core.model.runtime

import org.easy.schulte.core.model.settings.enums.SettingsMessage

data class SettingsRuntimeState(
  val apiKeyVisible: Boolean = false,
  val settingsMessage: SettingsMessage? = null,
  val showClearRecordsDialog: Boolean = false,
)
