package org.easy.schulte.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.easy.schulte.core.model.configuration.AppConfiguration
import org.easy.schulte.core.model.configuration.FeatureConfiguration
import org.easy.schulte.core.model.configuration.enums.ConfigurationFeature
import org.easy.schulte.core.model.runtime.AccountRuntimeState
import org.easy.schulte.core.model.runtime.ReportRuntimeState
import org.easy.schulte.core.model.runtime.SettingsRuntimeState
import org.easy.schulte.core.model.runtime.TrainingRecordsRuntimeState
import org.easy.schulte.core.model.runtime.TrainingRuntimeState

internal interface FeatureStateRepository {
  val trainingState: StateFlow<TrainingRuntimeState>
  val reportState: StateFlow<ReportRuntimeState>
  val settingsState: StateFlow<SettingsRuntimeState>
  val recordsState: Flow<TrainingRecordsRuntimeState>
  val accountState: Flow<AccountRuntimeState>

  fun currentTrainingState(): TrainingRuntimeState
  fun currentReportState(): ReportRuntimeState
  fun currentSettingsState(): SettingsRuntimeState
  fun currentRecordsState(): TrainingRecordsRuntimeState
  fun currentAccountState(): AccountRuntimeState
  fun currentConfiguration(): AppConfiguration
  fun currentFeatureConfiguration(feature: ConfigurationFeature): FeatureConfiguration
  fun observeFeatureConfiguration(feature: ConfigurationFeature): Flow<FeatureConfiguration>
}
