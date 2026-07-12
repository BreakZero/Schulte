package org.easy.schulte.core.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.easy.schulte.core.model.configuration.AppConfiguration
import org.easy.schulte.db.SchulteDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SqlDelightConfigurationStoreDispatcherTest {
  @Test
  fun readingConfigurationWaitsForInjectedDatabaseDispatcher() = runTest {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    SchulteDatabase.Schema.create(driver)
    val dispatcher = StandardTestDispatcher(testScheduler)
    val store = SqlDelightConfigurationStore(
      databaseProvider = SchulteDatabaseProvider(TestDatabaseDriverFactory(driver)),
      appDispatchers = AppDispatchers(database = dispatcher),
    )

    val configuration = async(start = CoroutineStart.UNDISPATCHED) { store.getConfiguration() }

    assertFalse(configuration.isCompleted)
    testScheduler.runCurrent()
    assertEquals(AppConfiguration(), configuration.await())
  }
}

private class TestDatabaseDriverFactory(
  private val driver: JdbcSqliteDriver,
) : DatabaseDriverFactory {
  override fun createDriver() = driver
}
