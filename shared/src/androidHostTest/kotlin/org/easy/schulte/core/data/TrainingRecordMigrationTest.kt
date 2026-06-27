package org.easy.schulte.core.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlin.test.Test
import kotlin.test.assertEquals
import org.easy.schulte.core.model.training.enums.LayoutMode
import org.easy.schulte.db.SchulteDatabase

class TrainingRecordMigrationTest {
  @Test
  fun migrationThreePreservesLegacyRecordAndDefaultsLayoutToStatic() {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    try {
      driver.execute(
        identifier = null,
        sql = VERSION_TWO_TRAINING_RECORD_SCHEMA,
        parameters = 0,
      )
      driver.execute(
        identifier = null,
        sql = VERSION_TWO_TRAINING_RECORD_INDEX,
        parameters = 0,
      )
      driver.execute(
        identifier = null,
        sql = INSERT_LEGACY_RECORD,
        parameters = 0,
      )

      SchulteDatabase.Schema.migrate(
        driver = driver,
        oldVersion = 3,
        newVersion = 4,
      )

      val records = SchulteDatabase(driver).schulteDatabaseQueries
        .selectByConditions(
          grid_size = 5L,
          age_group = "Adult",
          mark_mode = "BriefFeedbackOnly",
          layout_mode = LayoutMode.Static.name,
        )
        .executeAsList()
      assertEquals(1, records.size)
      assertEquals("legacy-record", records.single().id)
      assertEquals(LayoutMode.Static.name, records.single().layout_mode)
      assertEquals(12_345L, records.single().elapsed_time_millis)
    } finally {
      driver.close()
    }
  }
}

private const val VERSION_TWO_TRAINING_RECORD_SCHEMA = """
  CREATE TABLE training_record (
    id TEXT NOT NULL PRIMARY KEY,
    owner_user_id TEXT,
    created_at INTEGER NOT NULL,
    grid_size INTEGER NOT NULL,
    age_group TEXT NOT NULL,
    mark_mode TEXT NOT NULL,
    elapsed_time_millis INTEGER NOT NULL,
    error_count INTEGER NOT NULL,
    score_level TEXT NOT NULL,
    is_personal_best INTEGER NOT NULL,
    previous_record_id TEXT,
    improvement_status TEXT NOT NULL,
    time_delta_millis INTEGER,
    error_delta INTEGER
  );
"""

private const val VERSION_TWO_TRAINING_RECORD_INDEX = """
  CREATE INDEX training_record_conditions_idx
  ON training_record(grid_size, age_group, mark_mode, created_at);
"""

private const val INSERT_LEGACY_RECORD = """
  INSERT INTO training_record(
    id, owner_user_id, created_at, grid_size, age_group, mark_mode,
    elapsed_time_millis, error_count, score_level, is_personal_best,
    previous_record_id, improvement_status, time_delta_millis, error_delta
  ) VALUES (
    'legacy-record', NULL, 1000, 5, 'Adult', 'BriefFeedbackOnly',
    12345, 0, 'Excellent', 1, NULL, 'FirstRecord', NULL, NULL
  );
"""
