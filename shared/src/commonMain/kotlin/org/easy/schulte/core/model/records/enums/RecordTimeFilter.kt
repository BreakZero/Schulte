package org.easy.schulte.core.model.records.enums

enum class RecordTimeFilter(val days: Int?) {
  All(null),
  Last7Days(7),
  Last30Days(30),
}
