package org.easy.schulte.feature.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
import org.easy.schulte.core.model.ImprovementStatus
import org.easy.schulte.core.model.RecordGridFilter
import org.easy.schulte.core.model.RecordModeFilter
import org.easy.schulte.core.model.RecordTimeFilter
import org.easy.schulte.core.model.TrainingRecord
import org.easy.schulte.core.platform.currentTimeMillis
import org.easy.schulte.core.ui.FocusBlue
import org.easy.schulte.core.ui.KeyValueRow
import org.easy.schulte.core.ui.QuietText
import org.easy.schulte.core.ui.SchulteCard
import org.easy.schulte.core.ui.SchulteScaffold
import org.easy.schulte.core.ui.ScoreBadge
import org.easy.schulte.core.ui.SectionTitle
import org.easy.schulte.core.ui.StatCard
import org.easy.schulte.core.ui.formatSecondsText
import org.easy.schulte.core.ui.titleText
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun TrainingRecordsRoot(
  onBack: () -> Unit,
  onStartTraining: () -> Unit,
  viewModel: TrainingRecordsViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel) {
    viewModel.events.collect { event ->
      when (event) {
        TrainingRecordsEvent.Back -> onBack()
        TrainingRecordsEvent.StartTraining -> onStartTraining()
      }
    }
  }

  TrainingRecordsScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun TrainingRecordsScreen(
  state: TrainingRecordsState,
  onAction: (TrainingRecordsAction) -> Unit,
) {
  SchulteScaffold(
    title = "训练记录",
    onNavigationClick = { onAction(TrainingRecordsAction.Back) },
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      if (state.records.isEmpty()) {
        EmptyRecords(onStartTraining = { onAction(TrainingRecordsAction.StartTraining) })
      } else {
        RecordsOwnershipCard(state)
        SummaryGrid(state)
        Filters(state, onAction)
        state.filteredRecords().forEach { record ->
          RecordRow(record)
        }
      }
    }
  }
}

@Composable
private fun RecordsOwnershipCard(state: TrainingRecordsState) {
  SchulteCard {
    Text(
      if (state.isLoggedIn) "当前记录归属到账号：${state.currentUserNickname}" else "当前为本地记录",
      fontWeight = FontWeight.SemiBold,
      color = Color(0xFF172033),
    )
    Text(
      if (state.isLoggedIn) "新完成的训练会归属到当前账号。" else "登录后可将本地训练记录归属到账号。",
      color = QuietText,
      lineHeight = 21.sp,
    )
  }
}

@Composable
private fun SummaryGrid(state: TrainingRecordsState) {
  val summary = state.recordSummary
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
      StatCard("总训练次数", "${summary.totalCount} 次", Modifier.weight(1f))
      StatCard("最近 7 天", "${summary.recent7DaysCount} 次", Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
      StatCard("当前最佳", summary.bestRecord?.let { formatSecondsText(it.elapsedTimeMillis) } ?: "暂无", Modifier.weight(1f))
      StatCard("最近训练", summary.latestRecord?.relativeTimeText().orEmpty(), Modifier.weight(1f))
    }
  }
}

@Composable
private fun Filters(
  state: TrainingRecordsState,
  onAction: (TrainingRecordsAction) -> Unit,
) {
  SchulteCard {
    SectionTitle("筛选")
    FilterGroup(
      items = RecordGridFilter.entries,
      selected = state.recordGridFilter,
      label = { it.label() },
      onClick = { onAction(TrainingRecordsAction.SelectGridFilter(it)) },
    )
    FilterGroup(
      items = RecordModeFilter.entries,
      selected = state.recordModeFilter,
      label = { it.label() },
      onClick = { onAction(TrainingRecordsAction.SelectModeFilter(it)) },
    )
    FilterGroup(
      items = RecordTimeFilter.entries,
      selected = state.recordTimeFilter,
      label = { it.label() },
      onClick = { onAction(TrainingRecordsAction.SelectTimeFilter(it)) },
    )
  }
}

