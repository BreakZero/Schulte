package org.easy.schulte.core.domain

import org.easy.schulte.core.model.AiAnalysis
import org.easy.schulte.core.model.GridSpec
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.ScoreLevel
import org.easy.schulte.core.model.TrainingReport
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import schulte.shared.generated.resources.Res
import schulte.shared.generated.resources.grid_spec_five_title
import schulte.shared.generated.resources.grid_spec_four_title
import schulte.shared.generated.resources.grid_spec_seven_title
import schulte.shared.generated.resources.grid_spec_three_title
import schulte.shared.generated.resources.local_ai_errors_none
import schulte.shared.generated.resources.local_ai_errors_with_count
import schulte.shared.generated.resources.local_ai_next_error_goal_none
import schulte.shared.generated.resources.local_ai_next_error_goal_with_count
import schulte.shared.generated.resources.local_ai_next_time_goal
import schulte.shared.generated.resources.local_ai_recommended_spec
import schulte.shared.generated.resources.local_ai_speed_excellent
import schulte.shared.generated.resources.local_ai_speed_improvable
import schulte.shared.generated.resources.local_ai_suggestion_accuracy_first
import schulte.shared.generated.resources.local_ai_suggestion_daily
import schulte.shared.generated.resources.local_ai_suggestion_upgrade
import schulte.shared.generated.resources.local_ai_summary
import schulte.shared.generated.resources.mark_mode_assisted_title
import schulte.shared.generated.resources.mark_mode_standard_title
import schulte.shared.generated.resources.score_level_below
import schulte.shared.generated.resources.score_level_excellent
import schulte.shared.generated.resources.score_level_good
import schulte.shared.generated.resources.score_level_pass
import schulte.shared.generated.resources.score_level_practice
import schulte.shared.generated.resources.seconds_format

internal suspend fun createLocalAiAnalysis(report: TrainingReport): AiAnalysis {
  val target = report.nextTargetSeconds ?: report.elapsedSeconds.toInt().coerceAtLeast(1)
  return AiAnalysis(
    summary = getString(
      Res.string.local_ai_summary,
      getString(report.gridSpec.titleResource),
      getString(report.markMode.titleResource),
      formatSecondsResource(report.elapsedMillis),
      report.errorCount,
      getString(report.scoreLevel.titleResource),
    ),
    speed = if (report.scoreLevel == ScoreLevel.Excellent) {
      getString(Res.string.local_ai_speed_excellent)
    } else {
      getString(Res.string.local_ai_speed_improvable)
    },
    errors = if (report.errorCount == 0) {
      getString(Res.string.local_ai_errors_none)
    } else {
      getString(Res.string.local_ai_errors_with_count, report.errorCount)
    },
    nextTimeGoal = getString(Res.string.local_ai_next_time_goal, target),
    nextErrorGoal = if (report.errorCount == 0) {
      getString(Res.string.local_ai_next_error_goal_none)
    } else {
      getString(Res.string.local_ai_next_error_goal_with_count, (report.errorCount - 1).coerceAtLeast(0))
    },
    recommendedSpec = getString(
      Res.string.local_ai_recommended_spec,
      getString(report.gridSpec.titleResource),
      getString(MarkMode.BriefFeedbackOnly.titleResource),
    ),
    suggestions = listOf(
      getString(Res.string.local_ai_suggestion_daily),
      getString(Res.string.local_ai_suggestion_accuracy_first),
      getString(Res.string.local_ai_suggestion_upgrade),
    ),
  )
}

private suspend fun formatSecondsResource(millis: Long): String {
  val seconds = millis / 1000
  val centis = (millis % 1000) / 10
  return getString(Res.string.seconds_format, seconds, centis.twoDigits())
}

private fun Long.twoDigits(): String = if (this < 10) "0$this" else toString()

private val GridSpec.titleResource: StringResource
  get() = when (this) {
    GridSpec.Three -> Res.string.grid_spec_three_title
    GridSpec.Four -> Res.string.grid_spec_four_title
    GridSpec.Five -> Res.string.grid_spec_five_title
    GridSpec.Seven -> Res.string.grid_spec_seven_title
  }

private val MarkMode.titleResource: StringResource
  get() = when (this) {
    MarkMode.BriefFeedbackOnly -> Res.string.mark_mode_standard_title
    MarkMode.AssistedMarking -> Res.string.mark_mode_assisted_title
  }

private val ScoreLevel.titleResource: StringResource
  get() = when (this) {
    ScoreLevel.Excellent -> Res.string.score_level_excellent
    ScoreLevel.Good -> Res.string.score_level_good
    ScoreLevel.Pass -> Res.string.score_level_pass
    ScoreLevel.Below -> Res.string.score_level_below
    ScoreLevel.Practice -> Res.string.score_level_practice
  }
