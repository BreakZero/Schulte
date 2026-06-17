package org.easy.schulte.core.model.runtime

import org.easy.schulte.core.model.account.AccountForm
import org.easy.schulte.core.model.account.CompetitiveProfile
import org.easy.schulte.core.model.account.UserAccount
import org.easy.schulte.core.model.account.enums.AccountMessage

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
