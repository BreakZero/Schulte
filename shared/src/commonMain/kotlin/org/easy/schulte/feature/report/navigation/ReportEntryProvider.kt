package org.easy.schulte.feature.report.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.easy.schulte.feature.report.ReportRoot
import org.easy.schulte.navigation.AiAdviceRoute
import org.easy.schulte.navigation.AppNavigator
import org.easy.schulte.navigation.ConfigRoute
import org.easy.schulte.navigation.ProfileRoute
import org.easy.schulte.navigation.ReportRoute
import org.easy.schulte.navigation.SettingsRoute
import org.easy.schulte.navigation.TrainingRecordsRoute
import org.easy.schulte.navigation.TrainingRoute

fun EntryProviderScope<NavKey>.reportEntryProvider(
  navigator: AppNavigator,
) {
  entry<ReportRoute> {
    ReportRoot(
      onRestartTraining = { navigator.resetTo(TrainingRoute) },
      onGenerateAiAnalysis = { navigator.navigate(AiAdviceRoute) },
      onOpenRecords = { navigator.navigate(TrainingRecordsRoute) },
      onBackToConfig = { navigator.resetTo(ConfigRoute) },
      onOpenSettings = { navigator.navigate(SettingsRoute) },
      onOpenProfile = { navigator.navigate(ProfileRoute) },
    )
  }
}
