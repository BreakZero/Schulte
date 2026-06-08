package org.easy.schulte.core.model.account

import org.easy.schulte.core.model.account.enums.Gender

data class UserAccount(
  val userId: String,
  val registerId: String,
  val nickname: String,
  val password: String,
  val gender: Gender = Gender.Private,
  val createdAt: Long,
)
