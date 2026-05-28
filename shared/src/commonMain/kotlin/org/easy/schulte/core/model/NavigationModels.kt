package org.easy.schulte.core.model

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey

@Serializable
data object ConfigRoute : AppRoute

@Serializable
data object TrainingRoute : AppRoute

@Serializable
data object ReportRoute : AppRoute

@Serializable
data object AiAdviceRoute : AppRoute

@Serializable
data object SettingsRoute : AppRoute
