package org.easy.schulte.feature.records

import org.easy.schulte.core.model.RecordGridFilter
import org.easy.schulte.core.model.RecordModeFilter
import org.easy.schulte.core.model.RecordTimeFilter
import org.easy.schulte.core.model.TrainingRecord
import org.easy.schulte.core.model.TrainingRecordSummary

internal data class TrainingRecordsState(
  val records: List<TrainingRecord> = emptyList(),
  val recordSummary: TrainingRecordSummary = TrainingRecordSummary(),
  val recordGridFilter: RecordGridFilter = RecordGridFilter.All,
  val recordModeFilter: RecordModeFilter = RecordModeFilter.All,
  val recordTimeFilter: RecordTimeFilter = RecordTimeFilter.All,
  val isLoggedIn: Boolean = false,
  val currentUserNickname: String = "",
)
