package org.easy.schulte.core.data

import org.easy.schulte.core.domain.TrainingTapResult
import org.easy.schulte.core.model.report.TrainingReport

internal interface TrainingRepository : FeatureStateRepository {
  fun startTraining(numbers: List<Int>)
  fun updateElapsedMillis(elapsedMillis: Long)
  fun applyCellTap(result: TrainingTapResult)
  fun clearFeedbackIfMatches(value: Int)
  fun finishTraining(report: TrainingReport)
  fun exitTraining()
}
