package org.easy.schulte.di

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.easy.schulte.core.data.AccountRepository
import org.easy.schulte.core.data.AiAnalysisRepository
import org.easy.schulte.core.data.ConfigurationRepository
import org.easy.schulte.core.data.DatabaseDriverFactory
import org.easy.schulte.core.data.SettingsRepository
import org.easy.schulte.core.data.TrainingRecordsRepository
import org.easy.schulte.core.data.TrainingRepository
import org.easy.schulte.core.security.SecureSecretStore
import org.easy.schulte.db.SchulteDatabase
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class RepositoryCompositionTest {
  @Test
  fun featureRepositoryBindingsResolveToDistinctImplementations() {
    val koin = repositoryKoin().koin

    val repositories = listOf<Any>(
      koin.get<ConfigurationRepository>(),
      koin.get<TrainingRepository>(),
      koin.get<SettingsRepository>(),
      koin.get<TrainingRecordsRepository>(),
      koin.get<AccountRepository>(),
      koin.get<AiAnalysisRepository>(),
    )

    assertEquals(6, repositories.map { it::class }.toSet().size)
    repositories.zipWithNext().forEach { (first, second) ->
      assertNotSame(first, second)
    }
  }

  @Test
  fun featureRepositoriesPreserveAccountSettingsAndTrainingStateAtTheirBoundaries() {
    val koin = repositoryKoin().koin
    val accountRepository = koin.get<AccountRepository>()
    val settingsRepository = koin.get<SettingsRepository>()
    val trainingRepository = koin.get<TrainingRepository>()

    accountRepository.updateLoginRegisterId("  player-42  ")
    settingsRepository.updateAiApiKey("secure-api-key")
    trainingRepository.startTraining(listOf(3, 1, 2))

    assertEquals("player-42", accountRepository.currentAccountState().accountForm.registerId)
    assertTrue(settingsRepository.currentSettingsState().hasApiKey)
    assertEquals(listOf(3, 1, 2), trainingRepository.currentTrainingState().numbers)
    assertEquals(1, trainingRepository.currentTrainingState().currentTarget)
  }

  private fun repositoryKoin() = koinApplication {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    SchulteDatabase.Schema.create(driver)
    modules(appModule(FixedDriverFactory(driver), FakeSecretStore()))
  }
}

private class FixedDriverFactory(
  private val driver: JdbcSqliteDriver,
) : DatabaseDriverFactory {
  override fun createDriver() = driver
}

private class FakeSecretStore : SecureSecretStore {
  private val values = mutableMapOf<String, String>()

  override fun read(key: String): String? = values[key]

  override fun write(key: String, value: String) {
    values[key] = value
  }

  override fun remove(key: String) {
    values.remove(key)
  }
}
