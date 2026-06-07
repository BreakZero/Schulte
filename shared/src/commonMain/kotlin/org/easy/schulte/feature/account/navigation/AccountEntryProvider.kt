package org.easy.schulte.feature.account.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.easy.schulte.feature.account.AccountSettingsRoot
import org.easy.schulte.feature.account.EditProfileRoot
import org.easy.schulte.feature.account.LoginRoot
import org.easy.schulte.feature.account.PkSoonRoot
import org.easy.schulte.feature.account.ProfileRoot
import org.easy.schulte.feature.account.RegisterRoot
import org.easy.schulte.navigation.AccountSettingsRoute
import org.easy.schulte.navigation.AppNavigator
import org.easy.schulte.navigation.ConfigRoute
import org.easy.schulte.navigation.EditProfileRoute
import org.easy.schulte.navigation.LoginRoute
import org.easy.schulte.navigation.PkSoonRoute
import org.easy.schulte.navigation.ProfileRoute
import org.easy.schulte.navigation.RegisterRoute
import org.easy.schulte.navigation.SettingsRoute
import org.easy.schulte.navigation.TrainingRecordsRoute

fun EntryProviderScope<NavKey>.accountEntryProvider(
  navigator: AppNavigator,
) {
  entry<ProfileRoute> {
    ProfileRoot(
      onBack = navigator::navigateUp,
      onOpenLogin = { navigator.navigate(LoginRoute) },
      onOpenRegister = { navigator.navigate(RegisterRoute) },
      onOpenEditProfile = { navigator.navigate(EditProfileRoute) },
      onOpenAccountSettings = { navigator.navigate(AccountSettingsRoute) },
      onOpenRecords = { navigator.navigate(TrainingRecordsRoute) },
      onOpenAiSettings = { navigator.navigate(SettingsRoute) },
      onOpenPkSoon = { navigator.navigate(PkSoonRoute) },
      onContinueTraining = { navigator.resetTo(ConfigRoute) },
    )
  }
  entry<LoginRoute> {
    LoginRoot(
      onBack = navigator::navigateUp,
      onOpenRegister = { navigator.replaceTop(RegisterRoute) },
      onOpenProfile = { navigator.resetTo(ProfileRoute) },
    )
  }
  entry<RegisterRoute> {
    RegisterRoot(
      onBack = navigator::navigateUp,
      onOpenLogin = { navigator.replaceTop(LoginRoute) },
      onOpenProfile = { navigator.resetTo(ProfileRoute) },
    )
  }
  entry<EditProfileRoute> {
    EditProfileRoot(onBack = navigator::navigateUp)
  }
  entry<AccountSettingsRoute> {
    AccountSettingsRoot(onBack = navigator::navigateUp)
  }
  entry<PkSoonRoute> {
    PkSoonRoot(onBack = navigator::navigateUp)
  }
}
