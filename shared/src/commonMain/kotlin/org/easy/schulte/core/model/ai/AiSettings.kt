package org.easy.schulte.core.model.ai

/**
 * Public AI provider settings that are safe to store in SQLite.
 *
 * The provider credential is held separately by [org.easy.schulte.core.security.AiApiKeyStore].
 */
data class AiSettings(
  val assistedMarkingEnabled: Boolean = false,
  val aiEnabled: Boolean = false,
  val baseUrl: String = "",
  val modelName: String = "gpt-4o-mini",
)
