package org.easy.schulte.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.easy.schulte.core.model.AccountForm
import org.easy.schulte.core.model.AccountMessage
import org.easy.schulte.core.model.AccountRuntimeState
import org.easy.schulte.core.model.AgeGroup
import org.easy.schulte.core.model.AiAnalysis
import org.easy.schulte.core.model.AiAnalysisState
import org.easy.schulte.core.model.AiSettings
import org.easy.schulte.core.model.AppConfiguration
import org.easy.schulte.core.model.CellFeedback
import org.easy.schulte.core.model.ConfigurationFeature
import org.easy.schulte.core.model.FeatureConfiguration
import org.easy.schulte.core.model.Gender
import org.easy.schulte.core.model.GridSpec
import org.easy.schulte.core.model.ImprovementStatus
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.ProgressComparison
import org.easy.schulte.core.model.RecordGridFilter
import org.easy.schulte.core.model.RecordModeFilter
import org.easy.schulte.core.model.RecordTimeFilter
import org.easy.schulte.core.model.ReportRuntimeState
import org.easy.schulte.core.model.SettingsMessage
import org.easy.schulte.core.model.SettingsRuntimeState
import org.easy.schulte.core.model.TrainingRecord
import org.easy.schulte.core.model.TrainingRecordsRuntimeState
import org.easy.schulte.core.model.TrainingRecordSummary
import org.easy.schulte.core.model.TrainingReport
import org.easy.schulte.core.model.TrainingRuntimeState
import org.easy.schulte.core.model.UserAccount
import org.easy.schulte.core.model.currentUser
import org.easy.schulte.core.model.unlinkedLocalRecordCount
import org.easy.schulte.core.network.AccountApi
import org.easy.schulte.core.network.toAccountErrorMessage
import org.easy.schulte.core.platform.currentTimeMillis
import kotlin.math.abs
import kotlin.math.max

