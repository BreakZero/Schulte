package org.easy.schulte.core.data

import org.easy.schulte.core.model.RecordGridFilter
import org.easy.schulte.core.model.RecordModeFilter
import org.easy.schulte.core.model.RecordTimeFilter

internal interface TrainingRecordsRepository : FeatureStateRepository {
  fun selectRecordGridFilter(filter: RecordGridFilter)
  fun selectRecordModeFilter(filter: RecordModeFilter)
  fun selectRecordTimeFilter(filter: RecordTimeFilter)
}
