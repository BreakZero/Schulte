package org.easy.schulte.feature.training

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.ui.CardBackground
import org.easy.schulte.core.ui.ErrorRed
import org.easy.schulte.core.ui.InfoCard
import org.easy.schulte.core.ui.LineColor
import org.easy.schulte.core.ui.QuietText
import org.easy.schulte.core.ui.SchulteScaffold
import org.easy.schulte.core.ui.StatCard
import org.easy.schulte.core.ui.formatTimer
import org.easy.schulte.core.ui.trainingTitleText
import org.jetbrains.compose.resources.stringResource
import schulte.shared.generated.resources.Res
import schulte.shared.generated.resources.action_exit_training
import schulte.shared.generated.resources.action_restart
import schulte.shared.generated.resources.count_times
import schulte.shared.generated.resources.training_assist_body
import schulte.shared.generated.resources.training_assist_title
import schulte.shared.generated.resources.training_current_target
import schulte.shared.generated.resources.training_current_target_value
import schulte.shared.generated.resources.training_error
import schulte.shared.generated.resources.training_timer

@Composable
internal fun TrainingScreen(
  state: SchulteState,
  onAction: (TrainingAction) -> Unit,
) {
  SchulteScaffold(
    title = state.selectedMarkMode.trainingTitleText(),
    onNavigationClick = { onAction(TrainingAction.ExitTraining) },
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        StatCard(
          stringResource(Res.string.training_current_target),
          stringResource(Res.string.training_current_target_value, state.currentTarget),
          Modifier.weight(1.2f),
        )
        StatCard(stringResource(Res.string.training_timer), formatTimer(state.elapsedMillis), Modifier.weight(1f))
        StatCard(
          stringResource(Res.string.training_error),
          stringResource(Res.string.count_times, state.errorCount),
          Modifier.weight(1f),
        )
      }
      if (state.selectedMarkMode == MarkMode.AssistedMarking) {
        InfoCard(
          title = stringResource(Res.string.training_assist_title),
          body = stringResource(Res.string.training_assist_body),
        )
      }
      SchulteGrid(state = state, onCellClick = { onAction(TrainingAction.CellClick(it)) })
      Spacer(Modifier.weight(1f))
      Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
          onClick = { onAction(TrainingAction.RestartTraining) },
          modifier = Modifier
            .weight(1f)
            .height(48.dp),
          shape = RoundedCornerShape(8.dp),
        ) {
          Text(stringResource(Res.string.action_restart))
        }
        OutlinedButton(
          onClick = { onAction(TrainingAction.ExitTraining) },
          modifier = Modifier
            .weight(1f)
            .height(48.dp),
          shape = RoundedCornerShape(8.dp),
        ) {
          Text(stringResource(Res.string.action_exit_training))
        }
      }
    }
  }
}

@Composable
private fun SchulteGrid(
  state: SchulteState,
  onCellClick: (Int) -> Unit,
) {
  val size = state.selectedGrid.size
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(1f),
    verticalArrangement = Arrangement.spacedBy(if (size >= 7) 6.dp else 8.dp),
  ) {
    state.numbers.chunked(size).forEach { row ->
      Row(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.spacedBy(if (size >= 7) 6.dp else 8.dp),
      ) {
        row.forEach { value ->
          val feedback = state.lastFeedback?.takeIf { it.value == value }
          val isCompleted = state.completedNumbers.contains(value)
          val scale by animateFloatAsState(
            targetValue = if (feedback?.isCorrect == true) 0.96f else 1f,
          )
          val background = when {
            feedback?.isCorrect == true -> Color(0xFFE6F4FF)
            feedback?.isCorrect == false -> Color(0xFFFFECEC)
            isCompleted -> Color(0xFFF1F5F9)
            else -> CardBackground
          }
          val contentColor = if (isCompleted) QuietText.copy(alpha = 0.48f) else Color(0xFF152238)

          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxSize()
              .scale(scale)
              .background(background, RoundedCornerShape(8.dp))
              .border(1.dp, if (feedback?.isCorrect == false) ErrorRed else LineColor, RoundedCornerShape(8.dp))
              .clickable { onCellClick(value) },
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = value.toString(),
              color = contentColor,
              fontSize = if (size >= 7) 18.sp else 24.sp,
              fontWeight = FontWeight.SemiBold,
              textAlign = TextAlign.Center,
            )
          }
        }
      }
    }
  }
}
