package org.easy.schulte.core.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.easy.schulte.db.SchulteDatabase

internal class IosDatabaseDriverFactory : DatabaseDriverFactory {
  override fun createDriver(): SqlDriver = NativeSqliteDriver(
    schema = SchulteDatabase.Schema,
    name = "schulte.db",
  )
}
