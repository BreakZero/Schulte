package org.easy.schulte.feature.account

import org.easy.schulte.core.model.Gender

internal sealed interface AccountAction {
  data object Back : AccountAction
  data object OpenLogin : AccountAction
  data object OpenRegister : AccountAction
  data object OpenEditProfile : AccountAction
  data object OpenAccountSettings : AccountAction
  data object OpenRecords : AccountAction
  data object OpenAiSettings : AccountAction
  data object OpenPkSoon : AccountAction
  data object ContinueTraining : AccountAction
  data class UpdateRegisterId(val value: String) : AccountAction
  data class UpdateNickname(val value: String) : AccountAction
  data class UpdatePassword(val value: String) : AccountAction
  data class UpdateConfirmPassword(val value: String) : AccountAction
  data class SelectGender(val gender: Gender) : AccountAction
  data class AcceptAgreement(val accepted: Boolean) : AccountAction
  data object SubmitLogin : AccountAction
  data object SubmitRegister : AccountAction
  data object SaveProfile : AccountAction
  data object CopyRegisterId : AccountAction
  data object RequestLinkLocalRecords : AccountAction
  data object LinkLocalRecords : AccountAction
  data object DismissLinkLocalRecords : AccountAction
  data object RequestLogout : AccountAction
  data object CancelLogout : AccountAction
  data object ConfirmLogout : AccountAction
  data object ClearMessage : AccountAction
}
