package org.easy.schulte.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.easy.schulte.core.model.ConfigRoute
import org.easy.schulte.core.model.NavigationMode
import org.easy.schulte.core.model.SchulteEvent
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.model.SettingsRoute
import org.easy.schulte.core.model.TrainingRoute
import org.easy.schulte.feature.advice.AdviceAction
import org.easy.schulte.feature.advice.AdviceViewModel
import org.easy.schulte.feature.config.ConfigAction
import org.easy.schulte.feature.config.ConfigViewModel
import org.easy.schulte.feature.report.ReportAction
import org.easy.schulte.feature.report.ReportViewModel
import org.easy.schulte.feature.settings.SettingsAction
import org.easy.schulte.feature.settings.SettingsViewModel
import org.easy.schulte.feature.training.TrainingAction
import org.easy.schulte.feature.training.TrainingViewModel

class SchulteViewModel : ViewModel() {
  private val _state = MutableStateFlow(SchulteState())
  val state = _state.asStateFlow()

  private val _events = Channel<SchulteEvent>()
  val events = _events.receiveAsFlow()

  private val trainingViewModel = TrainingViewModel(
    state = { _state.value },
    updateState = { reducer -> _state.update(reducer) },
    scope = viewModelScope,
    onTrainingCompleted = { report ->
      viewModelScope.launch {
        _events.send(SchulteEvent.TrainingCompleted(report))
      }
    },
    onTrainingStarted = {
      viewModelScope.launch {
        _events.send(SchulteEvent.Navigate(TrainingRoute, NavigationMode.ResetToRoot))
      }
    },
    onTrainingExited = {
      viewModelScope.launch {
        _events.send(SchulteEvent.Navigate(ConfigRoute, NavigationMode.ResetToRoot))
      }
    },
  )

  private val adviceViewModel = AdviceViewModel(
    state = { _state.value },
    updateState = { reducer -> _state.update(reducer) },
    scope = viewModelScope,
    restartTraining = trainingViewModel::startTraining,
    backToReport = {
      viewModelScope.launch {
        _events.send(SchulteEvent.NavigateUp)
      }
    },
    onAiAnalysisCompleted = {
      viewModelScope.launch {
        _events.send(SchulteEvent.AiAnalysisCompleted)
      }
    },
  )

  private val configViewModel = ConfigViewModel(
    updateState = { reducer -> _state.update(reducer) },
    startTraining = trainingViewModel::startTraining,
    openSettings = {
      viewModelScope.launch {
        _events.send(SchulteEvent.Navigate(SettingsRoute))
      }
    },
  )

  private val reportViewModel = ReportViewModel(
    state = { _state.value },
    updateState = { reducer -> _state.update(reducer) },
    restartTraining = trainingViewModel::startTraining,
    generateAiAnalysis = adviceViewModel::generateAiAnalysis,
    backToConfig = {
      viewModelScope.launch {
        _events.send(SchulteEvent.Navigate(ConfigRoute, NavigationMode.ResetToRoot))
      }
    },
    openSettings = {
      viewModelScope.launch {
        _events.send(SchulteEvent.Navigate(SettingsRoute))
      }
    },
  )

  private val settingsViewModel = SettingsViewModel(
    state = { _state.value },
    updateState = { reducer -> _state.update(reducer) },
    closeSettings = {
      viewModelScope.launch {
        _events.send(SchulteEvent.NavigateUp)
      }
    },
  )

  fun onConfigAction(action: ConfigAction) {
    configViewModel.onAction(action)
  }

  fun onTrainingAction(action: TrainingAction) {
    trainingViewModel.onAction(action)
  }

  fun onReportAction(action: ReportAction) {
    reportViewModel.onAction(action)
  }

  fun onAdviceAction(action: AdviceAction) {
    adviceViewModel.onAction(action)
  }

  fun onSettingsAction(action: SettingsAction) {
    settingsViewModel.onAction(action)
  }
}
