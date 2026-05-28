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
import org.easy.schulte.core.ui.SuccessGreen
import org.easy.schulte.core.ui.WarningAmber
import org.easy.schulte.core.ui.formatSeconds

@Composable
internal fun ReportScreen(
  state: SchulteState,
  onAction: (ReportAction) -> Unit,
) {
  val report = state.report ?: return
  SchulteScaffold(title = "本次训练报告") {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Text(
        text = "完成一次 ${report.gridSpec.title} ${report.markMode.title}",
        color = QuietText,
      )
      ResultHeroCard(report)
      ReportOverview(report)
      InfoCard(
        title = "评分说明",
        body = if (report.isOfficialScore) {
          "根据当前规格、年龄段与完成时间进行等级判断。错误次数暂作为辅助指标展示。"
        } else {
          "本次使用辅助标记，完成时间和错误次数仅作为练习参考。"
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
          Text("再来一次")
        }
        OutlinedButton(
          onClick = { onAction(ReportAction.BackToConfig) },
          modifier = Modifier
            .weight(1f)
            .height(50.dp),
          shape = RoundedCornerShape(8.dp),
        ) {
          Text("返回首页")
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
      text = if (configured) "AI 增强分析" else "AI 增强分析未启用",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
      color = Color(0xFF162033),
    )
    Spacer(Modifier.height(8.dp))
    Text(
      text = if (configured) {
        "让 AI 根据本次训练表现，生成专注力训练建议"
      } else {
        "配置 API Key 和 Base URL 后，可以生成个性化训练建议。不配置 AI 也可以正常使用基础训练和成绩报告。"
      },
      color = QuietText,
    )
    AnimatedVisibility(state.aiAnalysisState == AiAnalysisState.NeedsSettings) {
      Text("请先完成 AI 配置", color = WarningAmber, modifier = Modifier.padding(top = 8.dp))
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
          state.aiAnalysisState == AiAnalysisState.Loading -> "分析中..."
          configured -> "生成 AI 分析"
          else -> "前往设置"
        },
      )
    }
    Spacer(Modifier.height(8.dp))
    Text("AI 建议仅供训练参考，不作为医学或心理诊断", color = QuietText, fontSize = 12.sp)
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
        Text("完成时间", color = QuietText)
        Text(
          text = formatSeconds(report.elapsedMillis),
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
        ScoreLevel.Excellent -> "你的视觉搜索速度表现很好"
        ScoreLevel.Good -> "你的完成速度较好，保持稳定节奏"
        ScoreLevel.Pass -> "已达到基础完成标准"
        ScoreLevel.Below -> "建议先放慢节奏，优先保证准确"
        ScoreLevel.Practice -> "辅助模式成绩仅供练习参考"
      },
      color = QuietText,
    )
  }
}

@Composable
private fun ReportOverview(report: TrainingReport) {
  SchulteCard {
    SectionTitle("数据概览")
    KeyValueRow("方格规格", report.gridSpec.title)
    KeyValueRow("年龄段", report.ageGroup.title)
    KeyValueRow("训练模式", report.markMode.title)
    KeyValueRow("错误次数", "${report.errorCount} 次")
    KeyValueRow("完成时间", formatSeconds(report.elapsedMillis))
    KeyValueRow("是否正式成绩", if (report.isOfficialScore) "是" else "否")
  }
}
