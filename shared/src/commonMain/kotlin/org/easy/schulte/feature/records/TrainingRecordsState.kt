package org.easy.schulte.feature.records

import org.easy.schulte.core.model.records.TrainingRecord
import org.easy.schulte.core.model.records.TrainingRecordSummary
import org.easy.schulte.core.model.records.enums.RecordGridFilter
import org.easy.schulte.core.model.records.enums.RecordLayoutFilter
import org.easy.schulte.core.model.records.enums.RecordModeFilter
import org.easy.schulte.core.model.records.enums.RecordTimeFilter

internal data class TrainingRecordsState(
  val records: List<TrainingRecord> = emptyList(),
  val recordSummary: TrainingRecordSummary = TrainingRecordSummary(),
  val recordGridFilter: RecordGridFilter = RecordGridFilter.All,
  val recordModeFilter: RecordModeFilter = RecordModeFilter.All,
  val recordLayoutFilter: RecordLayoutFilter = RecordLayoutFilter.All,
  val recordTimeFilter: RecordTimeFilter = RecordTimeFilter.All,
  val isLoggedIn: Boolean = false,
  val currentUserNickname: String = "",
)
