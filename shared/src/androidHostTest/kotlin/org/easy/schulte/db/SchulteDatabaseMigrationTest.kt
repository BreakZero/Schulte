package org.easy.schulte.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SchulteDatabaseMigrationTest {
  @Test
  fun migratingVersion3AccountRemovesPasswordAndPreservesNonSecretFields() {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    driver.execute(
      null,
      """
      CREATE TABLE user_account (
        user_id TEXT NOT NULL PRIMARY KEY,
        register_id TEXT NOT NULL UNIQUE,
        nickname TEXT NOT NULL,
        password TEXT NOT NULL,
        gender TEXT NOT NULL,
        created_at INTEGER NOT NULL
      )
      """.trimIndent(),
      0,
    )
    driver.execute(
      null,
      """
      INSERT INTO user_account(user_id, register_id, nickname, password, gender, created_at)
      VALUES ('user-1', 'register-1', 'Ada', 'legacy-plaintext-password', 'Female', 123456789)
      """.trimIndent(),
      0,
    )

    assertEquals(4L, SchulteDatabase.Schema.version)
    SchulteDatabase.Schema.migrate(driver, oldVersion = 3, newVersion = 4)

    val columns = driver.executeQuery(
      null,
      "PRAGMA table_info(user_account)",
      { cursor ->
        QueryResult.Value(
          buildList {
            while (cursor.next().value) add(cursor.getString(1)!!)
          },
        )
      },
      0,
    ).value
    val account = driver.executeQuery(
      null,
      "SELECT user_id, register_id, nickname, gender, created_at FROM user_account",
      { cursor ->
        cursor.next()
        QueryResult.Value(
          listOf(
            cursor.getString(0)!!,
            cursor.getString(1)!!,
            cursor.getString(2)!!,
            cursor.getString(3)!!,
            cursor.getLong(4).toString(),
          ),
        )
      },
      0,
    ).value

    assertEquals(listOf("user_id", "register_id", "nickname", "gender", "created_at"), columns)
    assertEquals(listOf("user-1", "register-1", "Ada", "Female", "123456789"), account)
    assertFalse(columns.contains("password"))
    assertFalse(account.contains("legacy-plaintext-password"))
  }
}
