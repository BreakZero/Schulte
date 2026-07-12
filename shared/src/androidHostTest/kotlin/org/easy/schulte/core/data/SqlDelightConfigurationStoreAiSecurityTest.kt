package org.easy.schulte.core.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.easy.schulte.core.model.ai.AiSettings
import org.easy.schulte.core.model.configuration.AppConfiguration
import org.easy.schulte.core.security.AiApiKeyStore
import org.easy.schulte.core.security.SecureSecretStore
import org.easy.schulte.db.SchulteDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class SqlDelightConfigurationStoreAiSecurityTest {
  @Test
  fun savingAiSettingsKeepsApiKeyOutOfConfigurationEntriesAndRestoresItFromSecureStore() {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    SchulteDatabase.Schema.create(driver)
    val databaseProvider = SchulteDatabaseProvider(FixedDriverFactory(driver))
    val store = SqlDelightConfigurationStore(databaseProvider)
    val secretStore = FakeAiSecretStore()
    val apiKeyStore = AiApiKeyStore(secretStore)
    val secret = "ai-api-key-that-must-not-reach-sqlite"

    store.updateConfiguration(
      AppConfiguration(
        aiSettings = AiSettings(
          aiEnabled = true,
          baseUrl = "https://api.example.test",
          modelName = "test-model",
        ),
      ),
    )
    apiKeyStore.save(secret)

    val persistedValues = databaseProvider.database.schulteDatabaseQueries
      .selectFeatureConfigurations("global")
      .executeAsList()
      .map { it.config_value }

    assertFalse(persistedValues.contains(secret))
    assertEquals(secret, apiKeyStore.read())
    assertEquals("https://api.example.test", SqlDelightConfigurationStore(databaseProvider).getConfiguration().aiSettings.baseUrl)
  }

  @Test
  fun missingAiApiKeyIsTreatedAsNotConfigured() {
    val apiKeyStore = AiApiKeyStore(FakeAiSecretStore())

    assertFalse(apiKeyStore.hasApiKey())
    assertNull(apiKeyStore.read())
  }

  @Test
  fun clearingAiApiKeyRemovesSecureValueAndLegacyDatabaseEntry() {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    SchulteDatabase.Schema.create(driver)
    val databaseProvider = SchulteDatabaseProvider(FixedDriverFactory(driver))
    databaseProvider.database.schulteDatabaseQueries.upsertConfiguration(
      feature = "global",
      config_key = "ai_api_key",
      config_value = "legacy-plaintext-api-key",
      updated_at = 1,
    )
    val apiKeyStore = AiApiKeyStore(FakeAiSecretStore())
    apiKeyStore.save("secure-api-key")

    val store = SqlDelightConfigurationStore(databaseProvider)
    apiKeyStore.clear()

    assertNull(apiKeyStore.read())
    assertFalse(
      databaseProvider.database.schulteDatabaseQueries
        .selectFeatureConfigurations("global")
        .executeAsList()
        .any { it.config_key == "ai_api_key" },
    )
    assertFalse(store.getConfiguration().aiSettings.aiEnabled)
  }
}

private class FixedDriverFactory(
  private val driver: JdbcSqliteDriver,
) : DatabaseDriverFactory {
  override fun createDriver() = driver
}

private class FakeAiSecretStore : SecureSecretStore {
  private val values = mutableMapOf<String, String>()

  override fun read(key: String): String? = values[key]

  override fun write(key: String, value: String) {
    values[key] = value
  }

  override fun remove(key: String) {
    values.remove(key)
  }
}
