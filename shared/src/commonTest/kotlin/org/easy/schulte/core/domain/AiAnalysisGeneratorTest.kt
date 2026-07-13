package org.easy.schulte.core.domain

import org.easy.schulte.core.model.ai.AiErrorGuidance
import org.easy.schulte.core.model.ai.AiSpeedGuidance
import org.easy.schulte.core.model.ai.AiTrainingSuggestion
import org.easy.schulte.core.model.report.TrainingReport
import org.easy.schulte.core.model.report.enums.ScoreLevel
import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.MarkMode
import kotlin.test.Test
import kotlin.test.assertEquals

class AiAnalysisGeneratorTest {
  private val generator = AiAnalysisGenerator()

  @Test
  fun excellentZeroErrorReportProducesTypedMaintainAccuracyRecommendation() {
    val report = TrainingReport(
      gridSpec = GridSpec.Three,
      ageGroup = AgeGroup.Adult,
      markMode = MarkMode.BriefFeedbackOnly,
      elapsedMillis = 4_250,
      errorCount = 0,
      scoreLevel = ScoreLevel.Excellent,
      isOfficialScore = true,
      nextTargetSeconds = 4,
    )

    val recommendation = generator.createLocalAiAnalysis(report)

    assertEquals(GridSpec.Three, recommendation.gridSpec)
    assertEquals(MarkMode.BriefFeedbackOnly, recommendation.markMode)
    assertEquals(4_250, recommendation.elapsedMillis)
    assertEquals(ScoreLevel.Excellent, recommendation.scoreLevel)
    assertEquals(AiSpeedGuidance.Excellent, recommendation.speedGuidance)
    assertEquals(AiErrorGuidance.NoErrors, recommendation.errorGuidance)
    assertEquals(4, recommendation.nextTimeGoalSeconds)
    assertEquals(0, recommendation.nextErrorGoalCount)
    assertEquals(GridSpec.Three, recommendation.recommendedGridSpec)
    assertEquals(MarkMode.BriefFeedbackOnly, recommendation.recommendedMarkMode)
    assertEquals(AiTrainingSuggestion.entries.toList(), recommendation.suggestions)
  }

  @Test
  fun nonExcellentReportWithErrorsProducesTypedImprovementGoals() {
    val report = TrainingReport(
      gridSpec = GridSpec.Five,
      ageGroup = AgeGroup.Teen,
      markMode = MarkMode.AssistedMarking,
      elapsedMillis = 6_990,
      errorCount = 2,
      scoreLevel = ScoreLevel.Good,
      isOfficialScore = false,
      nextTargetSeconds = null,
    )

    val recommendation = generator.createLocalAiAnalysis(report)

    assertEquals(AiSpeedGuidance.Improvable, recommendation.speedGuidance)
    assertEquals(AiErrorGuidance.ErrorsPresent, recommendation.errorGuidance)
    assertEquals(6, recommendation.nextTimeGoalSeconds)
    assertEquals(1, recommendation.nextErrorGoalCount)
    assertEquals(GridSpec.Five, recommendation.recommendedGridSpec)
    assertEquals(MarkMode.BriefFeedbackOnly, recommendation.recommendedMarkMode)
  }
}
