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

@Composable
internal fun ConfigScreen(
  state: SchulteState,
  onAction: (ConfigAction) -> Unit,
) {
  SchulteScaffold(
    title = "舒尔特方格训练",
    actionText = "设置",
    onActionClick = { onAction(ConfigAction.OpenSettings) },
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(vertical = 20.dp, horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      Text(
        text = "开始一次专注力训练",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF162033),
      )
      Text(
        text = "按顺序点击数字，训练视觉搜索与注意力集中",
        color = QuietText,
        style = MaterialTheme.typography.bodyMedium,
      )
      InfoCard(
        title = "建议每日练习 3~5 分钟",
        body = "保持自然呼吸，尽量不要移动视线中心",
      )
      SectionTitle("方格规格")
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        GridSpec.entries.forEach { spec ->
          SelectCard(
            modifier = Modifier.weight(1f),
            selected = state.selectedGrid == spec,
            title = spec.title,
            subtitle = spec.difficulty,
            onClick = { onAction(ConfigAction.SelectGrid(spec)) },
          )
        }
      }
      SectionTitle("年龄段")
      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AgeGroup.entries.forEach { ageGroup ->
          FilterChip(
            selected = state.selectedAgeGroup == ageGroup,
            onClick = { onAction(ConfigAction.SelectAgeGroup(ageGroup)) },
            label = { Text(ageGroup.title) },
          )
        }
      }
      SectionTitle("训练模式")
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
        Text("开始训练", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}
