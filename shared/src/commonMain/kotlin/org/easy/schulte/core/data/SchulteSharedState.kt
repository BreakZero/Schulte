package org.easy.schulte.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.easy.schulte.core.model.configuration.AppConfiguration
import org.easy.schulte.core.model.configuration.FeatureConfiguration
import org.easy.schulte.core.model.configuration.enums.ConfigurationFeature
import org.easy.schulte.core.model.records.TrainingRecord
import org.easy.schulte.core.model.records.TrainingRecordSummary
import org.easy.schulte.core.model.runtime.AccountRuntimeState
import org.easy.schulte.core.model.runtime.ReportRuntimeState
import org.easy.schulte.core.model.runtime.SettingsRuntimeState
import org.easy.schulte.core.model.runtime.TrainingRecordsRuntimeState
import org.easy.schulte.core.model.runtime.TrainingRuntimeState
import org.easy.schulte.core.platform.currentTimeMillis
import org.easy.schulte.core.security.AccountSessionStore
import org.easy.schulte.core.security.AiApiKeyStore

/** Shared runtime state and read models used by feature-specific repositories. */
internal class SchulteSharedState(
  internal val recordStore: TrainingRecordStore,
  internal val configurationStore: ConfigurationStore,
  internal val aiApiKeyStore: AiApiKeyStore,
  accountSessionStore: AccountSessionStore,
) : FeatureStateRepository {
  private val restoredSession = accountSessionStore.read()

  internal val mutableTrainingState = MutableStateFlow(TrainingRuntimeState())
  internal val mutableReportState = MutableStateFlow(ReportRuntimeState())
  internal val mutableSettingsState = MutableStateFlow(
    SettingsRuntimeState(hasApiKey = aiApiKeyStore.hasApiKey()),
  )
  internal val mutableAccountState = MutableStateFlow(
    AccountRuntimeState(currentUserId = restoredSession?.userId),
  )

  override val trainingState: StateFlow<TrainingRuntimeState> = mutableTrainingState.asStateFlow()
  override val reportState: StateFlow<ReportRuntimeState> = mutableReportState.asStateFlow()
  override val settingsState: StateFlow<SettingsRuntimeState> = mutableSettingsState.asStateFlow()
  override val recordsState: Flow<TrainingRecordsRuntimeState> = recordStore.observeAllRecords()
    .map { records -> TrainingRecordsRuntimeState().withRecords(records, now = currentTimeMillis()) }
  override val accountState: Flow<AccountRuntimeState> = recordStore.observeAccounts()
    .combine(mutableAccountState) { accounts, accountState -> accountState.copy(accounts = accounts) }

  override fun currentTrainingState(): TrainingRuntimeState = trainingState.value

  override fun currentReportState(): ReportRuntimeState = reportState.value

  override fun currentSettingsState(): SettingsRuntimeState = settingsState.value

  override suspend fun currentRecordsState(): TrainingRecordsRuntimeState = TrainingRecordsRuntimeState()
    .withRecords(recordStore.getAllRecords(), now = currentTimeMillis())

  override suspend fun currentAccountState(): AccountRuntimeState = mutableAccountState.value.copy(
    accounts = recordStore.getAccounts(),
  )

  override suspend fun currentConfiguration(): AppConfiguration = configurationStore.getConfiguration()

  override suspend fun currentFeatureConfiguration(feature: ConfigurationFeature): FeatureConfiguration = configurationStore
    .getFeatureConfiguration(feature)
    .withAiConfigured()

  override fun observeFeatureConfiguration(feature: ConfigurationFeature): Flow<FeatureConfiguration> = configurationStore
    .observeFeatureConfiguration(feature)
    .map { it.withAiConfigured() }

  private fun FeatureConfiguration.withAiConfigured(): FeatureConfiguration = copy(
    aiConfigured = aiSettings?.let {
      it.aiEnabled && it.baseUrl.isNotBlank() && it.modelName.isNotBlank() && aiApiKeyStore.hasApiKey()
    },
  )
}

private fun TrainingRecordsRuntimeState.withRecords(records: List<TrainingRecord>, now: Long): TrainingRecordsRuntimeState = copy(
  records = records,
  recordSummary = records.summary(now),
)

private fun List<TrainingRecord>.summary(now: Long): TrainingRecordSummary {
  val latest = firstOrNull()
  val recent7DaysStart = now - 7L * 24 * 60 * 60 * 1000
  val recentSameCondition = latest?.let { latestRecord ->
    filter {
      it.gridSpec == latestRecord.gridSpec &&
        it.ageGroup == latestRecord.ageGroup &&
        it.markMode == latestRecord.markMode
    }.take(5)
  }.orEmpty()
  return TrainingRecordSummary(
    totalCount = size,
    recent7DaysCount = count { it.createdAt >= recent7DaysStart },
    bestRecord = minByOrNull { it.elapsedTimeMillis },
    latestRecord = latest,
    recentAverageTimeMillis = recentSameCondition.takeIf { it.isNotEmpty() }
      ?.map { it.elapsedTimeMillis }
      ?.average()
      ?.toLong(),
    recentAverageErrorCount = recentSameCondition.takeIf { it.isNotEmpty() }
      ?.map { it.errorCount }
      ?.average(),
  )
}
