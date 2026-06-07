package org.easy.schulte.feature.training.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.easy.schulte.feature.training.TrainingRoot
import org.easy.schulte.navigation.AppNavigator
import org.easy.schulte.navigation.ConfigRoute
import org.easy.schulte.navigation.ReportRoute
import org.easy.schulte.navigation.TrainingRoute

fun EntryProviderScope<NavKey>.trainingEntryProvider(
  navigator: AppNavigator,
) {
  entry<TrainingRoute> {
    TrainingRoot(
      onTrainingCompleted = { navigator.replaceTop(ReportRoute) },
      onTrainingExited = { navigator.resetTo(ConfigRoute) },
    )
  }
}
