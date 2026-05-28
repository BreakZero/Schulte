package org.easy.schulte.feature.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.easy.schulte.core.model.AgeGroup
import org.easy.schulte.core.model.GridSpec
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.ui.FocusBlue
import org.easy.schulte.core.ui.InfoCard
import org.easy.schulte.core.ui.ModeCard
import org.easy.schulte.core.ui.QuietText
import org.easy.schulte.core.ui.SchulteScaffold
import org.easy.schulte.core.ui.SectionTitle
import org.easy.schulte.core.ui.SelectCard
import org.easy.schulte.core.ui.difficultyText
import org.easy.schulte.core.ui.titleText
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import schulte.shared.generated.resources.Res
import schulte.shared.generated.resources.action_start_training
import schulte.shared.generated.resources.app_title
import schulte.shared.generated.resources.config_daily_tip_body
import schulte.shared.generated.resources.config_daily_tip_title
import schulte.shared.generated.resources.config_headline
import schulte.shared.generated.resources.config_subtitle
import schulte.shared.generated.resources.ic_settings_24
import schulte.shared.generated.resources.section_age_group
import schulte.shared.generated.resources.section_grid_spec
import schulte.shared.generated.resources.section_training_mode

@Composable
internal fun ConfigScreen(
  state: SchulteState,
  onAction: (ConfigAction) -> Unit,
) {
  SchulteScaffold(
    title = stringResource(Res.string.app_title),
    actions = {
      IconButton(onClick = { onAction(ConfigAction.OpenSettings) }) {
        Icon(
          painter = painterResource(Res.drawable.ic_settings_24),
          contentDescription = null,
        )
      }
    },
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(vertical = 20.dp, horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      Text(
        text = stringResource(Res.string.config_headline),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF162033),
      )
      Text(
        text = stringResource(Res.string.config_subtitle),
        color = QuietText,
        style = MaterialTheme.typography.bodyMedium,
      )
      InfoCard(
        title = stringResource(Res.string.config_daily_tip_title),
        body = stringResource(Res.string.config_daily_tip_body),
      )
      SectionTitle(stringResource(Res.string.section_grid_spec))
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        GridSpec.entries.forEach { spec ->
          SelectCard(
            modifier = Modifier.weight(1f),
            selected = state.selectedGrid == spec,
            title = spec.titleText(),
            subtitle = spec.difficultyText(),
            onClick = { onAction(ConfigAction.SelectGrid(spec)) },
          )
        }
      }
      SectionTitle(stringResource(Res.string.section_age_group))
      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AgeGroup.entries.forEach { ageGroup ->
          FilterChip(
            selected = state.selectedAgeGroup == ageGroup,
            onClick = { onAction(ConfigAction.SelectAgeGroup(ageGroup)) },
            label = { Text(ageGroup.titleText()) },
          )
        }
      }
      SectionTitle(stringResource(Res.string.section_training_mode))
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        MarkMode.entries.forEach { mode ->
          ModeCard(
            selected = state.selectedMarkMode == mode,
            mode = mode,
            onClick = { onAction(ConfigAction.SelectMarkMode(mode)) },
          )
        }
      }
      Spacer(Modifier.height(6.dp))
      Button(
        onClick = { onAction(ConfigAction.StartTraining) },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FocusBlue),
        shape = RoundedCornerShape(8.dp),
      ) {
        Text(stringResource(Res.string.action_start_training), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}
