package org.easy.schulte.core.model

data class TrainingRuntimeState(
  val numbers: List<Int> = emptyList(),
  val currentTarget: Int = 1,
  val completedNumbers: Set<Int> = emptySet(),
  val elapsedMillis: Long = 0L,
  val errorCount: Int = 0,
  val lastFeedback: CellFeedback? = null,
)

data class ReportRuntimeState(
  val report: TrainingReport? = null,
  val aiAnalysis: AiAnalysis? = null,
  val aiAnalysisState: AiAnalysisState = AiAnalysisState.Idle,
  val progressComparison: ProgressComparison? = null,
)

data class SettingsRuntimeState(
  val apiKeyVisible: Boolean = false,
  val settingsMessage: SettingsMessage? = null,
  val showClearRecordsDialog: Boolean = false,
)

data class TrainingRecordsRuntimeState(
  val records: List<TrainingRecord> = emptyList(),
  val recordSummary: TrainingRecordSummary = TrainingRecordSummary(),
)

data class AccountRuntimeState(
  val accounts: List<UserAccount> = emptyList(),
  val currentUserId: String? = null,
  val accountForm: AccountForm = AccountForm(),
  val accountMessage: AccountMessage? = null,
  val accountIsSubmitting: Boolean = false,
  val showLinkLocalRecordsDialog: Boolean = false,
  val showLogoutDialog: Boolean = false,
  val competitiveProfile: CompetitiveProfile = CompetitiveProfile(),
)

val AccountRuntimeState.currentUser: UserAccount?
  get() = accounts.firstOrNull { it.userId == currentUserId }

val AccountRuntimeState.isLoggedIn: Boolean
  get() = currentUser != null

val TrainingRecordsRuntimeState.unlinkedLocalRecordCount: Int
  get() = records.count { it.ownerUserId == null }
