package org.easy.schulte.core.data

import org.easy.schulte.core.model.records.TrainingRecord
import org.easy.schulte.core.model.records.enums.ImprovementStatus
import org.easy.schulte.core.model.report.enums.ScoreLevel
import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.LayoutMode
import org.easy.schulte.core.model.training.enums.MarkMode
import kotlin.test.Test
import kotlin.test.assertEquals

class TrainingRecordSummaryTest {
  @Test
  fun trendAndBestUseLatestRecordsLayoutMode() {
    val dynamicLatest = record(
      id = "dynamic-latest",
      createdAt = 3_000L,
      layoutMode = LayoutMode.ShuffleAfterCorrectTap,
      elapsedMillis = 20_000L,
    )
    val staticFaster = record(
      id = "static-faster",
      createdAt = 2_000L,
      layoutMode = LayoutMode.Static,
      elapsedMillis = 5_000L,
    )
    val dynamicPrevious = record(
      id = "dynamic-previous",
      createdAt = 1_000L,
      layoutMode = LayoutMode.ShuffleAfterCorrectTap,
      elapsedMillis = 30_000L,
    )

    val summary = listOf(dynamicLatest, staticFaster, dynamicPrevious).summary(now = 3_000L)

    assertEquals(dynamicLatest, summary.latestRecord)
    assertEquals(dynamicLatest, summary.bestRecord)
    assertEquals(25_000L, summary.recentAverageTimeMillis)
  }
}

private fun record(
  id: String,
  createdAt: Long,
  layoutMode: LayoutMode,
  elapsedMillis: Long,
): TrainingRecord = TrainingRecord(
  id = id,
  ownerUserId = null,
  createdAt = createdAt,
  gridSpec = GridSpec.Five,
  ageGroup = AgeGroup.Adult,
  markMode = MarkMode.BriefFeedbackOnly,
  layoutMode = layoutMode,
  elapsedTimeMillis = elapsedMillis,
  errorCount = 0,
  scoreLevel = if (layoutMode == LayoutMode.Static) ScoreLevel.Excellent else ScoreLevel.Practice,
  isPersonalBest = true,
  previousRecordId = null,
  improvementStatus = ImprovementStatus.FirstRecord,
  timeDeltaMillis = null,
  errorDelta = null,
)
