package org.easy.schulte.core.model.ai

data class AiAnalysis(
  val summary: String,
  val speed: String,
  val errors: String,
  val nextTimeGoal: String,
  val nextErrorGoal: String,
  val recommendedSpec: String,
  val suggestions: List<String>,
)
