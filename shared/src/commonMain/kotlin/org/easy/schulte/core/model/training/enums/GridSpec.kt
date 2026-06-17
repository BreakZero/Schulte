package org.easy.schulte.core.model.training.enums

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
