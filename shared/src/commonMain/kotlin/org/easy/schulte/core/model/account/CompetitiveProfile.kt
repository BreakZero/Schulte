package org.easy.schulte.core.model.account

data class CompetitiveProfile(
  val rankName: String = "未定级",
  val rankPoints: String = "暂无",
  val winRateText: String = "暂无对战数据",
  val totalMatches: Int = 0,
  val wins: Int = 0,
  val losses: Int = 0,
  val draws: Int = 0,
)
