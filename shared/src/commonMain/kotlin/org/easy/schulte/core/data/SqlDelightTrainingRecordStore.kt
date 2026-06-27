package org.easy.schulte.core.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.easy.schulte.core.model.account.UserAccount
import org.easy.schulte.core.model.records.TrainingRecord
import org.easy.schulte.core.model.records.enums.ImprovementStatus
import org.easy.schulte.core.model.report.enums.ScoreLevel
import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.LayoutMode
import org.easy.schulte.core.model.training.enums.MarkMode
import org.easy.schulte.db.Training_record
import org.easy.schulte.db.User_account

internal class SqlDelightTrainingRecordStore(
  databaseProvider: SchulteDatabaseProvider,
) : TrainingRecordStore {
  private val database = databaseProvider.database
  private val queries = database.schulteDatabaseQueries

  override fun observeAllRecords(): Flow<List<TrainingRecord>> = queries
    .selectAll()
    .asFlow()
    .mapToList(Dispatchers.IO)
    .map { rows -> rows.map(::mapRecord) }

  override fun getAllRecords(): List<TrainingRecord> = queries.selectAll().executeAsList().map(::mapRecord)

  override fun getRecordsForConditions(
    gridSize: Int,
    ageGroupName: String,
    markModeName: String,
    layoutModeName: String,
  ): List<TrainingRecord> = queries
    .selectByConditions(gridSize.toLong(), ageGroupName, markModeName, layoutModeName)
    .executeAsList()
    .map(::mapRecord)

  override fun insertRecord(record: TrainingRecord) {
    queries.transaction {
      queries.insertRecord(
        id = record.id,
        owner_user_id = record.ownerUserId,
        created_at = record.createdAt,
        grid_size = record.gridSpec.size.toLong(),
        age_group = record.ageGroup.name,
        mark_mode = record.markMode.name,
        layout_mode = record.layoutMode.name,
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

  override fun observeAccounts(): Flow<List<UserAccount>> = queries
    .selectAccounts()
    .asFlow()
    .mapToList(Dispatchers.IO)
    .map { rows -> rows.map(::mapAccount) }

  override fun getAccounts(): List<UserAccount> = queries.selectAccounts().executeAsList().map(::mapAccount)

  override fun insertAccount(account: UserAccount) {
    queries.insertAccount(
      user_id = account.userId,
      register_id = account.registerId,
      nickname = account.nickname,
      password = account.password,
      gender = account.gender.name,
      created_at = account.createdAt,
    )
  }

  override fun updateAccountProfile(account: UserAccount) {
    queries.updateAccountProfile(
      nickname = account.nickname,
      gender = account.gender.name,
      user_id = account.userId,
    )
  }

  override fun updateUnownedRecordsOwner(userId: String) {
    queries.updateUnownedRecordsOwner(userId)
  }

  private fun mapRecord(row: Training_record): TrainingRecord = TrainingRecord(
    id = row.id,
    ownerUserId = row.owner_user_id,
    createdAt = row.created_at,
    gridSpec = GridSpec.entries.first { it.size == row.grid_size.toInt() },
    ageGroup = AgeGroup.valueOf(row.age_group),
    markMode = MarkMode.valueOf(row.mark_mode),
    layoutMode = LayoutMode.valueOf(row.layout_mode),
    elapsedTimeMillis = row.elapsed_time_millis,
    errorCount = row.error_count.toInt(),
    scoreLevel = ScoreLevel.valueOf(row.score_level),
    isPersonalBest = row.is_personal_best == 1L,
    previousRecordId = row.previous_record_id,
    improvementStatus = ImprovementStatus.valueOf(row.improvement_status),
    timeDeltaMillis = row.time_delta_millis,
    errorDelta = row.error_delta?.toInt(),
  )

  private fun mapAccount(row: User_account): UserAccount = UserAccount(
    userId = row.user_id,
    registerId = row.register_id,
    nickname = row.nickname,
    password = row.password,
    gender = org.easy.schulte.core.model.account.enums.Gender.valueOf(row.gender),
    createdAt = row.created_at,
  )
}

private const val MAX_RECORD_COUNT = 1000L
