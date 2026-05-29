package org.easy.schulte.core.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.easy.schulte.core.model.AgeGroup
import org.easy.schulte.core.model.AiAnalysis
import org.easy.schulte.core.model.AiAnalysisState
import org.easy.schulte.core.model.AiSettings
import org.easy.schulte.core.model.CellFeedback
import org.easy.schulte.core.model.GridSpec
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.model.SettingsMessage
import org.easy.schulte.core.model.TrainingReport

internal class InMemorySchulteRepository : SchulteRepository {
  private val mutableState = MutableStateFlow(SchulteState())

  override val state = mutableState.asStateFlow()

  override fun currentState(): SchulteState = state.value

  override fun selectGrid(spec: GridSpec) {
    mutableState.update { it.copy(selectedGrid = spec) }
  }

  override fun selectAgeGroup(ageGroup: AgeGroup) {
    mutableState.update { it.copy(selectedAgeGroup = ageGroup) }
  }

  override fun selectMarkMode(markMode: MarkMode) {
    mutableState.update { it.copy(selectedMarkMode = markMode) }
  }

  override fun clearSettingsMessage() {
    mutableState.update { it.copy(settingsMessage = null) }
  }

  override fun startTraining(numbers: List<Int>) {
    mutableState.update {
      it.copy(
        numbers = numbers,
        currentTarget = 1,
        completedNumbers = emptySet(),
        elapsedMillis = 0L,
        errorCount = 0,
        lastFeedback = null,
        report = null,
        aiAnalysis = null,
        aiAnalysisState = AiAnalysisState.Idle,
      )
    }
  }

  override fun updateElapsedMillis(elapsedMillis: Long) {
    mutableState.update { it.copy(elapsedMillis = elapsedMillis) }
  }

  override fun recordCorrectCell(value: Int, completedNumbers: Set<Int>, nextTarget: Int) {
    mutableState.update {
      it.copy(
        currentTarget = nextTarget,
        completedNumbers = completedNumbers,
        lastFeedback = CellFeedback(value, isCorrect = true),
      )
    }
  }

  override fun recordIncorrectCell(value: Int) {
    mutableState.update {
      it.copy(
        errorCount = it.errorCount + 1,
        lastFeedback = CellFeedback(value, isCorrect = false),
      )
    }
  }

  override fun clearFeedbackIfMatches(value: Int) {
    mutableState.update {
      if (it.lastFeedback?.value == value) it.copy(lastFeedback = null) else it
    }
  }

  override fun finishTraining(report: TrainingReport) {
    mutableState.update {
      it.copy(
        report = report,
        elapsedMillis = report.elapsedMillis,
        lastFeedback = null,
      )
    }
  }

  override fun exitTraining() {
    mutableState.update { it.copy(lastFeedback = null) }
  }

  override fun updateAiSettings(block: AiSettings.() -> AiSettings) {
    mutableState.update { it.copy(aiSettings = it.aiSettings.block(), settingsMessage = null) }
  }

  override fun toggleApiKeyVisibility() {
    mutableState.update { it.copy(apiKeyVisible = !it.apiKeyVisible) }
  }

  override fun clearAiSettings() {
    mutableState.update {
      it.copy(
        aiSettings = it.aiSettings.copy(
          aiEnabled = false,
          apiKey = "",
          baseUrl = "",
          modelName = "gpt-4o-mini",
        ),
        settingsMessage = SettingsMessage.AiCleared,
        aiAnalysisState = AiAnalysisState.Idle,
        aiAnalysis = null,
      )
    }
  }

  override fun saveSettings() {
    mutableState.update {
      it.copy(
        settingsMessage = SettingsMessage.Saved,
        selectedMarkMode = if (it.aiSettings.assistedMarkingEnabled) {
          MarkMode.AssistedMarking
        } else {
          MarkMode.BriefFeedbackOnly
        },
      )
    }
  }

  override fun setSettingsMessage(message: SettingsMessage) {
    mutableState.update { it.copy(settingsMessage = message) }
  }

  override fun markAiAnalysisNeedsSettings() {
    mutableState.update { it.copy(aiAnalysisState = AiAnalysisState.NeedsSettings) }
  }

  override fun markAiAnalysisLoading() {
    mutableState.update { it.copy(aiAnalysisState = AiAnalysisState.Loading) }
  }

  override fun setAiAnalysis(analysis: AiAnalysis) {
    mutableState.update {
      it.copy(
        aiAnalysisState = AiAnalysisState.Success,
        aiAnalysis = analysis,
      )
    }
  }
}
