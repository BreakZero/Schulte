package org.easy.schulte.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import org.easy.schulte.core.data.SchulteRepository
import org.easy.schulte.core.model.ConfigurationFeature
import org.easy.schulte.core.model.FeatureConfiguration
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.model.currentUser
import org.easy.schulte.core.model.isLoggedIn
import org.easy.schulte.core.model.unlinkedLocalRecordCount
import org.easy.schulte.feature.account.AccountState
import org.easy.schulte.feature.advice.AdviceState
import org.easy.schulte.feature.config.ConfigState
import org.easy.schulte.feature.records.TrainingRecordsState
import org.easy.schulte.feature.report.ReportState
import org.easy.schulte.feature.settings.SettingsState
import org.easy.schulte.feature.training.TrainingState

internal fun SchulteRepository.configStateIn(scope: CoroutineScope): StateFlow<ConfigState> = featureStateIn(scope, ConfigurationFeature.Config, ::toConfigState)

internal fun SchulteRepository.trainingStateIn(scope: CoroutineScope): StateFlow<TrainingState> = featureStateIn(scope, ConfigurationFeature.Training, ::toTrainingState)

internal fun SchulteRepository.reportStateIn(scope: CoroutineScope): StateFlow<ReportState> = featureStateIn(scope, ConfigurationFeature.Report, ::toReportState)

internal fun SchulteRepository.adviceStateIn(scope: CoroutineScope): StateFlow<AdviceState> = featureStateIn(scope, ConfigurationFeature.Advice, ::toAdviceState)

internal fun SchulteRepository.settingsStateIn(scope: CoroutineScope): StateFlow<SettingsState> = featureStateIn(scope, ConfigurationFeature.Settings, ::toSettingsState)

internal fun SchulteRepository.trainingRecordsStateIn(scope: CoroutineScope): StateFlow<TrainingRecordsState> = featureStateIn(scope, ConfigurationFeature.Records, ::toTrainingRecordsState)

internal fun SchulteRepository.accountStateIn(scope: CoroutineScope): StateFlow<AccountState> = featureStateIn(scope, ConfigurationFeature.Account, ::toAccountState)

private fun <T> SchulteRepository.featureStateIn(
  scope: CoroutineScope,
  feature: ConfigurationFeature,
  mapper: (SchulteState, FeatureConfiguration) -> T,
): StateFlow<T> = state
  .combine(observeFeatureConfiguration(feature)) { state, configuration ->
    mapper(state, configuration)
  }
  .distinctUntilChanged()
  .stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = mapper(currentState(), currentFeatureConfiguration(feature)),
  )

private fun toConfigState(state: SchulteState, configuration: FeatureConfiguration): ConfigState = ConfigState(
  selectedGrid = configuration.selectedGrid ?: ConfigState().selectedGrid,
  selectedAgeGroup = configuration.selectedAgeGroup ?: ConfigState().selectedAgeGroup,
  selectedMarkMode = configuration.selectedMarkMode ?: ConfigState().selectedMarkMode,
  latestRecord = state.recordSummary.latestRecord,
  isLoggedIn = state.isLoggedIn,
  hasRecords = state.records.isNotEmpty(),
)

private fun toTrainingState(state: SchulteState, configuration: FeatureConfiguration): TrainingState = TrainingState(
  selectedGrid = configuration.selectedGrid ?: TrainingState().selectedGrid,
  selectedMarkMode = configuration.selectedMarkMode ?: TrainingState().selectedMarkMode,
  numbers = state.numbers,
  currentTarget = state.currentTarget,
  completedNumbers = state.completedNumbers,
  elapsedMillis = state.elapsedMillis,
  errorCount = state.errorCount,
  lastFeedback = state.lastFeedback,
)

private fun toReportState(state: SchulteState, configuration: FeatureConfiguration): ReportState = ReportState(
  report = state.report,
  isLoggedIn = state.isLoggedIn,
  currentUserNickname = state.currentUser?.nickname.orEmpty(),
  progressComparison = state.progressComparison,
  recordSummary = state.recordSummary,
  aiConfigured = configuration.aiSettings?.isConfigured ?: false,
  aiAnalysisState = state.aiAnalysisState,
)

private fun toAdviceState(state: SchulteState, configuration: FeatureConfiguration): AdviceState = AdviceState(
  aiAnalysis = state.aiAnalysis,
)

private fun toSettingsState(state: SchulteState, configuration: FeatureConfiguration): SettingsState {
  val user = state.currentUser
  val defaultState = SettingsState()
  return SettingsState(
    selectedGrid = configuration.selectedGrid ?: defaultState.selectedGrid,
    selectedAgeGroup = configuration.selectedAgeGroup ?: defaultState.selectedAgeGroup,
    selectedMarkMode = configuration.selectedMarkMode ?: defaultState.selectedMarkMode,
    aiSettings = configuration.aiSettings ?: defaultState.aiSettings,
    apiKeyVisible = state.apiKeyVisible,
    settingsMessage = state.settingsMessage,
    showClearRecordsDialog = state.showClearRecordsDialog,
    totalRecordCount = state.recordSummary.totalCount,
    currentAccountRecordCount = user?.let { account -> state.records.count { it.ownerUserId == account.userId } } ?: 0,
    unlinkedLocalRecordCount = state.unlinkedLocalRecordCount,
    isLoggedIn = state.isLoggedIn,
    currentUserNickname = user?.nickname.orEmpty(),
    currentUserRegisterId = user?.registerId.orEmpty(),
  )
}

private fun toTrainingRecordsState(state: SchulteState, configuration: FeatureConfiguration): TrainingRecordsState = TrainingRecordsState(
  records = state.records,
  recordSummary = state.recordSummary,
  recordGridFilter = configuration.recordGridFilter ?: TrainingRecordsState().recordGridFilter,
  recordModeFilter = configuration.recordModeFilter ?: TrainingRecordsState().recordModeFilter,
  recordTimeFilter = configuration.recordTimeFilter ?: TrainingRecordsState().recordTimeFilter,
  isLoggedIn = state.isLoggedIn,
  currentUserNickname = state.currentUser?.nickname.orEmpty(),
)

private fun toAccountState(state: SchulteState, configuration: FeatureConfiguration): AccountState = AccountState(
  currentUser = state.currentUser,
  isLoggedIn = state.isLoggedIn,
  accountForm = state.accountForm,
  accountMessage = state.accountMessage,
  showLinkLocalRecordsDialog = state.showLinkLocalRecordsDialog,
  showLogoutDialog = state.showLogoutDialog,
  unlinkedLocalRecordCount = state.unlinkedLocalRecordCount,
  recordSummary = state.recordSummary,
  assistedTrainingCount = state.records.count { it.markMode == MarkMode.AssistedMarking },
  competitiveProfile = state.competitiveProfile,
)
