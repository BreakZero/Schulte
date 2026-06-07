package org.easy.schulte.core.model

data class SchulteState(
  val numbers: List<Int> = emptyList(),
  val currentTarget: Int = 1,
  val completedNumbers: Set<Int> = emptySet(),
  val elapsedMillis: Long = 0L,
  val errorCount: Int = 0,
  val lastFeedback: CellFeedback? = null,
  val report: TrainingReport? = null,
  val apiKeyVisible: Boolean = false,
  val settingsMessage: SettingsMessage? = null,
  val aiAnalysis: AiAnalysis? = null,
  val aiAnalysisState: AiAnalysisState = AiAnalysisState.Idle,
  val records: List<TrainingRecord> = emptyList(),
  val recordSummary: TrainingRecordSummary = TrainingRecordSummary(),
  val progressComparison: ProgressComparison? = null,
  val showClearRecordsDialog: Boolean = false,
  val accounts: List<UserAccount> = emptyList(),
  val currentUserId: String? = null,
  val accountForm: AccountForm = AccountForm(),
  val accountMessage: AccountMessage? = null,
  val showLinkLocalRecordsDialog: Boolean = false,
  val showLogoutDialog: Boolean = false,
  val competitiveProfile: CompetitiveProfile = CompetitiveProfile(),
)

val SchulteState.currentUser: UserAccount?
  get() = accounts.firstOrNull { it.userId == currentUserId }

val SchulteState.isLoggedIn: Boolean
  get() = currentUser != null

val SchulteState.unlinkedLocalRecordCount: Int
  get() = records.count { it.ownerUserId == null }
