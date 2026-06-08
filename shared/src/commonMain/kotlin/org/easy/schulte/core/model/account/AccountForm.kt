package org.easy.schulte.core.model.account

import org.easy.schulte.core.model.account.enums.Gender

data class AccountForm(
  val registerId: String = "",
  val nickname: String = "",
  val password: String = "",
  val confirmPassword: String = "",
  val gender: Gender = Gender.Private,
  val agreementAccepted: Boolean = false,
  val errorMessage: String? = null,
)
