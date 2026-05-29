package org.easy.schulte.feature.settings

internal sealed interface SettingsEvent {
  data object CloseSettings : SettingsEvent
}
