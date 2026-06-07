package org.easy.schulte.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.easy.schulte.core.data.SchulteRepository
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

internal fun SchulteRepository.configStateIn(scope: CoroutineScope): StateFlow<ConfigState> = featureStateIn(scope, SchulteState::toConfigState)

internal fun SchulteRepository.trainingStateIn(scope: CoroutineScope): StateFlow<TrainingState> = featureStateIn(scope, SchulteState::toTrainingState)

internal fun SchulteRepository.reportStateIn(scope: CoroutineScope): StateFlow<ReportState> = featureStateIn(scope, SchulteState::toReportState)

internal fun SchulteRepository.adviceStateIn(scope: CoroutineScope): StateFlow<AdviceState> = featureStateIn(scope, SchulteState::toAdviceState)

internal fun SchulteRepository.settingsStateIn(scope: CoroutineScope): StateFlow<SettingsState> = featureStateIn(scope, SchulteState::toSettingsState)

internal fun SchulteRepository.trainingRecordsStateIn(scope: CoroutineScope): StateFlow<TrainingRecordsState> = featureStateIn(scope, SchulteState::toTrainingRecordsState)

internal fun SchulteRepository.accountStateIn(scope: CoroutineScope): StateFlow<AccountState> = featureStateIn(scope, SchulteState::toAccountState)

private fun <T> SchulteRepository.featureStateIn(
  scope: CoroutineScope,
  mapper: (SchulteState) -> T,
): StateFlow<T> = state
  .map(mapper)
  .distinctUntilChanged()
  .stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = mapper(currentState()),
  )

private fun SchulteState.toConfigState(): ConfigState = ConfigState(
  selectedGrid = selectedGrid,
  selectedAgeGroup = selectedAgeGroup,
  selectedMarkMode = selectedMarkMode,
  latestRecord = recordSummary.latestRecord,
  isLoggedIn = isLoggedIn,
  hasRecords = records.isNotEmpty(),
)

private fun SchulteState.toTrainingState(): TrainingState = TrainingState(
  selectedGrid = selectedGrid,
  selectedMarkMode = selectedMarkMode,
  numbers = numbers,
  currentTarget = currentTarget,
  completedNumbers = completedNumbers,
  elapsedMillis = elapsedMillis,
  errorCount = errorCount,
  lastFeedback = lastFeedback,
)

private fun SchulteState.toReportState(): ReportState = ReportState(
  report = report,
  isLoggedIn = isLoggedIn,
  currentUserNickname = currentUser?.nickname.orEmpty(),
  progressComparison = progressComparison,
  recordSummary = recordSummary,
  aiConfigured = aiSettings.isConfigured,
  aiAnalysisState = aiAnalysisState,
)

private fun SchulteState.toAdviceState(): AdviceState = AdviceState(
  aiAnalysis = aiAnalysis,
)

private fun SchulteState.toSettingsState(): SettingsState {
  val user = currentUser
  return SettingsState(
    selectedGrid = selectedGrid,
    selectedAgeGroup = selectedAgeGroup,
    selectedMarkMode = selectedMarkMode,
    aiSettings = aiSettings,
    apiKeyVisible = apiKeyVisible,
    settingsMessage = settingsMessage,
    showClearRecordsDialog = showClearRecordsDialog,
    totalRecordCount = recordSummary.totalCount,
    currentAccountRecordCount = user?.let { account -> records.count { it.ownerUserId == account.userId } } ?: 0,
    unlinkedLocalRecordCount = unlinkedLocalRecordCount,
    isLoggedIn = isLoggedIn,
    currentUserNickname = user?.nickname.orEmpty(),
    currentUserRegisterId = user?.registerId.orEmpty(),
  )
}

private fun SchulteState.toTrainingRecordsState(): TrainingRecordsState = TrainingRecordsState(
  records = records,
  recordSummary = recordSummary,
  recordGridFilter = recordGridFilter,
  recordModeFilter = recordModeFilter,
  recordTimeFilter = recordTimeFilter,
  isLoggedIn = isLoggedIn,
  currentUserNickname = currentUser?.nickname.orEmpty(),
)

private fun SchulteState.toAccountState(): AccountState = AccountState(
  currentUser = currentUser,
  isLoggedIn = isLoggedIn,
  accountForm = accountForm,
  accountMessage = accountMessage,
  showLinkLocalRecordsDialog = showLinkLocalRecordsDialog,
  showLogoutDialog = showLogoutDialog,
  unlinkedLocalRecordCount = unlinkedLocalRecordCount,
  recordSummary = recordSummary,
  assistedTrainingCount = records.count { it.markMode == MarkMode.AssistedMarking },
  competitiveProfile = competitiveProfile,
)
