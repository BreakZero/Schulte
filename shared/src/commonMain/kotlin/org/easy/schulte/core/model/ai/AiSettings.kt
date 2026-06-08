package org.easy.schulte.core.model.ai

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
