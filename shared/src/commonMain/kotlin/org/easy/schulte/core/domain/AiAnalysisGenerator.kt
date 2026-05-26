package org.easy.schulte.core.domain

import org.easy.schulte.core.model.AiAnalysis
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.ScoreLevel
import org.easy.schulte.core.model.TrainingReport
import org.easy.schulte.core.ui.formatSeconds

internal fun createLocalAiAnalysis(report: TrainingReport): AiAnalysis {
    val target = report.nextTargetSeconds ?: report.elapsedSeconds.toInt().coerceAtLeast(1)
    return AiAnalysis(
        summary = "你在 ${report.gridSpec.title} ${report.markMode.title} 下完成时间为 ${formatSeconds(report.elapsedMillis)}，错误 ${report.errorCount} 次，整体表现为${report.scoreLevel.title}。",
        speed = if (report.scoreLevel == ScoreLevel.Excellent) {
            "完成时间已达到当前年龄段优秀水平，可以尝试保持速度的同时降低错误次数。"
        } else {
            "当前速度仍有提升空间，建议先稳定扫描节奏，再逐步压缩完成时间。"
        },
        errors = if (report.errorCount == 0) {
            "本次没有错误点击，说明目标切换较稳定。下一步可以在保持准确率的前提下提速。"
        } else {
            "本次出现 ${report.errorCount} 次错误，可能来自目标切换时的注意力偏移。建议先锁定下一个目标，再点击。"
        },
        nextTimeGoal = "${target} 秒以内",
        nextErrorGoal = if (report.errorCount == 0) "保持 0 次" else "${(report.errorCount - 1).coerceAtLeast(0)} 次以内",
        recommendedSpec = "${report.gridSpec.title} ${MarkMode.BriefFeedbackOnly.title}",
        suggestions = listOf(
            "每天练习 3 组，每组间隔 30 秒",
            "优先保证准确率，再提升速度",
            "连续 3 次稳定后，再尝试更高规格",
        ),
    )
}
