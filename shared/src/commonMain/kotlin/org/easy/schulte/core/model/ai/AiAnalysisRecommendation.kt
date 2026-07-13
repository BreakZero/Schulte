package org.easy.schulte.core.model.ai

import org.easy.schulte.core.model.report.enums.ScoreLevel
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.MarkMode

/** Semantic local-analysis result. Presentation is responsible for localizing it. */
data class AiAnalysisRecommendation(
  val gridSpec: GridSpec,
  val markMode: MarkMode,
  val elapsedMillis: Long,
  val errorCount: Int,
  val scoreLevel: ScoreLevel,
  val speedGuidance: AiSpeedGuidance,
  val errorGuidance: AiErrorGuidance,
  val nextTimeGoalSeconds: Int,
  val nextErrorGoalCount: Int,
  val recommendedGridSpec: GridSpec,
  val recommendedMarkMode: MarkMode,
  val suggestions: List<AiTrainingSuggestion>,
)

enum class AiSpeedGuidance {
  Excellent,
  Improvable,
}

enum class AiErrorGuidance {
  NoErrors,
  ErrorsPresent,
}

enum class AiTrainingSuggestion {
  DailyPractice,
  AccuracyFirst,
  UpgradeAfterConsistency,
}
