package org.easy.schulte.core.domain

import org.easy.schulte.core.model.ai.AiAnalysisRecommendation
import org.easy.schulte.core.model.ai.AiErrorGuidance
import org.easy.schulte.core.model.ai.AiSpeedGuidance
import org.easy.schulte.core.model.ai.AiTrainingSuggestion
import org.easy.schulte.core.model.report.TrainingReport
import org.easy.schulte.core.model.report.enums.ScoreLevel
import org.easy.schulte.core.model.training.enums.MarkMode

internal class AiAnalysisGenerator {
  fun createLocalAiAnalysis(report: TrainingReport): AiAnalysisRecommendation {
    val target = report.nextTargetSeconds ?: report.elapsedSeconds.toInt().coerceAtLeast(1)
    return AiAnalysisRecommendation(
      gridSpec = report.gridSpec,
      markMode = report.markMode,
      elapsedMillis = report.elapsedMillis,
      errorCount = report.errorCount,
      scoreLevel = report.scoreLevel,
      speedGuidance = if (report.scoreLevel == ScoreLevel.Excellent) {
        AiSpeedGuidance.Excellent
      } else {
        AiSpeedGuidance.Improvable
      },
      errorGuidance = if (report.errorCount == 0) {
        AiErrorGuidance.NoErrors
      } else {
        AiErrorGuidance.ErrorsPresent
      },
      nextTimeGoalSeconds = target,
      nextErrorGoalCount = (report.errorCount - 1).coerceAtLeast(0),
      recommendedGridSpec = report.gridSpec,
      recommendedMarkMode = MarkMode.BriefFeedbackOnly,
      suggestions = AiTrainingSuggestion.entries,
    )
  }
}
