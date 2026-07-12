package org.easy.schulte.core.data

import kotlinx.coroutines.flow.update
import org.easy.schulte.core.model.account.AccountForm
import org.easy.schulte.core.model.account.UserAccount
import org.easy.schulte.core.model.account.enums.AccountMessage
import org.easy.schulte.core.model.account.enums.Gender
import org.easy.schulte.core.model.runtime.currentUser
import org.easy.schulte.core.model.runtime.unlinkedLocalRecordCount
import org.easy.schulte.core.network.AccountApi
import org.easy.schulte.core.network.AuthSession
import org.easy.schulte.core.network.toAccountErrorMessage
import org.easy.schulte.core.security.AccountSession
import org.easy.schulte.core.security.AccountSessionStore

internal class AccountRepositoryImpl(
  private val sharedState: SchulteSharedState,
  private val accountApi: AccountApi,
  private val accountSessionStore: AccountSessionStore,
) : AccountRepository,
  FeatureStateRepository by sharedState {
  private val restoredSession = accountSessionStore.read()
  private var accessToken: String = restoredSession?.accessToken.orEmpty()
  private var refreshToken: String = restoredSession?.refreshToken.orEmpty()

  override fun updateLoginRegisterId(value: String) {
    sharedState.mutableAccountState.update {
      it.copy(accountForm = it.accountForm.copy(registerId = value.trim(), errorMessage = null))
    }
  }

  override fun updateAccountNickname(value: String) {
    updateAccountForm { copy(nickname = value, errorMessage = null) }
  }

  override fun updateAccountPassword(value: String) {
    updateAccountForm { copy(password = value, errorMessage = null) }
  }

  override fun updateAccountConfirmPassword(value: String) {
    updateAccountForm { copy(confirmPassword = value, errorMessage = null) }
  }

  override fun updateAccountGender(gender: Gender) {
    updateAccountForm { copy(gender = gender, errorMessage = null) }
  }

  override fun updateAgreementAccepted(accepted: Boolean) {
    updateAccountForm { copy(agreementAccepted = accepted, errorMessage = null) }
  }

  override fun clearAccountForm() {
    sharedState.mutableAccountState.update {
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
    val state = sharedState.currentAccountState()
    val form = state.accountForm
    val error = validateRegistration(form)
    if (error != null) {
      sharedState.mutableAccountState.update { it.copy(accountForm = form.copy(errorMessage = error)) }
      return
    }
    submitAccountRequest {
      val session = accountApi.register(
        nickname = form.nickname.trim(),
        password = form.password,
        gender = form.gender,
        acceptedTerms = form.agreementAccepted,
      )
      saveSession(session)
      upsertCurrentAccount(session.user, AccountMessage.Registered)
    }
  }

  override suspend fun loginAccount() {
    val form = sharedState.currentAccountState().accountForm
    if (form.registerId.isBlank() || form.password.isBlank()) {
      sharedState.mutableAccountState.update {
        it.copy(accountForm = form.copy(errorMessage = "请输入注册 ID 和密码"))
      }
      return
    }
    submitAccountRequest {
      val session = accountApi.login(registrationId = form.registerId.trim(), password = form.password)
      saveSession(session)
      upsertCurrentAccount(session.user, AccountMessage.LoggedIn)
    }
  }

  override suspend fun updateCurrentProfile() {
    val state = sharedState.currentAccountState()
    val user = state.currentUser ?: return
    val form = state.accountForm
    val nickname = form.nickname.trim()
    if (nickname.isBlank()) {
      sharedState.mutableAccountState.update { it.copy(accountForm = form.copy(errorMessage = "昵称不能为空")) }
      return
    }
    submitAccountRequest {
      val updatedUser = if (accessToken.isNotBlank()) {
        accountApi.updateMe(accessToken = accessToken, nickname = nickname, gender = form.gender).copy(
          userId = user.userId,
          registerId = user.registerId,
        )
      } else {
        user.copy(nickname = nickname, gender = form.gender)
      }
      sharedState.recordStore.updateAccountProfile(updatedUser)
      sharedState.mutableAccountState.update {
        it.copy(
          accounts = it.accounts.map { account -> if (account.userId == user.userId) updatedUser else account },
          accountMessage = AccountMessage.ProfileSaved,
          accountForm = AccountForm(nickname = updatedUser.nickname, gender = updatedUser.gender),
        )
      }
    }
  }

  override fun requestLinkLocalRecords() {
    sharedState.mutableAccountState.update {
      it.copy(showLinkLocalRecordsDialog = sharedState.currentRecordsState().unlinkedLocalRecordCount > 0)
    }
  }

  override suspend fun linkLocalRecords() {
    val userId = sharedState.currentAccountState().currentUserId ?: return
    sharedState.recordStore.updateUnownedRecordsOwner(userId)
    sharedState.mutableAccountState.update {
      it.copy(showLinkLocalRecordsDialog = false, accountMessage = AccountMessage.LocalRecordsLinked)
    }
  }

  override fun dismissLinkLocalRecords() {
    sharedState.mutableAccountState.update { it.copy(showLinkLocalRecordsDialog = false) }
  }

  override fun requestLogout() {
    sharedState.mutableAccountState.update { it.copy(showLogoutDialog = true) }
  }

  override fun cancelLogout() {
    sharedState.mutableAccountState.update { it.copy(showLogoutDialog = false) }
  }

  override suspend fun logout() {
    val token = accessToken
    val refresh = refreshToken
    if (token.isNotBlank() && refresh.isNotBlank()) {
      runCatching { accountApi.logout(token, refresh) }
    }
    accessToken = ""
    refreshToken = ""
    accountSessionStore.clear()
    sharedState.mutableAccountState.update {
      it.copy(
        currentUserId = null,
        showLogoutDialog = false,
        accountForm = AccountForm(),
        accountMessage = AccountMessage.LoggedOut,
      )
    }
  }

  override fun markRegisterIdCopied() {
    sharedState.mutableAccountState.update { it.copy(accountMessage = AccountMessage.RegisterIdCopied) }
  }

  override fun clearAccountMessage() {
    sharedState.mutableAccountState.update { it.copy(accountMessage = null) }
  }

  private fun updateAccountForm(block: AccountForm.() -> AccountForm) {
    sharedState.mutableAccountState.update { it.copy(accountForm = it.accountForm.block()) }
  }

  private fun saveSession(session: AuthSession) {
    accessToken = session.accessToken
    refreshToken = session.refreshToken
    accountSessionStore.save(
      AccountSession(
        accessToken = session.accessToken,
        refreshToken = session.refreshToken,
        userId = session.user.userId,
      ),
    )
  }

  private suspend fun submitAccountRequest(block: suspend () -> Unit) {
    sharedState.mutableAccountState.update {
      it.copy(
        accountIsSubmitting = true,
        accountForm = it.accountForm.copy(errorMessage = null),
        accountMessage = null,
      )
    }
    runCatching { block() }.onFailure { error ->
      val message = error.toAccountErrorMessage()
      sharedState.mutableAccountState.update { it.copy(accountForm = it.accountForm.copy(errorMessage = message)) }
    }
    sharedState.mutableAccountState.update { it.copy(accountIsSubmitting = false) }
  }

  private fun upsertCurrentAccount(account: UserAccount, message: AccountMessage) {
    sharedState.recordStore.insertAccount(account)
    sharedState.mutableAccountState.update {
      val accounts = it.accounts.filterNot { existing -> existing.userId == account.userId } + account
      it.copy(
        accounts = accounts,
        currentUserId = account.userId,
        accountForm = AccountForm(nickname = account.nickname, gender = account.gender),
        accountMessage = message,
        showLinkLocalRecordsDialog = sharedState.currentRecordsState().unlinkedLocalRecordCount > 0,
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
