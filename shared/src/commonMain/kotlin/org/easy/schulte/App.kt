package org.easy.schulte

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.easy.schulte.app.SchulteViewModel
import org.easy.schulte.core.model.AiAdviceRoute
import org.easy.schulte.core.model.AppRoute
import org.easy.schulte.core.model.ConfigRoute
import org.easy.schulte.core.model.NavigationMode
import org.easy.schulte.core.model.ReportRoute
import org.easy.schulte.core.model.SchulteEvent
import org.easy.schulte.core.model.SettingsRoute
import org.easy.schulte.core.model.TrainingRoute
import org.easy.schulte.core.ui.PageBackground
import org.easy.schulte.feature.advice.AiAdviceScreen
import org.easy.schulte.feature.config.ConfigScreen
import org.easy.schulte.feature.report.ReportAction
import org.easy.schulte.feature.report.ReportScreen
import org.easy.schulte.feature.settings.SettingsScreen
import org.easy.schulte.feature.training.TrainingAction
import org.easy.schulte.feature.training.TrainingScreen

@Composable
@Preview
fun App() {
  MaterialTheme {
    val viewModel = viewModel { SchulteViewModel() }
    SchulteRoot(viewModel = viewModel)
  }
}

@Composable
fun SchulteRoot(viewModel: SchulteViewModel = viewModel { SchulteViewModel() }) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val backStack = rememberNavBackStack(
    configuration = appSavedStateConfiguration,
    ConfigRoute,
  )

  LaunchedEffect(viewModel) {
    viewModel.events.collect { event ->
      when (event) {
        is SchulteEvent.TrainingCompleted -> backStack.replaceTop(ReportRoute)

        SchulteEvent.AiAnalysisCompleted -> backStack.navigate(AiAdviceRoute)

        is SchulteEvent.Navigate -> when (event.mode) {
          NavigationMode.Push -> backStack.navigate(event.route)
          NavigationMode.ReplaceTop -> backStack.replaceTop(event.route)
          NavigationMode.ResetToRoot -> backStack.resetTo(event.route)
        }

        SchulteEvent.NavigateUp -> backStack.navigateUp()
      }
    }
  }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = PageBackground,
  ) {
    NavDisplay(
      backStack = backStack,
      modifier = Modifier.fillMaxSize(),
      onBack = {
        when (backStack.lastOrNull()) {
          TrainingRoute -> viewModel.onTrainingAction(TrainingAction.ExitTraining)
          ReportRoute -> viewModel.onReportAction(ReportAction.BackToConfig)
          else -> backStack.navigateUp()
        }
      },
      entryProvider = entryProvider {
        entry<ConfigRoute> {
          ConfigScreen(state = state, onAction = viewModel::onConfigAction)
        }
        entry<TrainingRoute> {
          TrainingScreen(state = state, onAction = viewModel::onTrainingAction)
        }
        entry<ReportRoute> {
          ReportScreen(state = state, onAction = viewModel::onReportAction)
        }
        entry<AiAdviceRoute> {
          AiAdviceScreen(state = state, onAction = viewModel::onAdviceAction)
        }
        entry<SettingsRoute> {
          SettingsScreen(state = state, onAction = viewModel::onSettingsAction)
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
