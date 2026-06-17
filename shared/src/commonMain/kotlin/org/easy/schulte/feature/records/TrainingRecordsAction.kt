package org.easy.schulte.feature.records

import org.easy.schulte.core.model.records.enums.RecordGridFilter
import org.easy.schulte.core.model.records.enums.RecordModeFilter
import org.easy.schulte.core.model.records.enums.RecordTimeFilter

internal sealed interface TrainingRecordsAction {
  data object Back : TrainingRecordsAction
  data object StartTraining : TrainingRecordsAction
  data class SelectGridFilter(val filter: RecordGridFilter) : TrainingRecordsAction
  data class SelectModeFilter(val filter: RecordModeFilter) : TrainingRecordsAction
  data class SelectTimeFilter(val filter: RecordTimeFilter) : TrainingRecordsAction
}
