package org.easy.schulte.core.model.records.enums

import org.easy.schulte.core.model.training.enums.LayoutMode

enum class RecordLayoutFilter(val layoutMode: LayoutMode?) {
  All(null),
  Static(LayoutMode.Static),
  Dynamic(LayoutMode.ShuffleAfterCorrectTap),
}
