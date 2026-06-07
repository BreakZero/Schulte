package org.easy.schulte.core.data

import org.easy.schulte.core.model.AgeGroup
import org.easy.schulte.core.model.GridSpec
import org.easy.schulte.core.model.ImprovementStatus
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.ScoreLevel
import org.easy.schulte.core.model.TrainingRecord
import org.easy.schulte.db.SchulteDatabase
import org.easy.schulte.db.Training_record

internal class SqlDelightTrainingRecordStore(
  driverFactory: DatabaseDriverFactory,
) : TrainingRecordStore {
  private val database = SchulteDatabase(driverFactory.createDriver())
  private val queries = database.schulteDatabaseQueries

  override fun getAllRecords(): List<TrainingRecord> = queries.selectAll().executeAsList().map(::mapRecord)

  override fun getRecordsForConditions(
    gridSize: Int,
    ageGroupName: String,
    markModeName: String,
  ): List<TrainingRecord> = queries
    .selectByConditions(gridSize.toLong(), ageGroupName, markModeName)
    .executeAsList()
    .map(::mapRecord)

  override fun insertRecord(record: TrainingRecord) {
    queries.transaction {
      queries.insertRecord(
        id = record.id,
        created_at = record.createdAt,
        grid_size = record.gridSpec.size.toLong(),
        age_group = record.ageGroup.name,
        mark_mode = record.markMode.name,
        elapsed_time_millis = record.elapsedTimeMillis,
        error_count = record.errorCount.toLong(),
        score_level = record.scoreLevel.name,
        is_personal_best = if (record.isPersonalBest) 1L else 0L,
        previous_record_id = record.previousRecordId,
        improvement_status = record.improvementStatus.name,
        time_delta_millis = record.timeDeltaMillis,
        error_delta = record.errorDelta?.toLong(),
      )
      val overflow = queries.countAll().executeAsOne() - MAX_RECORD_COUNT
      if (overflow > 0) queries.deleteOldest(overflow)
    }
  }

  override fun clearRecords() {
    queries.deleteAll()
  }

  private fun mapRecord(row: Training_record): TrainingRecord = TrainingRecord(
    id = row.id,
    createdAt = row.created_at,
    gridSpec = GridSpec.entries.first { it.size == row.grid_size.toInt() },
    ageGroup = AgeGroup.valueOf(row.age_group),
    markMode = MarkMode.valueOf(row.mark_mode),
    elapsedTimeMillis = row.elapsed_time_millis,
    errorCount = row.error_count.toInt(),
    scoreLevel = ScoreLevel.valueOf(row.score_level),
    isPersonalBest = row.is_personal_best == 1L,
    previousRecordId = row.previous_record_id,
    improvementStatus = ImprovementStatus.valueOf(row.improvement_status),
    timeDeltaMillis = row.time_delta_millis,
    errorDelta = row.error_delta?.toInt(),
  )
}

private const val MAX_RECORD_COUNT = 1000L
