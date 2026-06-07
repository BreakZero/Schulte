package org.easy.schulte.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val appSavedStateConfiguration = SavedStateConfiguration {
  serializersModule = SerializersModule {
    polymorphic(NavKey::class) {
      subclass(ConfigRoute::class, ConfigRoute.serializer())
      subclass(TrainingRoute::class, TrainingRoute.serializer())
      subclass(ReportRoute::class, ReportRoute.serializer())
      subclass(AiAdviceRoute::class, AiAdviceRoute.serializer())
      subclass(SettingsRoute::class, SettingsRoute.serializer())
      subclass(TrainingRecordsRoute::class, TrainingRecordsRoute.serializer())
      subclass(ProfileRoute::class, ProfileRoute.serializer())
      subclass(LoginRoute::class, LoginRoute.serializer())
      subclass(RegisterRoute::class, RegisterRoute.serializer())
      subclass(EditProfileRoute::class, EditProfileRoute.serializer())
      subclass(AccountSettingsRoute::class, AccountSettingsRoute.serializer())
      subclass(PkSoonRoute::class, PkSoonRoute.serializer())
    }
  }
}
