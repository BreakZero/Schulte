package org.easy.schulte.feature.advice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.ui.FocusBlue
import org.easy.schulte.core.ui.InfoCard
import org.easy.schulte.core.ui.KeyValueRow
import org.easy.schulte.core.ui.SchulteCard
import org.easy.schulte.core.ui.SchulteScaffold
import org.easy.schulte.core.ui.SectionTitle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import schulte.shared.generated.resources.*

@Composable
internal fun AiAdviceRoot(
  onRestartTraining: () -> Unit,
  onBackToReport: () -> Unit,
  viewModel: AdviceViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel) {
    viewModel.events.collect { event ->
      when (event) {
        AdviceEvent.RestartTraining -> onRestartTraining()
        AdviceEvent.BackToReport -> onBackToReport()
        AdviceEvent.AiAnalysisCompleted -> Unit
      }
    }
  }

  LaunchedEffect(Unit) {
    if (state.aiAnalysis == null) {
      viewModel.generateAiAnalysis()
    }
  }

  AiAdviceScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun AiAdviceScreen(
  state: SchulteState,
  onAction: (AdviceAction) -> Unit,
) {
  val analysis = state.aiAnalysis ?: return
  SchulteScaffold(
    title = stringResource(Res.string.advice_title),
    onNavigationClick = { onAction(AdviceAction.BackToReport) },
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      InfoCard(stringResource(Res.string.advice_summary), analysis.summary)
      InfoCard(stringResource(Res.string.advice_speed), analysis.speed)
      InfoCard(stringResource(Res.string.advice_errors), analysis.errors)
      SchulteCard {
        SectionTitle(stringResource(Res.string.section_next_goal))
        KeyValueRow(stringResource(Res.string.advice_target_time), analysis.nextTimeGoal)
        KeyValueRow(stringResource(Res.string.advice_error_count), analysis.nextErrorGoal)
        KeyValueRow(stringResource(Res.string.advice_recommended_spec), analysis.recommendedSpec)
      }
      SchulteCard {
        SectionTitle(stringResource(Res.string.section_training_suggestions))
        analysis.suggestions.forEach {
          Text(stringResource(Res.string.bullet_item, it), color = Color(0xFF223044), modifier = Modifier.padding(top = 8.dp))
        }
      }
      Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Button(
          onClick = { onAction(AdviceAction.RestartTraining) },
          modifier = Modifier
            .weight(1f)
            .height(50.dp),
          colors = ButtonDefaults.buttonColors(containerColor = FocusBlue),
          shape = RoundedCornerShape(8.dp),
        ) {
          Text(stringResource(Res.string.action_train_again))
        }
        OutlinedButton(
          onClick = { onAction(AdviceAction.BackToReport) },
          modifier = Modifier
            .weight(1f)
            .height(50.dp),
          shape = RoundedCornerShape(8.dp),
        ) {
          Text(stringResource(Res.string.action_back_report))
        }
      }
    }
  }
}
