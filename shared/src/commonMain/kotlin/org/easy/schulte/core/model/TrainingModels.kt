package org.easy.schulte.core.model

enum class GridSpec(
  val size: Int,
  val title: String,
  val difficulty: String,
) {
  Three(3, "3x3", "入门"),
  Four(4, "4x4", "初级"),
  Five(5, "5x5", "中级"),
  Seven(7, "7x7", "高级"),
  ;

  val count: Int get() = size * size
}

enum class AgeGroup(val title: String) {
  Child("3~5岁"),
  Junior("6~10岁"),
  Teen("11~17岁"),
  Adult("18岁以上"),
}

enum class MarkMode(val title: String, val description: String) {
  BriefFeedbackOnly("标准模式", "点击后仅短暂反馈，不持续标记"),
  AssistedMarking("辅助模式", "显示已完成标记，成绩仅供练习参考"),
}

data class CellFeedback(
  val value: Int,
  val isCorrect: Boolean,
)
