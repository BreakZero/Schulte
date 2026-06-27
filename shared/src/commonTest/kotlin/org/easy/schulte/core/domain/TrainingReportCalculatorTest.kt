package org.easy.schulte.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.easy.schulte.core.model.report.enums.ScoreLevel
import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.LayoutMode
import org.easy.schulte.core.model.training.enums.MarkMode

class TrainingReportCalculatorTest {
  private val calculator = TrainingReportCalculator()

  @Test
  fun staticBriefFeedbackIsOfficialAndUsesRatingThresholds() {
    val report = calculator.createReport(
      input(
        markMode = MarkMode.BriefFeedbackOnly,
        layoutMode = LayoutMode.Static,
      ),
    )

    assertTrue(report.isOfficialScore)
    assertEquals(ScoreLevel.Excellent, report.scoreLevel)
    assertEquals(17, report.nextTargetSeconds)
    assertEquals(LayoutMode.Static, report.layoutMode)
  }

  @Test
  fun staticAssistedMarkingIsPractice() {
    val report = calculator.createReport(
      input(
        markMode = MarkMode.AssistedMarking,
        layoutMode = LayoutMode.Static,
      ),
    )

    assertFalse(report.isOfficialScore)
    assertEquals(ScoreLevel.Practice, report.scoreLevel)
    assertNull(report.nextTargetSeconds)
  }

  @Test
  fun shuffledLayoutIsPracticeEvenWithBriefFeedback() {
    val report = calculator.createReport(
      input(
        markMode = MarkMode.BriefFeedbackOnly,
        layoutMode = LayoutMode.ShuffleAfterCorrectTap,
      ),
    )

    assertFalse(report.isOfficialScore)
    assertEquals(ScoreLevel.Practice, report.scoreLevel)
    assertNull(report.nextTargetSeconds)
    assertEquals(LayoutMode.ShuffleAfterCorrectTap, report.layoutMode)
  }

  private fun input(
    markMode: MarkMode,
    layoutMode: LayoutMode,
  ) = TrainingReportInput(
    gridSpec = GridSpec.Five,
    ageGroup = AgeGroup.Adult,
    markMode = markMode,
    layoutMode = layoutMode,
    elapsedMillis = 18_000L,
    errorCount = 0,
  )
}
