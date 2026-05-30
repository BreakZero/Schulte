package org.easy.schulte.core.data

import org.easy.schulte.core.model.TrainingRecord

internal interface TrainingRecordStore {
  fun getAllRecords(): List<TrainingRecord>
  fun getRecordsForConditions(gridSize: Int, ageGroupName: String, markModeName: String): List<TrainingRecord>
  fun insertRecord(record: TrainingRecord)
  fun clearRecords()
}
