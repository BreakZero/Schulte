package org.easy.schulte.feature.report

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.easy.schulte.core.model.AiAnalysisState
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.model.ScoreLevel
import org.easy.schulte.core.model.TrainingReport
import org.easy.schulte.core.ui.ErrorRed
import org.easy.schulte.core.ui.FocusBlue
import org.easy.schulte.core.ui.FocusTeal
import org.easy.schulte.core.ui.InfoCard
import org.easy.schulte.core.ui.KeyValueRow
import org.easy.schulte.core.ui.QuietText
import org.easy.schulte.core.ui.SchulteCard
import org.easy.schulte.core.ui.SchulteScaffold
import org.easy.schulte.core.ui.ScoreBadge
import org.easy.schulte.core.ui.SectionTitle
import org.easy.schulte.core.ui.WarningAmber
import org.easy.schulte.core.ui.formatSecondsText
import org.easy.schulte.core.ui.titleText
import org.jetbrains.compose.resources.stringResource
import schulte.shared.generated.resources.Res
import schulte.shared.generated.resources.action_back_home
import schulte.shared.generated.resources.action_generate_ai_analysis
import schulte.shared.generated.resources.action_open_settings
import schulte.shared.generated.resources.action_try_again
import schulte.shared.generated.resources.ai_analyzing
import schulte.shared.generated.resources.ai_disclaimer
import schulte.shared.generated.resources.ai_entry_disabled_body
import schulte.shared.generated.resources.ai_entry_disabled_title
import schulte.shared.generated.resources.ai_entry_enabled_body
import schulte.shared.generated.resources.ai_entry_enabled_title
import schulte.shared.generated.resources.ai_needs_settings
import schulte.shared.generated.resources.count_times
import schulte.shared.generated.resources.no
import schulte.shared.generated.resources.report_age_group
import schulte.shared.generated.resources.report_completion_summary
import schulte.shared.generated.resources.report_elapsed_time
import schulte.shared.generated.resources.report_error_count
import schulte.shared.generated.resources.report_grid_spec
import schulte.shared.generated.resources.report_is_official_score
import schulte.shared.generated.resources.report_score_note_official
import schulte.shared.generated.resources.report_score_note_practice
import schulte.shared.generated.resources.report_score_note_title
import schulte.shared.generated.resources.report_title
import schulte.shared.generated.resources.report_training_mode
import schulte.shared.generated.resources.score_below_message
import schulte.shared.generated.resources.score_excellent_message
import schulte.shared.generated.resources.score_good_message
import schulte.shared.generated.resources.score_pass_message
import schulte.shared.generated.resources.score_practice_message
import schulte.shared.generated.resources.section_report_overview
import schulte.shared.generated.resources.yes

@Composable
internal fun ReportScreen(
  state: SchulteState,
  onAction: (ReportAction) -> Unit,
) {
  val report = state.report ?: return
  SchulteScaffold(title = stringResource(Res.string.report_title)) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Text(
        text = stringResource(
          Res.string.report_completion_summary,
          report.gridSpec.titleText(),
          report.markMode.titleText(),
        ),
        color = QuietText,
      )
      ResultHeroCard(report)
      ReportOverview(report)
      InfoCard(
        title = stringResource(Res.string.report_score_note_title),
        body = if (report.isOfficialScore) {
          stringResource(Res.string.report_score_note_official)
        } else {
          stringResource(Res.string.report_score_note_practice)
        },
      )
      AiEntryCard(state = state, onAction = onAction)
      Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Button(
          onClick = { onAction(ReportAction.RestartTraining) },
          modifier = Modifier
            .weight(1f)
            .height(50.dp),
          colors = ButtonDefaults.buttonColors(containerColor = FocusBlue),
          shape = RoundedCornerShape(8.dp),
        ) {
          Text(stringResource(Res.string.action_try_again))
        }
        OutlinedButton(
          onClick = { onAction(ReportAction.BackToConfig) },
          modifier = Modifier
            .weight(1f)
            .height(50.dp),
          shape = RoundedCornerShape(8.dp),
        ) {
          Text(stringResource(Res.string.action_back_home))
        }
      }
    }
  }
}

