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
import org.easy.schulte.app.SchulteViewModel
import org.easy.schulte.core.model.SchulteEvent
import org.easy.schulte.core.model.Screen
import org.easy.schulte.core.ui.PageBackground
import org.easy.schulte.feature.advice.AiAdviceScreen
import org.easy.schulte.feature.config.ConfigScreen
import org.easy.schulte.feature.report.ReportScreen
import org.easy.schulte.feature.settings.SettingsScreen
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

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is SchulteEvent.TrainingCompleted -> Unit
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PageBackground,
    ) {
        when (state.screen) {
            Screen.Config -> ConfigScreen(state = state, onAction = viewModel::onConfigAction)
            Screen.Training -> TrainingScreen(state = state, onAction = viewModel::onTrainingAction)
            Screen.Report -> ReportScreen(state = state, onAction = viewModel::onReportAction)
            Screen.AiAdvice -> AiAdviceScreen(state = state, onAction = viewModel::onAdviceAction)
            Screen.Settings -> SettingsScreen(state = state, onAction = viewModel::onSettingsAction)
        }
    }
}
