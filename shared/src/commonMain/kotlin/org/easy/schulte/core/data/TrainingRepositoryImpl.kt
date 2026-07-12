package org.easy.schulte.core.data

import kotlinx.coroutines.flow.update
import org.easy.schulte.core.model.ai.enums.AiAnalysisState
import org.easy.schulte.core.model.records.ProgressComparison
import org.easy.schulte.core.model.records.TrainingRecord
import org.easy.schulte.core.model.records.enums.ImprovementStatus
import org.easy.schulte.core.model.report.TrainingReport
import org.easy.schulte.core.model.training.CellFeedback
import org.easy.schulte.core.model.training.enums.MarkMode
import org.easy.schulte.core.platform.currentTimeMillis
import kotlin.math.abs
import kotlin.math.max

internal class TrainingRepositoryImpl(
  private val sharedState: SchulteSharedState,
) : TrainingRepository,
  FeatureStateRepository by sharedState {
  override fun startTraining(numbers: List<Int>) {
    sharedState.mutableTrainingState.update {
      it.copy(
        numbers = numbers,
        currentTarget = 1,
        completedNumbers = emptySet(),
        elapsedMillis = 0L,
        errorCount = 0,
        lastFeedback = null,
      )
    }
    sharedState.mutableReportState.update {
      it.copy(
        report = null,
        aiAnalysis = null,
        aiAnalysisState = AiAnalysisState.Idle,
        progressComparison = null,
      )
    }
    sharedState.mutableAccountState.update { it.copy(accountMessage = null) }
  }

  override fun updateElapsedMillis(elapsedMillis: Long) {
    sharedState.mutableTrainingState.update { it.copy(elapsedMillis = elapsedMillis) }
  }

  override fun recordCorrectCell(value: Int, completedNumbers: Set<Int>, nextTarget: Int) {
    sharedState.mutableTrainingState.update {
      it.copy(
        currentTarget = nextTarget,
        completedNumbers = completedNumbers,
        lastFeedback = CellFeedback(value, isCorrect = true),
      )
    }
  }

  override fun recordIncorrectCell(value: Int) {
    sharedState.mutableTrainingState.update {
      it.copy(errorCount = it.errorCount + 1, lastFeedback = CellFeedback(value, isCorrect = false))
    }
  }

  override fun clearFeedbackIfMatches(value: Int) {
    sharedState.mutableTrainingState.update {
      if (it.lastFeedback?.value == value) it.copy(lastFeedback = null) else it
    }
  }

  override suspend fun finishTraining(report: TrainingReport) {
    val comparison = createTrainingRecord(report)
    sharedState.recordStore.insertRecord(comparison.currentRecord)
    sharedState.mutableTrainingState.update { it.copy(elapsedMillis = report.elapsedMillis, lastFeedback = null) }
    sharedState.mutableReportState.update {
      it.copy(
        report = report,
        progressComparison = comparison,
        aiAnalysis = null,
        aiAnalysisState = AiAnalysisState.Idle,
      )
    }
  }

  override fun exitTraining() {
    sharedState.mutableTrainingState.update { it.copy(lastFeedback = null) }
  }

  private suspend fun createTrainingRecord(report: TrainingReport): ProgressComparison {
    val createdAt = currentTimeMillis()
    val sameConditionRecords = sharedState.recordStore.getRecordsForConditions(
      gridSize = report.gridSpec.size,
      ageGroupName = report.ageGroup.name,
      markModeName = report.markMode.name,
    )
    val previousRecord = sameConditionRecords.firstOrNull()
    val previousBest = sameConditionRecords.minByOrNull { it.elapsedTimeMillis }
    val timeDelta = previousRecord?.let { report.elapsedMillis - it.elapsedTimeMillis }
    val errorDelta = previousRecord?.let { report.errorCount - it.errorCount }
    val isPersonalBest = previousBest == null || report.elapsedMillis < previousBest.elapsedTimeMillis
    val baseStatus = improvementStatus(
      currentElapsedMillis = report.elapsedMillis,
      currentErrorCount = report.errorCount,
      previousRecord = previousRecord,
      isPersonalBest = isPersonalBest,
    )
    val record = TrainingRecord(
      id = "${createdAt}_${report.gridSpec.size}_${report.elapsedMillis}",
      ownerUserId = sharedState.currentAccountState().currentUserId,
      createdAt = createdAt,
      gridSpec = report.gridSpec,
      ageGroup = report.ageGroup,
      markMode = report.markMode,
      elapsedTimeMillis = report.elapsedMillis,
      errorCount = report.errorCount,
      scoreLevel = report.scoreLevel,
      isPersonalBest = isPersonalBest,
      previousRecordId = previousRecord?.id,
      improvementStatus = baseStatus,
      timeDeltaMillis = timeDelta,
      errorDelta = errorDelta,
    )
    return ProgressComparison(
      currentRecord = record,
      previousRecord = previousRecord,
      bestRecord = if (isPersonalBest) record else previousBest,
      recentRecords = (listOf(record) + sameConditionRecords).take(5),
      improvementStatus = baseStatus,
      summaryText = record.summaryText(),
      nextGoalText = record.nextGoalText(),
    )
  }
}

