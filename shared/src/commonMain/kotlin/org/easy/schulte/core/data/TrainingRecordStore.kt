package org.easy.schulte.core.data

import kotlinx.coroutines.flow.Flow
import org.easy.schulte.core.model.account.UserAccount
import org.easy.schulte.core.model.records.TrainingRecord

internal interface TrainingRecordStore {
  fun observeAllRecords(): Flow<List<TrainingRecord>>
  suspend fun getAllRecords(): List<TrainingRecord>
  suspend fun getRecordsForConditions(gridSize: Int, ageGroupName: String, markModeName: String): List<TrainingRecord>
  suspend fun insertRecord(record: TrainingRecord)
  suspend fun clearRecords()
  fun observeAccounts(): Flow<List<UserAccount>>
  suspend fun getAccounts(): List<UserAccount>
  suspend fun insertAccount(account: UserAccount)
  suspend fun updateAccountProfile(account: UserAccount)
  suspend fun updateUnownedRecordsOwner(userId: String)
}
