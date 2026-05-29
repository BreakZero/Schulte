package org.easy.schulte

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.easy.schulte.core.model.AiAdviceRoute
import org.easy.schulte.core.model.AppRoute
import org.easy.schulte.core.model.ConfigRoute
import org.easy.schulte.core.model.ReportRoute
import org.easy.schulte.core.model.SettingsRoute
import org.easy.schulte.core.model.TrainingRoute
import org.easy.schulte.core.ui.PageBackground
import org.easy.schulte.di.appModule
import org.easy.schulte.feature.advice.AiAdviceRoot
import org.easy.schulte.feature.config.ConfigRoot
import org.easy.schulte.feature.report.ReportRoot
import org.easy.schulte.feature.settings.SettingsRoot
import org.easy.schulte.feature.training.TrainingRoot
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
  KoinApplication(
    configuration = koinConfiguration {
      modules(appModule)
    },
  ) {
    MaterialTheme {
      SchulteRoot()
    }
  }
}

@Composable
internal fun SchulteRoot() {
  val backStack = rememberNavBackStack(
    configuration = appSavedStateConfiguration,
    ConfigRoute,
  )

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = PageBackground,
  ) {
    NavDisplay(
      backStack = backStack,
      modifier = Modifier.fillMaxSize(),
      onBack = {
        when (backStack.lastOrNull()) {
          TrainingRoute,
          ReportRoute,
          -> backStack.resetTo(ConfigRoute)

          else -> backStack.navigateUp()
        }
      },
      entryProvider = entryProvider {
        entry<ConfigRoute> {
          ConfigRoot(
            onStartTraining = { backStack.resetTo(TrainingRoute) },
            onOpenSettings = { backStack.navigate(SettingsRoute) },
          )
        }
        entry<TrainingRoute> {
          TrainingRoot(
            onTrainingCompleted = { backStack.replaceTop(ReportRoute) },
            onTrainingExited = { backStack.resetTo(ConfigRoute) },
          )
        }
        entry<ReportRoute> {
          ReportRoot(
            onRestartTraining = { backStack.resetTo(TrainingRoute) },
            onGenerateAiAnalysis = { backStack.navigate(AiAdviceRoute) },
            onBackToConfig = { backStack.resetTo(ConfigRoute) },
            onOpenSettings = { backStack.navigate(SettingsRoute) },
          )
        }
        entry<AiAdviceRoute> {
          AiAdviceRoot(
            onRestartTraining = { backStack.resetTo(TrainingRoute) },
            onBackToReport = { backStack.navigateUp() },
          )
        }
        entry<SettingsRoute> {
          SettingsRoot(onCloseSettings = { backStack.navigateUp() })
        }
      },
    )
  }
}

private val appSavedStateConfiguration = SavedStateConfiguration {
  serializersModule = SerializersModule {
    polymorphic(NavKey::class) {
      subclass(ConfigRoute::class, ConfigRoute.serializer())
      subclass(TrainingRoute::class, TrainingRoute.serializer())
      subclass(ReportRoute::class, ReportRoute.serializer())
      subclass(AiAdviceRoute::class, AiAdviceRoute.serializer())
      subclass(SettingsRoute::class, SettingsRoute.serializer())
    }
  }
}

private fun MutableList<NavKey>.navigate(route: AppRoute) {
  if (lastOrNull() != route) add(route)
}

private fun MutableList<NavKey>.replaceTop(route: AppRoute) {
  if (isNotEmpty()) removeLast()
  add(route)
}

private fun MutableList<NavKey>.resetTo(route: AppRoute) {
  clear()
  add(ConfigRoute)
  if (route != ConfigRoute) add(route)
}

private fun MutableList<NavKey>.navigateUp() {
  if (size > 1) removeLast()
}
