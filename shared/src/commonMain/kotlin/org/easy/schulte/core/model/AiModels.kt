package org.easy.schulte.core.model

data class AiSettings(
  val assistedMarkingEnabled: Boolean = false,
  val aiEnabled: Boolean = false,
  val apiKey: String = "",
  val baseUrl: String = "",
  val modelName: String = "gpt-4o-mini",
) {
  val isConfigured: Boolean
    get() = aiEnabled && apiKey.isNotBlank() && baseUrl.isNotBlank() && modelName.isNotBlank()
}

data class AiAnalysis(
  val summary: String,
  val speed: String,
  val errors: String,
  val nextTimeGoal: String,
  val nextErrorGoal: String,
  val recommendedSpec: String,
  val suggestions: List<String>,
)

enum class AiAnalysisState {
  Idle,
  Loading,
  Success,
  NeedsSettings,
  Failed,
}
