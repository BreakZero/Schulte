package org.easy.schulte.feature.training

import org.easy.schulte.core.model.TrainingReport

internal sealed interface TrainingEvent {
  data class Completed(val report: TrainingReport) : TrainingEvent
  data object Exited : TrainingEvent
}
