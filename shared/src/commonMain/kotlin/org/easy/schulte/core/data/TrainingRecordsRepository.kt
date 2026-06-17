package org.easy.schulte.core.data

import org.easy.schulte.core.model.records.enums.RecordGridFilter
import org.easy.schulte.core.model.records.enums.RecordModeFilter
import org.easy.schulte.core.model.records.enums.RecordTimeFilter

internal interface TrainingRecordsRepository : FeatureStateRepository {
  fun selectRecordGridFilter(filter: RecordGridFilter)
  fun selectRecordModeFilter(filter: RecordModeFilter)
  fun selectRecordTimeFilter(filter: RecordTimeFilter)
}