@Composable
private fun <T> FilterGroup(
  items: List<T>,
  selected: T,
  label: @Composable (T) -> String,
  onClick: (T) -> Unit,
) {
  FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
    items.forEach { item ->
      FilterChip(
        selected = selected == item,
        onClick = { onClick(item) },
        label = { Text(label(item)) },
      )
    }
  }
}

@Composable
private fun RecordRow(record: TrainingRecord) {
  SchulteCard {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          "${record.gridSpec.titleText()} ${record.markMode.titleText()}",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF172033),
        )
        Text(
          "${formatSecondsText(record.elapsedTimeMillis)} · 错误 ${record.errorCount} 次",
          color = QuietText,
        )
      }
      ScoreBadge(record.scoreLevel)
    }
    KeyValueRow("进步情况", record.progressText())
    Text(record.relativeTimeText(), color = QuietText, fontSize = 13.sp)
  }
}

@Composable
private fun EmptyRecords(onStartTraining: () -> Unit) {
  SchulteCard {
    Text("还没有训练记录", fontWeight = FontWeight.SemiBold, color = Color(0xFF172033))
    Text("完成一次训练后，这里会展示你的完成时间、错误次数和进步情况。", color = QuietText, lineHeight = 21.sp)
    Button(
      onClick = onStartTraining,
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp),
      colors = ButtonDefaults.buttonColors(containerColor = FocusBlue),
      shape = RoundedCornerShape(8.dp),
    ) {
      Text("开始训练")
    }
  }
}

private fun TrainingRecordsState.filteredRecords(): List<TrainingRecord> {
  val now = currentTimeMillis()
  val minCreatedAt = recordTimeFilter.days?.let { now - it * 24L * 60 * 60 * 1000 }
  return records.filter { record ->
    (recordGridFilter.gridSpec == null || record.gridSpec == recordGridFilter.gridSpec) &&
      (recordModeFilter.markMode == null || record.markMode == recordModeFilter.markMode) &&
      (minCreatedAt == null || record.createdAt >= minCreatedAt)
  }
}

@Composable
private fun RecordGridFilter.label(): String = gridSpec?.titleText() ?: "全部"

@Composable
private fun RecordModeFilter.label(): String = markMode?.titleText() ?: "全部"

private fun RecordTimeFilter.label(): String = when (this) {
  RecordTimeFilter.All -> "全部"
  RecordTimeFilter.Last7Days -> "最近 7 天"
  RecordTimeFilter.Last30Days -> "最近 30 天"
}

private fun TrainingRecord.progressText(): String = when (improvementStatus) {
  ImprovementStatus.FirstRecord -> "首次记录"
  ImprovementStatus.PersonalBest -> "刷新个人最佳"
  ImprovementStatus.ImprovedSpeed -> "比上次快 ${deltaSecondsText(timeDeltaMillis ?: 0L)}"
  ImprovementStatus.ImprovedAccuracy -> "错误减少 ${kotlin.math.abs(errorDelta ?: 0)} 次"
  ImprovementStatus.Stable -> "表现稳定"
  ImprovementStatus.Mixed -> "速度更快，准确性略有波动"
  ImprovementStatus.Declined -> "本次略有波动"
}

private fun TrainingRecord.relativeTimeText(): String {
  val days = ((currentTimeMillis() - createdAt) / (24L * 60 * 60 * 1000)).coerceAtLeast(0)
  return when (days) {
    0L -> "今天"
    1L -> "昨天"
    else -> "$days 天前"
  }
}

private fun deltaSecondsText(deltaMillis: Long): String {
  val value = kotlin.math.abs(deltaMillis)
  val seconds = value / 1000
  val centis = (value % 1000) / 10
  return "$seconds.${if (centis < 10) "0$centis" else centis} 秒"
}
