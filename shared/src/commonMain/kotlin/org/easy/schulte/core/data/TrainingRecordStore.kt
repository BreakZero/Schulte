package org.easy.schulte.core.data

import kotlinx.coroutines.flow.Flow
import org.easy.schulte.core.model.TrainingRecord
import org.easy.schulte.core.model.UserAccount

internal interface TrainingRecordStore {
  fun observeAllRecords(): Flow<List<TrainingRecord>>
  fun getAllRecords(): List<TrainingRecord>
  fun getRecordsForConditions(gridSize: Int, ageGroupName: String, markModeName: String): List<TrainingRecord>
  fun insertRecord(record: TrainingRecord)
  fun clearRecords()
  fun observeAccounts(): Flow<List<UserAccount>>
  fun getAccounts(): List<UserAccount>
  fun insertAccount(account: UserAccount)
  fun updateAccountProfile(account: UserAccount)
  fun updateUnownedRecordsOwner(userId: String)
}
