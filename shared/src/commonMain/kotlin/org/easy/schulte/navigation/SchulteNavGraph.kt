package org.easy.schulte.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import org.easy.schulte.feature.account.navigation.accountEntryProvider
import org.easy.schulte.feature.advice.navigation.adviceEntryProvider
import org.easy.schulte.feature.config.navigation.configEntryProvider
import org.easy.schulte.feature.records.navigation.recordsEntryProvider
import org.easy.schulte.feature.report.navigation.reportEntryProvider
import org.easy.schulte.feature.settings.navigation.settingsEntryProvider
import org.easy.schulte.feature.training.navigation.trainingEntryProvider

@Composable
fun SchulteNavGraph(
  modifier: Modifier = Modifier,
) {
  val backStack = rememberNavBackStack(
    configuration = appSavedStateConfiguration,
    StartRoute,
  )
  val navigator = remember(backStack) { AppNavigator(backStack) }

  NavDisplay(
    backStack = backStack,
    modifier = modifier,
    onBack = navigator::onBack,
    entryProvider = entryProvider {
      configEntryProvider(navigator)
      trainingEntryProvider(navigator)
      reportEntryProvider(navigator)
      adviceEntryProvider(navigator)
      settingsEntryProvider(navigator)
      recordsEntryProvider(navigator)
      accountEntryProvider(navigator)
    },
  )
}