private fun improvementStatus(
  currentElapsedMillis: Long,
  currentErrorCount: Int,
  previousRecord: TrainingRecord?,
  isPersonalBest: Boolean,
): ImprovementStatus {
  if (previousRecord == null) return ImprovementStatus.FirstRecord
  if (isPersonalBest) return ImprovementStatus.PersonalBest
  val minEffectiveDeltaMillis = max(300L, (previousRecord.elapsedTimeMillis * 0.01).toLong())
  val timeDelta = currentElapsedMillis - previousRecord.elapsedTimeMillis
  val errorDelta = currentErrorCount - previousRecord.errorCount
  val faster = timeDelta <= -minEffectiveDeltaMillis
  val slower = timeDelta >= minEffectiveDeltaMillis
  return when {
    faster && errorDelta <= 0 -> ImprovementStatus.ImprovedSpeed
    abs(timeDelta) < minEffectiveDeltaMillis && errorDelta < 0 -> ImprovementStatus.ImprovedAccuracy
    faster && errorDelta > 0 -> ImprovementStatus.Mixed
    !slower && errorDelta == 0 -> ImprovementStatus.Stable
    else -> ImprovementStatus.Declined
  }
}

private fun TrainingRecord.summaryText(): String = when (improvementStatus) {
  ImprovementStatus.FirstRecord -> "这是你的首次 ${gridSpec.size}x${gridSpec.size} ${markMode.shortName()}训练，后续会展示进步趋势。"
  ImprovementStatus.PersonalBest -> "刷新了当前条件下的个人最佳成绩。"
  ImprovementStatus.ImprovedSpeed -> "本次比上次快了 ${timeDeltaMillis.fastDeltaText()}，视觉搜索速度有所提升。"
  ImprovementStatus.ImprovedAccuracy -> "本次速度基本稳定，错误减少 ${abs(errorDelta ?: 0)} 次，点击准确性有所提升。"
  ImprovementStatus.Stable -> "本次表现稳定，可以继续保持当前节奏。"
  ImprovementStatus.Mixed -> "本次完成时间更快，但错误次数增加了 ${errorDelta ?: 0} 次。"
  ImprovementStatus.Declined -> "本次表现略有波动，建议下一轮先保证准确率。"
}

private fun TrainingRecord.nextGoalText(): String = when (improvementStatus) {
  ImprovementStatus.FirstRecord -> "再完成 1 次同规格训练，建立对比基准。"
  ImprovementStatus.PersonalBest -> "尝试稳定在当前最佳成绩附近。"
  ImprovementStatus.ImprovedSpeed -> "保持当前节奏，错误次数不增加。"
  ImprovementStatus.ImprovedAccuracy -> "保持低错误的前提下略微提速。"
  ImprovementStatus.Stable -> "尝试比当前时间快 0.5 秒。"
  ImprovementStatus.Mixed, ImprovementStatus.Declined -> "优先降低错误次数，不急于提速。"
}

private fun Long?.fastDeltaText(): String {
  val value = abs(this ?: 0L)
  val seconds = value / 1000
  val centis = (value % 1000) / 10
  return "$seconds.${if (centis < 10) "0$centis" else centis} 秒"
}

private fun MarkMode.shortName(): String = when (this) {
  MarkMode.BriefFeedbackOnly -> "标准"
  MarkMode.AssistedMarking -> "辅助"
}
