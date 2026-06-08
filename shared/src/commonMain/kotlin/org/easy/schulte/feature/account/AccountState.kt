package org.easy.schulte.feature.account

import org.easy.schulte.core.model.account.AccountForm
import org.easy.schulte.core.model.account.CompetitiveProfile
import org.easy.schulte.core.model.account.UserAccount
import org.easy.schulte.core.model.account.enums.AccountMessage
import org.easy.schulte.core.model.records.TrainingRecordSummary

internal data class AccountState(
  val currentUser: UserAccount? = null,
  val isLoggedIn: Boolean = false,
  val accountForm: AccountForm = AccountForm(),
  val accountMessage: AccountMessage? = null,
  val isSubmitting: Boolean = false,
  val showLinkLocalRecordsDialog: Boolean = false,
  val showLogoutDialog: Boolean = false,
  val unlinkedLocalRecordCount: Int = 0,
  val recordSummary: TrainingRecordSummary = TrainingRecordSummary(),
  val assistedTrainingCount: Int = 0,
  val competitiveProfile: CompetitiveProfile = CompetitiveProfile(),
)
