package org.easy.schulte.core.data

import org.easy.schulte.db.SchulteDatabase

internal class SchulteDatabaseProvider(
  driverFactory: DatabaseDriverFactory,
) {
  val database = SchulteDatabase(driverFactory.createDriver())
}
