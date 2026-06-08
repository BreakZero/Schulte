package org.easy.schulte.core.model.records.enums

import org.easy.schulte.core.model.training.enums.GridSpec

enum class RecordGridFilter(val gridSpec: GridSpec?) {
  All(null),
  Three(GridSpec.Three),
  Four(GridSpec.Four),
  Five(GridSpec.Five),
  Seven(GridSpec.Seven),
}
