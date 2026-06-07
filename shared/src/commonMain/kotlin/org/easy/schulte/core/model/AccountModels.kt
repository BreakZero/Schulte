package org.easy.schulte.core.model

data class UserAccount(
  val userId: String,
  val registerId: String,
  val nickname: String,
  val password: String,
  val gender: Gender = Gender.Private,
  val createdAt: Long,
)

enum class Gender {
  Male,
  Female,
  Private,
}

data class AccountForm(
  val registerId: String = "",
  val nickname: String = "",
  val password: String = "",
  val confirmPassword: String = "",
  val gender: Gender = Gender.Private,
  val agreementAccepted: Boolean = false,
  val errorMessage: String? = null,
)

data class CompetitiveProfile(
  val rankName: String = "未定级",
  val rankPoints: String = "暂无",
  val winRateText: String = "暂无对战数据",
  val totalMatches: Int = 0,
  val wins: Int = 0,
  val losses: Int = 0,
  val draws: Int = 0,
)

enum class AccountMessage {
  Registered,
  LoggedIn,
  ProfileSaved,
  LocalRecordsLinked,
  LoggedOut,
  RegisterIdCopied,
}
