package org.easy.schulte.core.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlin.test.Test
import kotlin.test.assertEquals
import org.easy.schulte.core.model.training.enums.LayoutMode
import org.easy.schulte.db.SchulteDatabase

class LayoutModeConfigurationPersistenceTest {
  @Test
  fun defaultsToStaticAndRestoresEachSelectedLayoutMode() {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    try {
      SchulteDatabase.Schema.create(driver)
      val provider = SchulteDatabaseProvider(
        driverFactory = object : DatabaseDriverFactory {
          override fun createDriver(): SqlDriver = driver
        },
      )

      val initialStore = SqlDelightConfigurationStore(provider)
      val initialConfiguration = initialStore.getConfiguration()
      assertEquals(LayoutMode.Static, initialConfiguration.selectedLayoutMode)

      initialStore.updateConfiguration(
        initialConfiguration.copy(selectedLayoutMode = LayoutMode.ShuffleAfterCorrectTap),
      )

      val restoredStore = SqlDelightConfigurationStore(provider)
      assertEquals(
        LayoutMode.ShuffleAfterCorrectTap,
        restoredStore.getConfiguration().selectedLayoutMode,
      )

      restoredStore.updateConfiguration(
        restoredStore.getConfiguration().copy(selectedLayoutMode = LayoutMode.Static),
      )

      val restoredStaticStore = SqlDelightConfigurationStore(provider)
      assertEquals(
        LayoutMode.Static,
        restoredStaticStore.getConfiguration().selectedLayoutMode,
      )
    } finally {
      driver.close()
    }
  }
}
