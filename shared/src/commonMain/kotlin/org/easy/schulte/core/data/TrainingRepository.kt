package org.easy.schulte.core.data

import org.easy.schulte.core.model.TrainingReport

internal interface TrainingRepository : FeatureStateRepository {
  fun startTraining(numbers: List<Int>)
  fun updateElapsedMillis(elapsedMillis: Long)
  fun recordCorrectCell(value: Int, completedNumbers: Set<Int>, nextTarget: Int)
  fun recordIncorrectCell(value: Int)
  fun clearFeedbackIfMatches(value: Int)
  fun finishTraining(report: TrainingReport)
  fun exitTraining()
}
