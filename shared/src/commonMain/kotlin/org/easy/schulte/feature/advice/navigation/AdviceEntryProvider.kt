package org.easy.schulte.feature.advice.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.easy.schulte.feature.advice.AiAdviceRoot
import org.easy.schulte.navigation.AiAdviceRoute
import org.easy.schulte.navigation.AppNavigator
import org.easy.schulte.navigation.TrainingRoute

fun EntryProviderScope<NavKey>.adviceEntryProvider(
  navigator: AppNavigator,
) {
  entry<AiAdviceRoute> {
    AiAdviceRoot(
      onRestartTraining = { navigator.resetTo(TrainingRoute) },
      onBackToReport = navigator::navigateUp,
    )
  }
}
