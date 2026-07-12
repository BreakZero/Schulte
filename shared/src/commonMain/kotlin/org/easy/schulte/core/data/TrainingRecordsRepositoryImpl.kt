package org.easy.schulte.core.data

import org.easy.schulte.core.model.records.enums.RecordGridFilter
import org.easy.schulte.core.model.records.enums.RecordModeFilter
import org.easy.schulte.core.model.records.enums.RecordTimeFilter

internal class TrainingRecordsRepositoryImpl(
  private val sharedState: SchulteSharedState,
) : TrainingRecordsRepository,
  FeatureStateRepository by sharedState {
  override fun selectRecordGridFilter(filter: RecordGridFilter) {
    updateConfiguration { copy(recordGridFilter = filter) }
  }

  override fun selectRecordModeFilter(filter: RecordModeFilter) {
    updateConfiguration { copy(recordModeFilter = filter) }
  }

  override fun selectRecordTimeFilter(filter: RecordTimeFilter) {
    updateConfiguration { copy(recordTimeFilter = filter) }
  }

  private fun updateConfiguration(block: org.easy.schulte.core.model.configuration.AppConfiguration.() -> org.easy.schulte.core.model.configuration.AppConfiguration) {
    sharedState.configurationStore.updateConfiguration(sharedState.currentConfiguration().block())
  }
}
