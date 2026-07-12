package org.easy.schulte.core.data

import org.easy.schulte.core.model.records.enums.RecordGridFilter
import org.easy.schulte.core.model.records.enums.RecordModeFilter
import org.easy.schulte.core.model.records.enums.RecordTimeFilter

internal class TrainingRecordsRepositoryImpl(
  private val sharedState: SchulteSharedState,
) : TrainingRecordsRepository,
  FeatureStateRepository by sharedState {
  override suspend fun selectRecordGridFilter(filter: RecordGridFilter) {
    updateConfiguration { copy(recordGridFilter = filter) }
  }

  override suspend fun selectRecordModeFilter(filter: RecordModeFilter) {
    updateConfiguration { copy(recordModeFilter = filter) }
  }

  override suspend fun selectRecordTimeFilter(filter: RecordTimeFilter) {
    updateConfiguration { copy(recordTimeFilter = filter) }
  }

  private suspend fun updateConfiguration(block: org.easy.schulte.core.model.configuration.AppConfiguration.() -> org.easy.schulte.core.model.configuration.AppConfiguration) {
    sharedState.configurationStore.updateConfiguration(sharedState.currentConfiguration().block())
  }
}
