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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.ui.FocusBlue
import org.easy.schulte.core.ui.InfoCard
import org.easy.schulte.core.ui.KeyValueRow
import org.easy.schulte.core.ui.SchulteCard
import org.easy.schulte.core.ui.SchulteScaffold
import org.easy.schulte.core.ui.SectionTitle

@Composable
internal fun AiAdviceScreen(
  state: SchulteState,
  onAction: (AdviceAction) -> Unit,
) {
  val analysis = state.aiAnalysis ?: return
  SchulteScaffold(
    title = "AI 训练建议",
    navigationText = "报告",
    onNavigationClick = { onAction(AdviceAction.BackToReport) },
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      InfoCard("本次表现总结", analysis.summary)
      InfoCard("速度表现", analysis.speed)
      InfoCard("错误情况", analysis.errors)
      SchulteCard {
        SectionTitle("下一次目标")
        KeyValueRow("目标时间", analysis.nextTimeGoal)
        KeyValueRow("错误次数", analysis.nextErrorGoal)
        KeyValueRow("推荐规格", analysis.recommendedSpec)
      }
      SchulteCard {
        SectionTitle("增强训练建议")
        analysis.suggestions.forEach {
          Text("• $it", color = Color(0xFF223044), modifier = Modifier.padding(top = 8.dp))
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
          Text("再练一次")
        }
        OutlinedButton(
          onClick = { onAction(AdviceAction.BackToReport) },
          modifier = Modifier
            .weight(1f)
            .height(50.dp),
          shape = RoundedCornerShape(8.dp),
        ) {
          Text("返回报告")
        }
      }
    }
  }
}
