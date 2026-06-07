package org.easy.schulte.feature.records.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.easy.schulte.feature.records.TrainingRecordsRoot
import org.easy.schulte.navigation.AppNavigator
import org.easy.schulte.navigation.TrainingRecordsRoute
import org.easy.schulte.navigation.TrainingRoute

fun EntryProviderScope<NavKey>.recordsEntryProvider(
  navigator: AppNavigator,
) {
  entry<TrainingRecordsRoute> {
    TrainingRecordsRoot(
      onBack = navigator::navigateUp,
      onStartTraining = { navigator.resetTo(TrainingRoute) },
    )
  }
}
