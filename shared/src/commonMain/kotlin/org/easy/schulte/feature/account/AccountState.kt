package org.easy.schulte.feature.account

import org.easy.schulte.core.model.AccountForm
import org.easy.schulte.core.model.AccountMessage
import org.easy.schulte.core.model.CompetitiveProfile
import org.easy.schulte.core.model.TrainingRecordSummary
import org.easy.schulte.core.model.UserAccount

internal data class AccountState(
  val currentUser: UserAccount? = null,
  val isLoggedIn: Boolean = false,
  val accountForm: AccountForm = AccountForm(),
  val accountMessage: AccountMessage? = null,
  val showLinkLocalRecordsDialog: Boolean = false,
  val showLogoutDialog: Boolean = false,
  val unlinkedLocalRecordCount: Int = 0,
  val recordSummary: TrainingRecordSummary = TrainingRecordSummary(),
  val assistedTrainingCount: Int = 0,
  val competitiveProfile: CompetitiveProfile = CompetitiveProfile(),
)
