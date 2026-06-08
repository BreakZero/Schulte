package org.easy.schulte.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.easy.schulte.core.model.AccountRuntimeState
import org.easy.schulte.core.model.AppConfiguration
import org.easy.schulte.core.model.ConfigurationFeature
import org.easy.schulte.core.model.FeatureConfiguration
import org.easy.schulte.core.model.ReportRuntimeState
import org.easy.schulte.core.model.SettingsRuntimeState
import org.easy.schulte.core.model.TrainingRecordsRuntimeState
import org.easy.schulte.core.model.TrainingRuntimeState

internal interface FeatureStateRepository {
  val trainingState: StateFlow<TrainingRuntimeState>
  val reportState: StateFlow<ReportRuntimeState>
  val settingsState: StateFlow<SettingsRuntimeState>
  val recordsState: StateFlow<TrainingRecordsRuntimeState>
  val accountState: StateFlow<AccountRuntimeState>

  fun currentTrainingState(): TrainingRuntimeState
  fun currentReportState(): ReportRuntimeState
  fun currentSettingsState(): SettingsRuntimeState
  fun currentRecordsState(): TrainingRecordsRuntimeState
  fun currentAccountState(): AccountRuntimeState
  fun currentConfiguration(): AppConfiguration
  fun currentFeatureConfiguration(feature: ConfigurationFeature): FeatureConfiguration
  fun observeFeatureConfiguration(feature: ConfigurationFeature): Flow<FeatureConfiguration>
}
