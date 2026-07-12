package org.easy.schulte.core.data

import org.easy.schulte.core.model.report.TrainingReport

internal interface TrainingRepository : FeatureStateRepository {

  suspend fun startTraining(numbers: List<Int>)
  suspend fun updateElapsedMillis(elapsedMillis: Long)
  suspend fun recordCorrectCell(value: Int, completedNumbers: Set<Int>, nextTarget: Int)
  suspend fun recordIncorrectCell(value: Int)
  suspend fun clearFeedbackIfMatches(value: Int)
  suspend fun finishTraining(report: TrainingReport)
  suspend fun exitTraining()
}
