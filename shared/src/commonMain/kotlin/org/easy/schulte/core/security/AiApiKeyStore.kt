package org.easy.schulte.core.security

/**
 * Keeps the AI provider credential out of SQLite-backed application configuration.
 */
internal class AiApiKeyStore(
  private val secretStore: SecureSecretStore,
) {
  fun read(): String? = secretStore.read(API_KEY).takeUnless { it.isNullOrBlank() }

  fun save(apiKey: String) {
    if (apiKey.isBlank()) {
      clear()
    } else {
      secretStore.write(API_KEY, apiKey)
    }
  }

  fun hasApiKey(): Boolean = read() != null

  fun clear() {
    secretStore.remove(API_KEY)
  }

  private companion object {
    const val API_KEY = "ai_provider_api_key"
  }
}
