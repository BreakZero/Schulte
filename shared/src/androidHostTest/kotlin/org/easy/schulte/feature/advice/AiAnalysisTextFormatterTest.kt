package org.easy.schulte.feature.advice

import kotlinx.coroutines.test.runTest
import org.easy.schulte.core.model.ai.AiAnalysisRecommendation
import org.easy.schulte.core.model.ai.AiErrorGuidance
import org.easy.schulte.core.model.ai.AiSpeedGuidance
import org.easy.schulte.core.model.ai.AiTrainingSuggestion
import org.easy.schulte.core.model.report.enums.ScoreLevel
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.MarkMode
import org.jetbrains.compose.resources.StringResource
import schulte.shared.generated.resources.*
import kotlin.test.Test
import kotlin.test.assertEquals

class AiAnalysisTextFormatterTest {
  @Test
  fun formatterLocalizesSemanticRecommendationUsingExistingAdviceText() = runTest {
    val recommendation = AiAnalysisRecommendation(
      gridSpec = GridSpec.Five,
      markMode = MarkMode.AssistedMarking,
      elapsedMillis = 6_990,
      errorCount = 2,
      scoreLevel = ScoreLevel.Good,
      speedGuidance = AiSpeedGuidance.Improvable,
      errorGuidance = AiErrorGuidance.ErrorsPresent,
      nextTimeGoalSeconds = 6,
      nextErrorGoalCount = 1,
      recommendedGridSpec = GridSpec.Five,
      recommendedMarkMode = MarkMode.BriefFeedbackOnly,
      suggestions = AiTrainingSuggestion.entries,
    )

    val analysis = AiAnalysisTextFormatter(::testString).format(recommendation)

    assertEquals("你在 5x5 辅助模式 下完成时间为 6.99 秒，错误 2 次，整体表现为良。", analysis.summary)
    assertEquals("当前速度仍有提升空间，建议先稳定扫描节奏，再逐步压缩完成时间。", analysis.speed)
    assertEquals("本次出现 2 次错误，可能来自目标切换时的注意力偏移。建议先锁定下一个目标，再点击。", analysis.errors)
    assertEquals("6 秒以内", analysis.nextTimeGoal)
    assertEquals("1 次以内", analysis.nextErrorGoal)
    assertEquals("5x5 标准模式", analysis.recommendedSpec)
    assertEquals(
      listOf("每天练习 3 组，每组间隔 30 秒", "优先保证准确率，再提升速度", "连续 3 次稳定后，再尝试更高规格"),
      analysis.suggestions,
    )
  }
}

private fun testString(resource: StringResource, formatArgs: List<Any>): String = when (resource) {
  Res.string.grid_spec_five_title -> "5x5"
  Res.string.mark_mode_assisted_title -> "辅助模式"
  Res.string.mark_mode_standard_title -> "标准模式"
  Res.string.score_level_good -> "良"
  Res.string.seconds_format -> "${formatArgs[0]}.${formatArgs[1]} 秒"
  Res.string.local_ai_summary -> "你在 ${formatArgs[0]} ${formatArgs[1]} 下完成时间为 ${formatArgs[2]}，错误 ${formatArgs[3]} 次，整体表现为${formatArgs[4]}。"
  Res.string.local_ai_speed_improvable -> "当前速度仍有提升空间，建议先稳定扫描节奏，再逐步压缩完成时间。"
  Res.string.local_ai_errors_with_count -> "本次出现 ${formatArgs[0]} 次错误，可能来自目标切换时的注意力偏移。建议先锁定下一个目标，再点击。"
  Res.string.local_ai_next_time_goal -> "${formatArgs[0]} 秒以内"
  Res.string.local_ai_next_error_goal_with_count -> "${formatArgs[0]} 次以内"
  Res.string.local_ai_recommended_spec -> "${formatArgs[0]} ${formatArgs[1]}"
  Res.string.local_ai_suggestion_daily -> "每天练习 3 组，每组间隔 30 秒"
  Res.string.local_ai_suggestion_accuracy_first -> "优先保证准确率，再提升速度"
  Res.string.local_ai_suggestion_upgrade -> "连续 3 次稳定后，再尝试更高规格"
  else -> error("Unexpected resource: $resource")
}