internal class InMemorySchulteRepository(
  private val recordStore: TrainingRecordStore,
  private val configurationStore: ConfigurationStore,
  private val accountApi: AccountApi,
) : ConfigurationRepository,
  TrainingRepository,
  SettingsRepository,
  TrainingRecordsRepository,
  AccountRepository,
  AiAnalysisRepository {
  private var accessToken: String = ""
  private var refreshToken: String = ""

  private val mutableTrainingState = MutableStateFlow(TrainingRuntimeState())
  private val mutableReportState = MutableStateFlow(ReportRuntimeState())
  private val mutableSettingsState = MutableStateFlow(SettingsRuntimeState())
  private val mutableRecordsState = MutableStateFlow(
    TrainingRecordsRuntimeState()
      .withRecords(recordStore.getAllRecords(), now = currentTimeMillis()),
  )
  private val mutableAccountState = MutableStateFlow(
    AccountRuntimeState(
      accounts = recordStore.getAccounts(),
    ),
  )

  override val trainingState = mutableTrainingState.asStateFlow()
  override val reportState = mutableReportState.asStateFlow()
  override val settingsState = mutableSettingsState.asStateFlow()
  override val recordsState = mutableRecordsState.asStateFlow()
  override val accountState = mutableAccountState.asStateFlow()

  override fun currentTrainingState(): TrainingRuntimeState = trainingState.value

  override fun currentReportState(): ReportRuntimeState = reportState.value

  override fun currentSettingsState(): SettingsRuntimeState = settingsState.value

  override fun currentRecordsState(): TrainingRecordsRuntimeState = recordsState.value

  override fun currentAccountState(): AccountRuntimeState = accountState.value

  override fun currentConfiguration(): AppConfiguration = configurationStore.getConfiguration()

  override fun currentFeatureConfiguration(feature: ConfigurationFeature): FeatureConfiguration = configurationStore.getFeatureConfiguration(feature)

  override fun observeFeatureConfiguration(feature: ConfigurationFeature): Flow<FeatureConfiguration> = configurationStore.observeFeatureConfiguration(feature)

  override fun selectGrid(spec: GridSpec) {
    updateConfiguration { copy(selectedGrid = spec) }
  }

  override fun selectAgeGroup(ageGroup: AgeGroup) {
    updateConfiguration { copy(selectedAgeGroup = ageGroup) }
  }

  override fun selectMarkMode(markMode: MarkMode) {
    updateConfiguration { copy(selectedMarkMode = markMode) }
  }

  override fun clearSettingsMessage() {
    mutableSettingsState.update { it.copy(settingsMessage = null) }
  }

  override fun startTraining(numbers: List<Int>) {
    mutableTrainingState.update {
      it.copy(
        numbers = numbers,
        currentTarget = 1,
        completedNumbers = emptySet(),
        elapsedMillis = 0L,
        errorCount = 0,
        lastFeedback = null,
      )
    }
    mutableReportState.update {
      it.copy(
        report = null,
        aiAnalysis = null,
        aiAnalysisState = AiAnalysisState.Idle,
        progressComparison = null,
      )
    }
    mutableAccountState.update { it.copy(accountMessage = null) }
  }

  override fun updateElapsedMillis(elapsedMillis: Long) {
    mutableTrainingState.update { it.copy(elapsedMillis = elapsedMillis) }
  }

  override fun recordCorrectCell(value: Int, completedNumbers: Set<Int>, nextTarget: Int) {
    mutableTrainingState.update {
      it.copy(
        currentTarget = nextTarget,
        completedNumbers = completedNumbers,
        lastFeedback = CellFeedback(value, isCorrect = true),
      )
    }
  }

  override fun recordIncorrectCell(value: Int) {
    mutableTrainingState.update {
      it.copy(
        errorCount = it.errorCount + 1,
        lastFeedback = CellFeedback(value, isCorrect = false),
      )
    }
  }

  override fun clearFeedbackIfMatches(value: Int) {
    mutableTrainingState.update {
      if (it.lastFeedback?.value == value) it.copy(lastFeedback = null) else it
    }
  }

  override fun finishTraining(report: TrainingReport) {
    val comparison = createTrainingRecord(report)
    val records = listOf(comparison.currentRecord) + recordStore.getAllRecords()
    recordStore.insertRecord(comparison.currentRecord)
    mutableTrainingState.update {
      it.copy(
        elapsedMillis = report.elapsedMillis,
        lastFeedback = null,
      )
    }
    mutableRecordsState.update {
      it.copy(
        records = records,
        recordSummary = records.summary(now = currentTimeMillis()),
      )
    }
    mutableReportState.update {
      it.copy(
        report = report,
        progressComparison = comparison,
        aiAnalysis = null,
        aiAnalysisState = AiAnalysisState.Idle,
      )
    }
  }

  override fun exitTraining() {
    mutableTrainingState.update { it.copy(lastFeedback = null) }
  }

  override fun updateAiSettings(block: AiSettings.() -> AiSettings) {
    val nextConfiguration = currentConfiguration().let {
      it.copy(aiSettings = it.aiSettings.block())
    }
    configurationStore.updateConfiguration(nextConfiguration)
    mutableSettingsState.update { it.copy(settingsMessage = null) }
  }

  override fun toggleApiKeyVisibility() {
    mutableSettingsState.update { it.copy(apiKeyVisible = !it.apiKeyVisible) }
  }

  override fun clearAiSettings() {
    val nextConfiguration = currentConfiguration().copy(
      aiSettings = AiSettings(
        aiEnabled = false,
        apiKey = "",
        baseUrl = "",
        modelName = "gpt-4o-mini",
      ),
    )
    configurationStore.updateConfiguration(nextConfiguration)
    mutableSettingsState.update {
      it.copy(
        settingsMessage = SettingsMessage.AiCleared,
      )
    }
    mutableReportState.update { it.copy(aiAnalysisState = AiAnalysisState.Idle, aiAnalysis = null) }
  }

  override fun saveSettings() {
    val currentConfiguration = currentConfiguration()
    val nextConfiguration = currentConfiguration.copy(
      selectedMarkMode = if (currentConfiguration.aiSettings.assistedMarkingEnabled) {
        MarkMode.AssistedMarking
      } else {
        MarkMode.BriefFeedbackOnly
      },
    )
    configurationStore.updateConfiguration(nextConfiguration)
    mutableSettingsState.update {
      it.copy(
        settingsMessage = SettingsMessage.Saved,
      )
    }
  }

  override fun setSettingsMessage(message: SettingsMessage) {
    mutableSettingsState.update { it.copy(settingsMessage = message) }
  }

  override fun selectRecordGridFilter(filter: RecordGridFilter) {
    updateConfiguration { copy(recordGridFilter = filter) }
  }

  override fun selectRecordModeFilter(filter: RecordModeFilter) {
    updateConfiguration { copy(recordModeFilter = filter) }
  }

  override fun selectRecordTimeFilter(filter: RecordTimeFilter) {
    updateConfiguration { copy(recordTimeFilter = filter) }
  }

  override fun requestClearTrainingRecords() {
    mutableSettingsState.update { it.copy(showClearRecordsDialog = true) }
  }

  override fun cancelClearTrainingRecords() {
    mutableSettingsState.update { it.copy(showClearRecordsDialog = false) }
  }

  override fun clearTrainingRecords() {
    recordStore.clearRecords()
    mutableRecordsState.update {
      it.copy(
        records = emptyList(),
        recordSummary = TrainingRecordSummary(),
      )
    }
    mutableReportState.update { it.copy(progressComparison = null) }
    mutableSettingsState.update {
      it.copy(
        settingsMessage = SettingsMessage.RecordsCleared,
        showClearRecordsDialog = false,
      )
    }
  }

  override fun updateLoginRegisterId(value: String) {
    mutableAccountState.update { it.copy(accountForm = it.accountForm.copy(registerId = value.trim(), errorMessage = null)) }
  }

  override fun updateAccountNickname(value: String) {
    mutableAccountState.update { it.copy(accountForm = it.accountForm.copy(nickname = value, errorMessage = null)) }
  }

  override fun updateAccountPassword(value: String) {
    mutableAccountState.update { it.copy(accountForm = it.accountForm.copy(password = value, errorMessage = null)) }
  }

  override fun updateAccountConfirmPassword(value: String) {
    mutableAccountState.update { it.copy(accountForm = it.accountForm.copy(confirmPassword = value, errorMessage = null)) }
  }

  override fun updateAccountGender(gender: Gender) {
    mutableAccountState.update { it.copy(accountForm = it.accountForm.copy(gender = gender, errorMessage = null)) }
  }

  override fun updateAgreementAccepted(accepted: Boolean) {
    mutableAccountState.update { it.copy(accountForm = it.accountForm.copy(agreementAccepted = accepted, errorMessage = null)) }
  }

  override fun clearAccountForm() {
    mutableAccountState.update {
      it.copy(
        accountForm = AccountForm(
          nickname = it.currentUser?.nickname.orEmpty(),
          gender = it.currentUser?.gender ?: Gender.Private,
        ),
        accountMessage = null,
      )
    }
  }

  override suspend fun registerAccount() {
    val state = currentAccountState()
    val form = state.accountForm
    val error = validateRegistration(form)
    if (error != null) {
      mutableAccountState.update { it.copy(accountForm = form.copy(errorMessage = error)) }
      return
    }
    submitAccountRequest {
      val session = accountApi.register(
        nickname = form.nickname.trim(),
        password = form.password,
        gender = form.gender,
        acceptedTerms = form.agreementAccepted,
      )
      accessToken = session.accessToken
      refreshToken = session.refreshToken
      upsertCurrentAccount(session.user, AccountMessage.Registered)
    }
  }

  override suspend fun loginAccount() {
    val form = currentAccountState().accountForm
    if (form.registerId.isBlank() || form.password.isBlank()) {
      mutableAccountState.update { it.copy(accountForm = form.copy(errorMessage = "请输入注册 ID 和密码")) }
      return
    }
    submitAccountRequest {
      val session = accountApi.login(
        registrationId = form.registerId.trim(),
        password = form.password,
      )
      accessToken = session.accessToken
      refreshToken = session.refreshToken
      upsertCurrentAccount(session.user, AccountMessage.LoggedIn)
    }
  }

  override suspend fun updateCurrentProfile() {
    val state = currentAccountState()
    val user = state.currentUser ?: return
    val form = state.accountForm
    val nickname = form.nickname.trim()
    if (nickname.isBlank()) {
      mutableAccountState.update { it.copy(accountForm = form.copy(errorMessage = "昵称不能为空")) }
      return
    }
    submitAccountRequest {
      val updatedUser = if (accessToken.isNotBlank()) {
        accountApi.updateMe(
          accessToken = accessToken,
          nickname = nickname,
          gender = form.gender,
        ).copy(
          userId = user.userId,
          registerId = user.registerId,
          password = user.password,
        )
      } else {
        user.copy(nickname = nickname, gender = form.gender)
      }
      recordStore.updateAccountProfile(updatedUser)
      mutableAccountState.update {
        it.copy(
          accounts = it.accounts.map { account ->
            if (account.userId == user.userId) updatedUser else account
          },
          accountMessage = AccountMessage.ProfileSaved,
          accountForm = AccountForm(nickname = updatedUser.nickname, gender = updatedUser.gender),
        )
      }
    }
  }

  override fun requestLinkLocalRecords() {
    mutableAccountState.update { it.copy(showLinkLocalRecordsDialog = currentRecordsState().unlinkedLocalRecordCount > 0) }
  }

  override suspend fun linkLocalRecords() {
    val userId = currentAccountState().currentUserId ?: return
    recordStore.updateUnownedRecordsOwner(userId)
    mutableRecordsState.update {
      val linkedRecords = it.records.map { record ->
        if (record.ownerUserId == null) record.copy(ownerUserId = userId) else record
      }
      it.copy(
        records = linkedRecords,
        recordSummary = linkedRecords.summary(now = currentTimeMillis()),
      )
    }
    mutableAccountState.update {
      it.copy(
        showLinkLocalRecordsDialog = false,
        accountMessage = AccountMessage.LocalRecordsLinked,
      )
    }
  }

  override fun dismissLinkLocalRecords() {
    mutableAccountState.update { it.copy(showLinkLocalRecordsDialog = false) }
  }

  override fun requestLogout() {
    mutableAccountState.update { it.copy(showLogoutDialog = true) }
  }

  override fun cancelLogout() {
    mutableAccountState.update { it.copy(showLogoutDialog = false) }
  }

  override suspend fun logout() {
    val token = accessToken
    val refresh = refreshToken
    if (token.isNotBlank() && refresh.isNotBlank()) {
      runCatching {
        accountApi.logout(token, refresh)
      }
    }
    accessToken = ""
    refreshToken = ""
    mutableAccountState.update {
      it.copy(
        currentUserId = null,
        showLogoutDialog = false,
        accountForm = AccountForm(),
        accountMessage = AccountMessage.LoggedOut,
      )
    }
  }

  override fun markRegisterIdCopied() {
    mutableAccountState.update { it.copy(accountMessage = AccountMessage.RegisterIdCopied) }
  }

  override fun clearAccountMessage() {
    mutableAccountState.update { it.copy(accountMessage = null) }
  }

  override fun markAiAnalysisNeedsSettings() {
    mutableReportState.update { it.copy(aiAnalysisState = AiAnalysisState.NeedsSettings) }
  }

  override fun markAiAnalysisLoading() {
    mutableReportState.update { it.copy(aiAnalysisState = AiAnalysisState.Loading) }
  }

  override fun setAiAnalysis(analysis: AiAnalysis) {
    mutableReportState.update {
      it.copy(
        aiAnalysisState = AiAnalysisState.Success,
        aiAnalysis = analysis,
      )
    }
  }

  private fun createTrainingRecord(report: TrainingReport): ProgressComparison {
    val createdAt = currentTimeMillis()
    val sameConditionRecords = recordStore.getRecordsForConditions(
      gridSize = report.gridSpec.size,
      ageGroupName = report.ageGroup.name,
      markModeName = report.markMode.name,
    )
    val previousRecord = sameConditionRecords.firstOrNull()
    val previousBest = sameConditionRecords.minByOrNull { it.elapsedTimeMillis }
    val timeDelta = previousRecord?.let { report.elapsedMillis - it.elapsedTimeMillis }
    val errorDelta = previousRecord?.let { report.errorCount - it.errorCount }
    val isPersonalBest = previousBest == null || report.elapsedMillis < previousBest.elapsedTimeMillis
    val baseStatus = improvementStatus(
      currentElapsedMillis = report.elapsedMillis,
      currentErrorCount = report.errorCount,
      previousRecord = previousRecord,
      isPersonalBest = isPersonalBest,
    )
    val record = TrainingRecord(
      id = "${createdAt}_${report.gridSpec.size}_${report.elapsedMillis}",
      ownerUserId = currentAccountState().currentUserId,
      createdAt = createdAt,
      gridSpec = report.gridSpec,
      ageGroup = report.ageGroup,
      markMode = report.markMode,
      elapsedTimeMillis = report.elapsedMillis,
      errorCount = report.errorCount,
      scoreLevel = report.scoreLevel,
      isPersonalBest = isPersonalBest,
      previousRecordId = previousRecord?.id,
      improvementStatus = baseStatus,
      timeDeltaMillis = timeDelta,
      errorDelta = errorDelta,
    )
    val recent = (listOf(record) + sameConditionRecords).take(5)
    return ProgressComparison(
      currentRecord = record,
      previousRecord = previousRecord,
      bestRecord = if (isPersonalBest) record else previousBest,
      recentRecords = recent,
      improvementStatus = baseStatus,
      summaryText = record.summaryText(),
      nextGoalText = record.nextGoalText(),
    )
  }

  private fun updateConfiguration(block: AppConfiguration.() -> AppConfiguration) {
    val nextConfiguration = currentConfiguration().block()
    configurationStore.updateConfiguration(nextConfiguration)
  }

  private suspend fun submitAccountRequest(block: suspend () -> Unit) {
    mutableAccountState.update {
      it.copy(
        accountIsSubmitting = true,
        accountForm = it.accountForm.copy(errorMessage = null),
        accountMessage = null,
      )
    }
    runCatching {
      block()
    }.onFailure { error ->
      val message = error.toAccountErrorMessage()
      mutableAccountState.update {
        it.copy(accountForm = it.accountForm.copy(errorMessage = message))
      }
    }
    mutableAccountState.update { it.copy(accountIsSubmitting = false) }
  }

  private fun upsertCurrentAccount(account: UserAccount, message: AccountMessage) {
    recordStore.insertAccount(account)
    mutableAccountState.update {
      val accounts = it.accounts.filterNot { existing -> existing.userId == account.userId } + account
      it.copy(
        accounts = accounts,
        currentUserId = account.userId,
        accountForm = AccountForm(nickname = account.nickname, gender = account.gender),
        accountMessage = message,
        showLinkLocalRecordsDialog = currentRecordsState().unlinkedLocalRecordCount > 0,
      )
    }
  }
}

