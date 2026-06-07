package org.easy.schulte.navigation

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

@Serializable
data object TrainingRecordsRoute : AppRoute

@Serializable
data object ProfileRoute : AppRoute

@Serializable
data object LoginRoute : AppRoute

@Serializable
data object RegisterRoute : AppRoute

@Serializable
data object EditProfileRoute : AppRoute

@Serializable
data object AccountSettingsRoute : AppRoute

@Serializable
data object PkSoonRoute : AppRoute
