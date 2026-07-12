package org.easy.schulte.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.easy.schulte.core.data.FeatureStateRepository
import org.easy.schulte.core.model.configuration.FeatureConfiguration
import org.easy.schulte.core.model.configuration.enums.ConfigurationFeature
import org.easy.schulte.core.model.runtime.AccountRuntimeState
import org.easy.schulte.core.model.runtime.ReportRuntimeState
import org.easy.schulte.core.model.runtime.SettingsRuntimeState
import org.easy.schulte.core.model.runtime.TrainingRecordsRuntimeState
import org.easy.schulte.core.model.runtime.TrainingRuntimeState
import org.easy.schulte.core.model.runtime.currentUser
import org.easy.schulte.core.model.runtime.isLoggedIn
import org.easy.schulte.core.model.runtime.unlinkedLocalRecordCount
import org.easy.schulte.core.model.training.enums.MarkMode
import org.easy.schulte.feature.account.AccountState
import org.easy.schulte.feature.advice.AdviceState
import org.easy.schulte.feature.config.ConfigState
import org.easy.schulte.feature.records.TrainingRecordsState
import org.easy.schulte.feature.report.ReportState
import org.easy.schulte.feature.settings.SettingsState
import org.easy.schulte.feature.training.TrainingState

internal fun FeatureStateRepository.configStateIn(scope: CoroutineScope): StateFlow<ConfigState> = combine(
  observeFeatureConfiguration(ConfigurationFeature.Config),
  recordsState,
  accountState,
  ::toConfigState,
).stateIn(
  scope = scope,
  initialValue = toConfigState(
    currentFeatureConfiguration(ConfigurationFeature.Config),
    currentRecordsState(),
    currentAccountState(),
  ),
)

internal fun FeatureStateRepository.trainingStateIn(scope: CoroutineScope): StateFlow<TrainingState> = combine(
  observeFeatureConfiguration(ConfigurationFeature.Training),
  trainingState,
  ::toTrainingState,
).stateIn(
  scope = scope,
  initialValue = toTrainingState(
    currentFeatureConfiguration(ConfigurationFeature.Training),
    currentTrainingState(),
  ),
)

internal fun FeatureStateRepository.reportStateIn(scope: CoroutineScope): StateFlow<ReportState> = combine(
  observeFeatureConfiguration(ConfigurationFeature.Report),
  reportState,
  recordsState,
  accountState,
  ::toReportState,
).stateIn(
  scope = scope,
  initialValue = toReportState(
    currentFeatureConfiguration(ConfigurationFeature.Report),
    currentReportState(),
    currentRecordsState(),
    currentAccountState(),
  ),
)

internal fun FeatureStateRepository.adviceStateIn(scope: CoroutineScope): StateFlow<AdviceState> = reportState
  .map(::toAdviceState)
  .stateIn(
    scope = scope,
    initialValue = toAdviceState(currentReportState()),
  )

internal fun FeatureStateRepository.settingsStateIn(scope: CoroutineScope): StateFlow<SettingsState> = combine(
  observeFeatureConfiguration(ConfigurationFeature.Settings),
  settingsState,
  recordsState,
  accountState,
  ::toSettingsState,
).stateIn(
  scope = scope,
  initialValue = toSettingsState(
    currentFeatureConfiguration(ConfigurationFeature.Settings),
    currentSettingsState(),
    currentRecordsState(),
    currentAccountState(),
  ),
)

internal fun FeatureStateRepository.trainingRecordsStateIn(scope: CoroutineScope): StateFlow<TrainingRecordsState> = combine(
  observeFeatureConfiguration(ConfigurationFeature.Records),
  recordsState,
  accountState,
  ::toTrainingRecordsState,
).stateIn(
  scope = scope,
  initialValue = toTrainingRecordsState(
    currentFeatureConfiguration(ConfigurationFeature.Records),
    currentRecordsState(),
    currentAccountState(),
  ),
)

internal fun FeatureStateRepository.accountStateIn(scope: CoroutineScope): StateFlow<AccountState> = combine(
  accountState,
  recordsState,
  ::toAccountState,
).stateIn(
  scope = scope,
  initialValue = toAccountState(currentAccountState(), currentRecordsState()),
)

private fun <T> Flow<T>.stateIn(scope: CoroutineScope, initialValue: T): StateFlow<T> = distinctUntilChanged()
  .stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = initialValue,
  )

