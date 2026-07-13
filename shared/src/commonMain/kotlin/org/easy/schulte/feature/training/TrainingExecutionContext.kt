package org.easy.schulte.feature.training

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.random.Random
import kotlin.time.TimeSource

/** KMP-safe execution dependencies for deterministic training behavior. */
internal class TrainingExecutionContext(
  val dispatcher: CoroutineDispatcher = Dispatchers.Main,
  val clock: MonotonicClock = TimeSourceMonotonicClock(),
  val random: TrainingRandom = DefaultTrainingRandom(),
)

internal fun interface MonotonicClock {
  fun nowMillis(): Long
}

internal class TimeSourceMonotonicClock : MonotonicClock {
  private val origin = TimeSource.Monotonic.markNow()

  override fun nowMillis(): Long = origin.elapsedNow().inWholeMilliseconds
}

internal fun interface TrainingRandom {
  fun shuffle(values: List<Int>): List<Int>
}

internal class DefaultTrainingRandom(
  private val random: Random = Random.Default,
) : TrainingRandom {
  override fun shuffle(values: List<Int>): List<Int> = values.shuffled(random)
}