private fun validateRegistration(form: AccountForm): String? = when {
  form.nickname.trim().length !in 2..12 -> "昵称需为 2 到 12 个字符"
  form.password.length < 6 -> "密码至少 6 位"
  form.password != form.confirmPassword -> "两次密码不一致"
  !form.agreementAccepted -> "请先同意用户协议和隐私说明"
  else -> null
}

private fun improvementStatus(
  currentElapsedMillis: Long,
  currentErrorCount: Int,
  previousRecord: TrainingRecord?,
  isPersonalBest: Boolean,
): ImprovementStatus {
  if (previousRecord == null) return ImprovementStatus.FirstRecord
  if (isPersonalBest) return ImprovementStatus.PersonalBest
  val minEffectiveDeltaMillis = max(300L, (previousRecord.elapsedTimeMillis * 0.01).toLong())
  val timeDelta = currentElapsedMillis - previousRecord.elapsedTimeMillis
  val errorDelta = currentErrorCount - previousRecord.errorCount
  val faster = timeDelta <= -minEffectiveDeltaMillis
  val slower = timeDelta >= minEffectiveDeltaMillis
  return when {
    faster && errorDelta <= 0 -> ImprovementStatus.ImprovedSpeed
    abs(timeDelta) < minEffectiveDeltaMillis && errorDelta < 0 -> ImprovementStatus.ImprovedAccuracy
    faster && errorDelta > 0 -> ImprovementStatus.Mixed
    !slower && errorDelta == 0 -> ImprovementStatus.Stable
    else -> ImprovementStatus.Declined
  }
}

