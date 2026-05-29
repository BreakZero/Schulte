package org.easy.schulte.core.data

import kotlinx.coroutines.flow.StateFlow
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

internal interface SchulteRepository {
  val state: StateFlow<SchulteState>

  fun currentState(): SchulteState

  fun selectGrid(spec: GridSpec)
  fun selectAgeGroup(ageGroup: AgeGroup)
  fun selectMarkMode(markMode: MarkMode)
  fun clearSettingsMessage()

  fun startTraining(numbers: List<Int>)
  fun updateElapsedMillis(elapsedMillis: Long)
  fun recordCorrectCell(value: Int, completedNumbers: Set<Int>, nextTarget: Int)
  fun recordIncorrectCell(value: Int)
  fun clearFeedbackIfMatches(value: Int)
  fun finishTraining(report: TrainingReport)
  fun exitTraining()

  fun updateAiSettings(block: AiSettings.() -> AiSettings)
  fun toggleApiKeyVisibility()
  fun clearAiSettings()
  fun saveSettings()
  fun setSettingsMessage(message: SettingsMessage)

  fun markAiAnalysisNeedsSettings()
  fun markAiAnalysisLoading()
  fun setAiAnalysis(analysis: AiAnalysis)
}
