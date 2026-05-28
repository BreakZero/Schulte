package org.easy.schulte.core.model

enum class GridSpec(
  val size: Int,
) {
  Three(3),
  Four(4),
  Five(5),
  Seven(7),
  ;

  val count: Int get() = size * size
}

enum class AgeGroup {
  Child,
  Junior,
  Teen,
  Adult,
}

enum class MarkMode {
  BriefFeedbackOnly,
  AssistedMarking,
}

data class CellFeedback(
  val value: Int,
  val isCorrect: Boolean,
)