private fun TrainingRecord.summaryText(): String = when (improvementStatus) {
  ImprovementStatus.FirstRecord -> "这是你的首次 ${gridSpec.size}x${gridSpec.size} ${markMode.shortName()}训练，后续会展示进步趋势。"
  ImprovementStatus.PersonalBest -> "刷新了当前条件下的个人最佳成绩。"
  ImprovementStatus.ImprovedSpeed -> "本次比上次快了 ${timeDeltaMillis.fastDeltaText()}，视觉搜索速度有所提升。"
  ImprovementStatus.ImprovedAccuracy -> "本次速度基本稳定，错误减少 ${abs(errorDelta ?: 0)} 次，点击准确性有所提升。"
  ImprovementStatus.Stable -> "本次表现稳定，可以继续保持当前节奏。"
  ImprovementStatus.Mixed -> "本次完成时间更快，但错误次数增加了 ${errorDelta ?: 0} 次。"
  ImprovementStatus.Declined -> "本次表现略有波动，建议下一轮先保证准确率。"
}

private fun TrainingRecord.nextGoalText(): String = when (improvementStatus) {
  ImprovementStatus.FirstRecord -> "再完成 1 次同规格训练，建立对比基准。"
  ImprovementStatus.PersonalBest -> "尝试稳定在当前最佳成绩附近。"
  ImprovementStatus.ImprovedSpeed -> "保持当前节奏，错误次数不增加。"
  ImprovementStatus.ImprovedAccuracy -> "保持低错误的前提下略微提速。"
  ImprovementStatus.Stable -> "尝试比当前时间快 0.5 秒。"
  ImprovementStatus.Mixed, ImprovementStatus.Declined -> "优先降低错误次数，不急于提速。"
}

private fun Long?.fastDeltaText(): String {
  val value = abs(this ?: 0L)
  val seconds = value / 1000
  val centis = (value % 1000) / 10
  return "$seconds.${if (centis < 10) "0$centis" else centis} 秒"
}

private fun MarkMode.shortName(): String = when (this) {
  MarkMode.BriefFeedbackOnly -> "标准"
  MarkMode.AssistedMarking -> "辅助"
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
