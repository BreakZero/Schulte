package org.easy.schulte.core.data

import org.easy.schulte.core.model.records.enums.RecordGridFilter
import org.easy.schulte.core.model.records.enums.RecordModeFilter
import org.easy.schulte.core.model.records.enums.RecordTimeFilter

internal interface TrainingRecordsRepository : FeatureStateRepository {
  suspend fun selectRecordGridFilter(filter: RecordGridFilter)
  suspend fun selectRecordModeFilter(filter: RecordModeFilter)
  suspend fun selectRecordTimeFilter(filter: RecordTimeFilter)
}
