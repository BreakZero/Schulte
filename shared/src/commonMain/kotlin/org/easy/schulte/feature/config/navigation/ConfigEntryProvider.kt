package org.easy.schulte.feature.config.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.easy.schulte.feature.config.ConfigRoot
import org.easy.schulte.navigation.AppNavigator
import org.easy.schulte.navigation.ConfigRoute
import org.easy.schulte.navigation.SettingsRoute
import org.easy.schulte.navigation.TrainingRecordsRoute
import org.easy.schulte.navigation.TrainingRoute

fun EntryProviderScope<NavKey>.configEntryProvider(
  navigator: AppNavigator,
) {
  entry<ConfigRoute> {
    ConfigRoot(
      onStartTraining = { navigator.resetTo(TrainingRoute) },
      onOpenRecords = { navigator.navigate(TrainingRecordsRoute) },
      onOpenSettings = { navigator.navigate(SettingsRoute) },
    )
  }
}
