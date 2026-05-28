package org.easy.schulte.feature.config

import org.easy.schulte.core.model.SchulteState

internal class ConfigViewModel(
  private val updateState: (((SchulteState) -> SchulteState) -> Unit),
  private val startTraining: () -> Unit,
  private val openSettings: () -> Unit,
) {
  fun onAction(action: ConfigAction) {
    when (action) {
      is ConfigAction.SelectGrid -> updateState { it.copy(selectedGrid = action.spec) }

      is ConfigAction.SelectAgeGroup -> updateState { it.copy(selectedAgeGroup = action.ageGroup) }

      is ConfigAction.SelectMarkMode -> updateState { it.copy(selectedMarkMode = action.markMode) }

      ConfigAction.StartTraining -> startTraining()

      ConfigAction.OpenSettings -> {
        updateState { it.copy(settingsMessage = null) }
        openSettings()
      }
    }
  }
}