private fun toConfigState(
  configuration: FeatureConfiguration,
  records: TrainingRecordsRuntimeState,
  account: AccountRuntimeState,
): ConfigState = ConfigState(
  selectedGrid = configuration.selectedGrid ?: ConfigState().selectedGrid,
  selectedAgeGroup = configuration.selectedAgeGroup ?: ConfigState().selectedAgeGroup,
  selectedMarkMode = configuration.selectedMarkMode ?: ConfigState().selectedMarkMode,
  latestRecord = records.recordSummary.latestRecord,
  isLoggedIn = account.isLoggedIn,
  hasRecords = records.records.isNotEmpty(),
)

private fun toTrainingState(
  configuration: FeatureConfiguration,
  training: TrainingRuntimeState,
): TrainingState = TrainingState(
  selectedGrid = configuration.selectedGrid ?: TrainingState().selectedGrid,
  selectedMarkMode = configuration.selectedMarkMode ?: TrainingState().selectedMarkMode,
  numbers = training.numbers,
  currentTarget = training.currentTarget,
  completedNumbers = training.completedNumbers,
  elapsedMillis = training.elapsedMillis,
  errorCount = training.errorCount,
  lastFeedback = training.lastFeedback,
)

private fun toReportState(
  configuration: FeatureConfiguration,
  report: ReportRuntimeState,
  records: TrainingRecordsRuntimeState,
  account: AccountRuntimeState,
): ReportState = ReportState(
  report = report.report,
  isLoggedIn = account.isLoggedIn,
  currentUserNickname = account.currentUser?.nickname.orEmpty(),
  progressComparison = report.progressComparison,
  recordSummary = records.recordSummary,
  aiConfigured = configuration.aiConfigured ?: false,
  aiAnalysisState = report.aiAnalysisState,
)

private fun toAdviceState(report: ReportRuntimeState): AdviceState = AdviceState(
  aiAnalysis = report.aiAnalysis,
)

private fun toSettingsState(
  configuration: FeatureConfiguration,
  settings: SettingsRuntimeState,
  records: TrainingRecordsRuntimeState,
  account: AccountRuntimeState,
): SettingsState {
  val user = account.currentUser
  val defaultState = SettingsState()
  return SettingsState(
    selectedGrid = configuration.selectedGrid ?: defaultState.selectedGrid,
    selectedAgeGroup = configuration.selectedAgeGroup ?: defaultState.selectedAgeGroup,
    selectedMarkMode = configuration.selectedMarkMode ?: defaultState.selectedMarkMode,
    aiSettings = configuration.aiSettings ?: defaultState.aiSettings,
    apiKeyInput = settings.apiKeyInput,
    hasApiKey = settings.hasApiKey,
    apiKeyVisible = settings.apiKeyVisible,
    settingsMessage = settings.settingsMessage,
    showClearRecordsDialog = settings.showClearRecordsDialog,
    totalRecordCount = records.recordSummary.totalCount,
    currentAccountRecordCount = user?.let { account -> records.records.count { it.ownerUserId == account.userId } } ?: 0,
    unlinkedLocalRecordCount = records.unlinkedLocalRecordCount,
    isLoggedIn = account.isLoggedIn,
    currentUserNickname = user?.nickname.orEmpty(),
    currentUserRegisterId = user?.registerId.orEmpty(),
  )
}

private fun toTrainingRecordsState(
  configuration: FeatureConfiguration,
  records: TrainingRecordsRuntimeState,
  account: AccountRuntimeState,
): TrainingRecordsState = TrainingRecordsState(
  records = records.records,
  recordSummary = records.recordSummary,
  recordGridFilter = configuration.recordGridFilter ?: TrainingRecordsState().recordGridFilter,
  recordModeFilter = configuration.recordModeFilter ?: TrainingRecordsState().recordModeFilter,
  recordTimeFilter = configuration.recordTimeFilter ?: TrainingRecordsState().recordTimeFilter,
  isLoggedIn = account.isLoggedIn,
  currentUserNickname = account.currentUser?.nickname.orEmpty(),
)

private fun toAccountState(
  account: AccountRuntimeState,
  records: TrainingRecordsRuntimeState,
): AccountState = AccountState(
  currentUser = account.currentUser,
  isLoggedIn = account.isLoggedIn,
  accountForm = account.accountForm,
  accountMessage = account.accountMessage,
  isSubmitting = account.accountIsSubmitting,
  showLinkLocalRecordsDialog = account.showLinkLocalRecordsDialog,
  showLogoutDialog = account.showLogoutDialog,
  unlinkedLocalRecordCount = records.unlinkedLocalRecordCount,
  recordSummary = records.recordSummary,
  assistedTrainingCount = records.records.count { it.markMode == MarkMode.AssistedMarking },
  competitiveProfile = account.competitiveProfile,
)
