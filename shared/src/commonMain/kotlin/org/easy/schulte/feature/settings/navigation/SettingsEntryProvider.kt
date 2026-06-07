package org.easy.schulte.feature.settings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.easy.schulte.feature.settings.SettingsRoot
import org.easy.schulte.navigation.AppNavigator
import org.easy.schulte.navigation.ProfileRoute
import org.easy.schulte.navigation.SettingsRoute

fun EntryProviderScope<NavKey>.settingsEntryProvider(
  navigator: AppNavigator,
) {
  entry<SettingsRoute> {
    SettingsRoot(
      onCloseSettings = navigator::navigateUp,
      onOpenProfile = { navigator.navigate(ProfileRoute) },
    )
  }
}
