package org.easy.schulte.core.model

sealed interface SchulteEvent {
    data class TrainingCompleted(val report: TrainingReport) : SchulteEvent
}
