package org.easy.schulte.core.data

import org.easy.schulte.core.model.Gender

internal interface AccountRepository : FeatureStateRepository {
  fun updateLoginRegisterId(value: String)
  fun updateAccountNickname(value: String)
  fun updateAccountPassword(value: String)
  fun updateAccountConfirmPassword(value: String)
  fun updateAccountGender(gender: Gender)
  fun updateAgreementAccepted(accepted: Boolean)
  fun clearAccountForm()
  suspend fun registerAccount()
  suspend fun loginAccount()
  suspend fun updateCurrentProfile()
  fun requestLinkLocalRecords()
  suspend fun linkLocalRecords()
  fun dismissLinkLocalRecords()
  fun requestLogout()
  fun cancelLogout()
  suspend fun logout()
  fun markRegisterIdCopied()
  fun clearAccountMessage()
}
