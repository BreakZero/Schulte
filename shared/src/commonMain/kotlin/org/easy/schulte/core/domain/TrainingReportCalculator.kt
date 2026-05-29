package org.easy.schulte.core.domain

import org.easy.schulte.core.model.AgeGroup
import org.easy.schulte.core.model.GridSpec
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.model.ScoreLevel
import org.easy.schulte.core.model.TrainingReport
import kotlin.math.max

internal class TrainingReportCalculator {
  fun createReport(state: SchulteState): TrainingReport {
    val isOfficial = state.selectedMarkMode == MarkMode.BriefFeedbackOnly
    val level = if (isOfficial) {
      scoreLevel(state.selectedGrid, state.ageGroupOrSelected(), state.elapsedMillis)
    } else {
      ScoreLevel.Practice
    }
    return TrainingReport(
      gridSpec = state.selectedGrid,
      ageGroup = state.ageGroupOrSelected(),
      markMode = state.selectedMarkMode,
      elapsedMillis = max(state.elapsedMillis, 10L),
      errorCount = state.errorCount,
      scoreLevel = level,
      isOfficialScore = isOfficial,
      nextTargetSeconds = nextTargetSeconds(state.selectedGrid, state.ageGroupOrSelected(), level),
    )
  }
}

private fun SchulteState.ageGroupOrSelected(): AgeGroup = selectedAgeGroup

private fun scoreLevel(gridSpec: GridSpec, ageGroup: AgeGroup, elapsedMillis: Long): ScoreLevel {
  val seconds = elapsedMillis / 1000.0
  val thresholds = thresholdsFor(gridSpec, ageGroup)
  return when {
    seconds <= thresholds.excellent -> ScoreLevel.Excellent
    seconds <= thresholds.good -> ScoreLevel.Good
    seconds <= thresholds.pass -> ScoreLevel.Pass
    else -> ScoreLevel.Below
  }
}

private data class ScoreThresholds(
  val excellent: Double,
  val good: Double,
  val pass: Double,
)

private fun thresholdsFor(gridSpec: GridSpec, ageGroup: AgeGroup): ScoreThresholds {
  val adultBase = when (gridSpec) {
    GridSpec.Three -> ScoreThresholds(6.0, 8.0, 11.0)
    GridSpec.Four -> ScoreThresholds(11.0, 15.0, 20.0)
    GridSpec.Five -> ScoreThresholds(18.0, 23.0, 29.0)
    GridSpec.Seven -> ScoreThresholds(42.0, 55.0, 70.0)
  }
  val multiplier = when (ageGroup) {
    AgeGroup.Child -> 1.8
    AgeGroup.Junior -> 1.45
    AgeGroup.Teen -> 1.2
    AgeGroup.Adult -> 1.0
  }
  return ScoreThresholds(
    excellent = adultBase.excellent * multiplier,
    good = adultBase.good * multiplier,
    pass = adultBase.pass * multiplier,
  )
}

private fun nextTargetSeconds(gridSpec: GridSpec, ageGroup: AgeGroup, level: ScoreLevel): Int? {
  val thresholds = thresholdsFor(gridSpec, ageGroup)
  return when (level) {
    ScoreLevel.Below -> thresholds.pass.toInt()
    ScoreLevel.Pass -> thresholds.good.toInt()
    ScoreLevel.Good -> thresholds.excellent.toInt()
    ScoreLevel.Excellent -> (thresholds.excellent - 1).toInt().coerceAtLeast(1)
    ScoreLevel.Practice -> null
  }
}
