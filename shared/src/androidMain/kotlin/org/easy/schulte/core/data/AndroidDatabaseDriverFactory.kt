package org.easy.schulte.core.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.easy.schulte.db.SchulteDatabase

class AndroidDatabaseDriverFactory(
  private val context: Context,
) : DatabaseDriverFactory {
  override fun createDriver(): SqlDriver = AndroidSqliteDriver(
    schema = SchulteDatabase.Schema,
    context = context,
    name = "schulte.db",
  )
}
