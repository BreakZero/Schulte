package org.easy.schulte.core.model.runtime

import org.easy.schulte.core.model.records.TrainingRecord
import org.easy.schulte.core.model.records.TrainingRecordSummary

data class TrainingRecordsRuntimeState(
  val records: List<TrainingRecord> = emptyList(),
  val recordSummary: TrainingRecordSummary = TrainingRecordSummary(),
)
