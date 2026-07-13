package org.easy.schulte.feature.advice

import org.easy.schulte.core.model.ai.AiAnalysis
import org.easy.schulte.core.model.ai.AiAnalysisRecommendation
import org.easy.schulte.core.model.ai.AiErrorGuidance
import org.easy.schulte.core.model.ai.AiSpeedGuidance
import org.easy.schulte.core.model.ai.AiTrainingSuggestion
import org.easy.schulte.core.model.report.enums.ScoreLevel
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.MarkMode
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import schulte.shared.generated.resources.*

private typealias AiAnalysisStringResolver = suspend (StringResource, List<Any>) -> String

internal class AiAnalysisTextFormatter(
  private val stringResolver: AiAnalysisStringResolver = ::resolveComposeString,
) {
  suspend fun format(recommendation: AiAnalysisRecommendation): AiAnalysis {
    val suggestions = mutableListOf<String>()
    for (suggestion in recommendation.suggestions) {
      suggestions += text(suggestion.textResource)
    }
    return AiAnalysis(
      summary = text(
        Res.string.local_ai_summary,
        text(recommendation.gridSpec.titleResource),
        text(recommendation.markMode.titleResource),
        formatSecondsResource(recommendation.elapsedMillis),
        recommendation.errorCount,
        text(recommendation.scoreLevel.titleResource),
      ),
      speed = text(recommendation.speedGuidance.textResource),
      errors = when (recommendation.errorGuidance) {
        AiErrorGuidance.NoErrors -> text(Res.string.local_ai_errors_none)
        AiErrorGuidance.ErrorsPresent -> text(Res.string.local_ai_errors_with_count, recommendation.errorCount)
      },
      nextTimeGoal = text(Res.string.local_ai_next_time_goal, recommendation.nextTimeGoalSeconds),
      nextErrorGoal = if (recommendation.nextErrorGoalCount == 0) {
        text(Res.string.local_ai_next_error_goal_none)
      } else {
        text(Res.string.local_ai_next_error_goal_with_count, recommendation.nextErrorGoalCount)
      },
      recommendedSpec = text(
        Res.string.local_ai_recommended_spec,
        text(recommendation.recommendedGridSpec.titleResource),
        text(recommendation.recommendedMarkMode.titleResource),
      ),
      suggestions = suggestions,
    )
  }

  private suspend fun text(resource: StringResource, vararg formatArgs: Any): String = stringResolver(resource, formatArgs.toList())

  private suspend fun formatSecondsResource(millis: Long): String {
    val seconds = millis / 1000
    val centis = (millis % 1000) / 10
    return text(Res.string.seconds_format, seconds, centis.twoDigits())
  }
}

private suspend fun resolveComposeString(resource: StringResource, formatArgs: List<Any>): String = getString(
  resource,
  *formatArgs.toTypedArray(),
)

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

private val AiSpeedGuidance.textResource: StringResource
  get() = when (this) {
    AiSpeedGuidance.Excellent -> Res.string.local_ai_speed_excellent
    AiSpeedGuidance.Improvable -> Res.string.local_ai_speed_improvable
  }

private val AiTrainingSuggestion.textResource: StringResource
  get() = when (this) {
    AiTrainingSuggestion.DailyPractice -> Res.string.local_ai_suggestion_daily
    AiTrainingSuggestion.AccuracyFirst -> Res.string.local_ai_suggestion_accuracy_first
    AiTrainingSuggestion.UpgradeAfterConsistency -> Res.string.local_ai_suggestion_upgrade
  }
