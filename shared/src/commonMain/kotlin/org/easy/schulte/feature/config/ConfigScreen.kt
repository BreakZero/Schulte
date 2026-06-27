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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.easy.schulte.core.model.records.TrainingRecord
import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.LayoutMode
import org.easy.schulte.core.model.training.enums.MarkMode
import org.easy.schulte.core.ui.FocusBlue
import org.easy.schulte.core.ui.InfoCard
import org.easy.schulte.core.ui.ModeCard
import org.easy.schulte.core.ui.QuietText
import org.easy.schulte.core.ui.SchulteCard
import org.easy.schulte.core.ui.SchulteScaffold
import org.easy.schulte.core.ui.ScoreBadge
import org.easy.schulte.core.ui.SectionTitle
import org.easy.schulte.core.ui.SelectCard
import org.easy.schulte.core.ui.descriptionText
import org.easy.schulte.core.ui.difficultyText
import org.easy.schulte.core.ui.formatSecondsText
import org.easy.schulte.core.ui.titleText
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import schulte.shared.generated.resources.*

@Composable
internal fun ConfigRoot(
  onStartTraining: () -> Unit,
  onOpenRecords: () -> Unit,
  onOpenSettings: () -> Unit,
  viewModel: ConfigViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel) {
    viewModel.events.collect { event ->
      when (event) {
        ConfigEvent.StartTraining -> onStartTraining()
        ConfigEvent.OpenRecords -> onOpenRecords()
        ConfigEvent.OpenSettings -> onOpenSettings()
      }
    }
  }

  ConfigScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun ConfigScreen(
  state: ConfigState,
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
      RecentTrainingCard(
        latestRecord = state.latestRecord,
        isLoggedIn = state.isLoggedIn,
        onOpenRecords = { onAction(ConfigAction.OpenRecords) },
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
            title = mode.titleText(),
            description = mode.descriptionText(),
            onClick = { onAction(ConfigAction.SelectMarkMode(mode)) },
          )
        }
      }
      SectionTitle(stringResource(Res.string.section_layout_mode))
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        LayoutMode.entries.forEach { mode ->
          ModeCard(
            selected = state.selectedLayoutMode == mode,
            title = mode.titleText(),
            description = mode.descriptionText(),
            onClick = { onAction(ConfigAction.SelectLayoutMode(mode)) },
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
        Text(
          if (!state.hasRecords) "开始首次训练" else stringResource(Res.string.action_start_training),
          fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold,
        )
      }
    }
  }
}

@Composable
private fun RecentTrainingCard(
  latestRecord: TrainingRecord?,
  isLoggedIn: Boolean,
  onOpenRecords: () -> Unit,
) {
  SchulteCard {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      SectionTitle("最近训练")
      if (latestRecord?.isPersonalBest == true) {
        Text("个人最佳", color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
      }
    }
    if (latestRecord == null) {
      Text("还没有训练记录", color = Color(0xFF172033), fontWeight = FontWeight.SemiBold)
      Text("完成首次训练后，这里会展示你的训练记录和进步情况。", color = QuietText, lineHeight = 21.sp)
    } else {
      Text(if (isLoggedIn) "记录已归属当前账号" else "当前为本地游客记录", color = QuietText, fontSize = 13.sp)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            "${latestRecord.gridSpec.titleText()} ${latestRecord.markMode.titleText()} · ${latestRecord.layoutMode.titleText()}",
            fontWeight = FontWeight.SemiBold,
          )
          Text(
            "${formatSecondsText(latestRecord.elapsedTimeMillis)} · 错误 ${latestRecord.errorCount} 次",
            color = QuietText,
          )
          Text(latestRecord.shortProgressText(), color = QuietText, fontSize = 13.sp)
        }
        ScoreBadge(latestRecord.scoreLevel)
      }
    }
    OutlinedButton(
      onClick = onOpenRecords,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(8.dp),
    ) {
      Text("查看记录")
    }
  }
}

private fun TrainingRecord.shortProgressText(): String = when {
  previousRecordId == null -> "首次记录"
  isPersonalBest -> "刷新个人最佳"
  timeDeltaMillis != null && timeDeltaMillis < 0 -> "比上次快 ${deltaSecondsText(timeDeltaMillis)}"
  errorDelta != null && errorDelta < 0 -> "错误减少 ${-errorDelta} 次"
  else -> "表现稳定"
}

private fun deltaSecondsText(deltaMillis: Long): String {
  val value = kotlin.math.abs(deltaMillis)
  val seconds = value / 1000
  val centis = (value % 1000) / 10
  return "$seconds.${if (centis < 10) "0$centis" else centis} 秒"
}