@Composable
private fun AiEntryCard(
  state: SchulteState,
  onAction: (ReportAction) -> Unit,
) {
  val configured = state.aiSettings.isConfigured
  SchulteCard {
    Text(
      text = if (configured) {
        stringResource(Res.string.ai_entry_enabled_title)
      } else {
        stringResource(Res.string.ai_entry_disabled_title)
      },
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
      color = Color(0xFF162033),
    )
    Spacer(Modifier.height(8.dp))
    Text(
      text = if (configured) {
        stringResource(Res.string.ai_entry_enabled_body)
      } else {
        stringResource(Res.string.ai_entry_disabled_body)
      },
      color = QuietText,
    )
    AnimatedVisibility(state.aiAnalysisState == AiAnalysisState.NeedsSettings) {
      Text(stringResource(Res.string.ai_needs_settings), color = WarningAmber, modifier = Modifier.padding(top = 8.dp))
    }
    Spacer(Modifier.height(12.dp))
    Button(
      onClick = {
        if (configured) onAction(ReportAction.GenerateAiAnalysis) else onAction(ReportAction.OpenSettings)
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
      enabled = state.aiAnalysisState != AiAnalysisState.Loading,
      colors = ButtonDefaults.buttonColors(containerColor = if (configured) FocusTeal else FocusBlue),
      shape = RoundedCornerShape(8.dp),
    ) {
      Text(
        when {
          state.aiAnalysisState == AiAnalysisState.Loading -> stringResource(Res.string.ai_analyzing)
          configured -> stringResource(Res.string.action_generate_ai_analysis)
          else -> stringResource(Res.string.action_open_settings)
        },
      )
    }
    Spacer(Modifier.height(8.dp))
    Text(stringResource(Res.string.ai_disclaimer), color = QuietText, fontSize = 12.sp)
  }
}

@Composable
private fun ResultHeroCard(report: TrainingReport) {
  SchulteCard {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Top,
    ) {
      Column {
        Text(stringResource(Res.string.report_elapsed_time), color = QuietText)
        Text(
          text = formatSecondsText(report.elapsedMillis),
          fontSize = 40.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF14213D),
        )
      }
      ScoreBadge(report.scoreLevel)
    }
    Spacer(Modifier.height(8.dp))
    Text(
      text = when (report.scoreLevel) {
        ScoreLevel.Excellent -> stringResource(Res.string.score_excellent_message)
        ScoreLevel.Good -> stringResource(Res.string.score_good_message)
        ScoreLevel.Pass -> stringResource(Res.string.score_pass_message)
        ScoreLevel.Below -> stringResource(Res.string.score_below_message)
        ScoreLevel.Practice -> stringResource(Res.string.score_practice_message)
      },
      color = QuietText,
    )
  }
}

@Composable
private fun ReportOverview(report: TrainingReport) {
  SchulteCard {
    SectionTitle(stringResource(Res.string.section_report_overview))
    KeyValueRow(stringResource(Res.string.report_grid_spec), report.gridSpec.titleText())
    KeyValueRow(stringResource(Res.string.report_age_group), report.ageGroup.titleText())
    KeyValueRow(stringResource(Res.string.report_training_mode), report.markMode.titleText())
    KeyValueRow(stringResource(Res.string.report_error_count), stringResource(Res.string.count_times, report.errorCount))
    KeyValueRow(stringResource(Res.string.report_elapsed_time), formatSecondsText(report.elapsedMillis))
    KeyValueRow(
      stringResource(Res.string.report_is_official_score),
      if (report.isOfficialScore) stringResource(Res.string.yes) else stringResource(Res.string.no),
    )
  }
}
