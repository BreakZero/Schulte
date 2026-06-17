package org.easy.schulte.core.model.records.enums

import org.easy.schulte.core.model.training.enums.MarkMode

enum class RecordModeFilter(val markMode: MarkMode?) {
  All(null),
  Standard(MarkMode.BriefFeedbackOnly),
  Assisted(MarkMode.AssistedMarking),
}
