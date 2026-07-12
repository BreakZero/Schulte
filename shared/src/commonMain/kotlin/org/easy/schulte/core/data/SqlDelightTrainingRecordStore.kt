package org.easy.schulte.core.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.easy.schulte.core.model.account.UserAccount
import org.easy.schulte.core.model.records.TrainingRecord
import org.easy.schulte.core.model.records.enums.ImprovementStatus
import org.easy.schulte.core.model.report.enums.ScoreLevel
import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.MarkMode
import org.easy.schulte.db.Training_record
import org.easy.schulte.db.User_account

internal class SqlDelightTrainingRecordStore(
  databaseProvider: SchulteDatabaseProvider,
  private val appDispatchers: AppDispatchers,
) : TrainingRecordStore {
  private val database = databaseProvider.database
  private val queries = database.schulteDatabaseQueries

  override fun observeAllRecords(): Flow<List<TrainingRecord>> = queries.selectAll().asFlow()
    .mapToList(appDispatchers.database).map { rows -> rows.map(::mapRecord) }

  override suspend fun getAllRecords(): List<TrainingRecord> = withContext(appDispatchers.database) {
    queries.selectAll().executeAsList().map(::mapRecord)
  }

  override suspend fun getRecordsForConditions(
    gridSize: Int,
    ageGroupName: String,
    markModeName: String,
  ): List<TrainingRecord> = withContext(appDispatchers.database) {
    queries.selectByConditions(gridSize.toLong(), ageGroupName, markModeName).executeAsList().map(::mapRecord)
  }

  override suspend fun insertRecord(record: TrainingRecord) = withContext(appDispatchers.database) {
    queries.transaction {
      queries.insertRecord(
        id = record.id, owner_user_id = record.ownerUserId, created_at = record.createdAt,
        grid_size = record.gridSpec.size.toLong(), age_group = record.ageGroup.name, mark_mode = record.markMode.name,
        elapsed_time_millis = record.elapsedTimeMillis, error_count = record.errorCount.toLong(), score_level = record.scoreLevel.name,
        is_personal_best = if (record.isPersonalBest) 1L else 0L, previous_record_id = record.previousRecordId,
        improvement_status = record.improvementStatus.name, time_delta_millis = record.timeDeltaMillis,
        error_delta = record.errorDelta?.toLong(),
      )
      val overflow = queries.countAll().executeAsOne() - MAX_RECORD_COUNT
      if (overflow > 0) queries.deleteOldest(overflow)
    }
  }

  override suspend fun clearRecords() = withContext(appDispatchers.database) {
    queries.deleteAll()
    Unit
  }

  override fun observeAccounts(): Flow<List<UserAccount>> = queries.selectAccounts().asFlow()
    .mapToList(appDispatchers.database).map { rows -> rows.map(::mapAccount) }

  override suspend fun getAccounts(): List<UserAccount> = withContext(appDispatchers.database) {
    queries.selectAccounts().executeAsList().map(::mapAccount)
  }

  override suspend fun insertAccount(account: UserAccount) = withContext(appDispatchers.database) {
    queries.insertAccount(account.userId, account.registerId, account.nickname, account.gender.name, account.createdAt)
    Unit
  }

  override suspend fun updateAccountProfile(account: UserAccount) = withContext(appDispatchers.database) {
    queries.updateAccountProfile(account.nickname, account.gender.name, account.userId)
    Unit
  }

  override suspend fun updateUnownedRecordsOwner(userId: String) = withContext(appDispatchers.database) {
    queries.updateUnownedRecordsOwner(userId)
    Unit
  }

  private fun mapRecord(row: Training_record): TrainingRecord = TrainingRecord(
    id = row.id, ownerUserId = row.owner_user_id, createdAt = row.created_at,
    gridSpec = GridSpec.entries.first { it.size == row.grid_size.toInt() }, ageGroup = AgeGroup.valueOf(row.age_group),
    markMode = MarkMode.valueOf(row.mark_mode), elapsedTimeMillis = row.elapsed_time_millis, errorCount = row.error_count.toInt(),
    scoreLevel = ScoreLevel.valueOf(row.score_level), isPersonalBest = row.is_personal_best == 1L,
    previousRecordId = row.previous_record_id, improvementStatus = ImprovementStatus.valueOf(row.improvement_status),
    timeDeltaMillis = row.time_delta_millis, errorDelta = row.error_delta?.toInt(),
  )

  private fun mapAccount(row: User_account): UserAccount = UserAccount(
    userId = row.user_id,
    registerId = row.register_id,
    nickname = row.nickname,
    gender = org.easy.schulte.core.model.account.enums.Gender.valueOf(row.gender),
    createdAt = row.created_at,
  )
}

private const val MAX_RECORD_COUNT = 1000L
