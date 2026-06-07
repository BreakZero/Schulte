package org.easy.schulte.core.data

import app.cash.sqldelight.db.SqlDriver

interface DatabaseDriverFactory {
  fun createDriver(): SqlDriver
}
