package org.easy.schulte.feature.training

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.easy.schulte.core.data.FeatureStateRepository
import org.easy.schulte.core.data.TrainingRepository
import org.easy.schulte.core.domain.TrainingReportCalculator
import org.easy.schulte.core.model.configuration.AppConfiguration
import org.easy.schulte.core.model.configuration.FeatureConfiguration
import org.easy.schulte.core.model.configuration.enums.ConfigurationFeature
import org.easy.schulte.core.model.records.TrainingRecordSummary
import org.easy.schulte.core.model.report.TrainingReport
import org.easy.schulte.core.model.runtime.AccountRuntimeState
import org.easy.schulte.core.model.runtime.ReportRuntimeState
import org.easy.schulte.core.model.runtime.SettingsRuntimeState
import org.easy.schulte.core.model.runtime.TrainingRecordsRuntimeState
import org.easy.schulte.core.model.runtime.TrainingRuntimeState
import org.easy.schulte.core.model.training.CellFeedback
import org.easy.schulte.core.model.training.enums.GridSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TrainingViewModelTest {
  @Test
  fun startTrainingUsesInjectedRandomAndTicksWithVirtualMonotonicTime() = runTest {
    val repository = FakeTrainingRepository(configuration = AppConfiguration(selectedGrid = GridSpec.Three))
    val dispatcher = StandardTestDispatcher(testScheduler)
    val viewModel = TrainingViewModel(
      repository = repository,
      reportCalculator = TrainingReportCalculator(),
      executionContext = TrainingExecutionContext(
        dispatcher = dispatcher,
        clock = MonotonicClock { testScheduler.currentTime },
        random = TrainingRandom { values -> values.reversed() },
      ),
    )

    viewModel.startTraining()
    runCurrent()
    advanceTimeBy(66)
    runCurrent()

    assertEquals(listOf(9, 8, 7, 6, 5, 4, 3, 2, 1), repository.currentTrainingState().numbers)
    assertEquals(66, repository.currentTrainingState().elapsedMillis)
    viewModel.clearForTest()
  }

  @Test
  fun feedbackIsClearedAfterVirtualDelay() = runTest {
    val repository = FakeTrainingRepository(configuration = AppConfiguration(selectedGrid = GridSpec.Three))
    val viewModel = trainingViewModel(repository, StandardTestDispatcher(testScheduler))

    viewModel.startTraining()
    runCurrent()
    val target = repository.currentTrainingState().currentTarget
    viewModel.onAction(TrainingAction.CellClick(target))
    runCurrent()
    assertEquals(CellFeedback(target, isCorrect = true), repository.currentTrainingState().lastFeedback)

    advanceTimeBy(179)
    runCurrent()
    assertEquals(CellFeedback(target, isCorrect = true), repository.currentTrainingState().lastFeedback)

    advanceTimeBy(1)
    runCurrent()
    assertEquals(null, repository.currentTrainingState().lastFeedback)
    viewModel.clearForTest()
  }

  @Test
  fun completingTrainingPublishesBufferedNavigationEffectAndRestartResetsSession() = runTest {
    val repository = FakeTrainingRepository(configuration = AppConfiguration(selectedGrid = GridSpec.Three))
    val viewModel = trainingViewModel(repository, StandardTestDispatcher(testScheduler))

    viewModel.startTraining()
    runCurrent()
    repeat(9) {
      viewModel.onAction(TrainingAction.CellClick(repository.currentTrainingState().currentTarget))
      runCurrent()
    }

    val event = viewModel.events.first()
    assertIs<TrainingEvent.Completed>(event)
    assertEquals(1, repository.finishedReports.size)

    viewModel.onAction(TrainingAction.RestartTraining)
    runCurrent()
    assertEquals(1, repository.currentTrainingState().currentTarget)
    assertEquals(0, repository.currentTrainingState().errorCount)
    assertEquals(9, repository.currentTrainingState().numbers.size)
    viewModel.clearForTest()
  }

  @Test
  fun clearingViewModelCancelsTimerAndFeedbackJobs() = runTest {
    val repository = FakeTrainingRepository(configuration = AppConfiguration(selectedGrid = GridSpec.Three))
    val viewModel = trainingViewModel(repository, StandardTestDispatcher(testScheduler))

    viewModel.startTraining()
    runCurrent()
    viewModel.onAction(TrainingAction.CellClick(repository.currentTrainingState().currentTarget))
    runCurrent()
    viewModel.clearForTest()
    val elapsedAtClear = repository.currentTrainingState().elapsedMillis

    advanceTimeBy(1_000)
    runCurrent()

    assertEquals(elapsedAtClear, repository.currentTrainingState().elapsedMillis)
    assertTrue(repository.currentTrainingState().lastFeedback != null)
    assertFalse(repository.exitCalled)
  }

  private fun trainingViewModel(
    repository: FakeTrainingRepository,
    dispatcher: TestDispatcher,
  ) = TrainingViewModel(
    repository = repository,
    reportCalculator = TrainingReportCalculator(),
    executionContext = TrainingExecutionContext(
      dispatcher = dispatcher,
      clock = MonotonicClock { dispatcher.scheduler.currentTime },
      random = TrainingRandom { values -> values },
    ),
  )
}

private class FakeTrainingRepository(
  private val configuration: AppConfiguration = AppConfiguration(),
) : TrainingRepository {
  private val mutableTrainingState = MutableStateFlow(TrainingRuntimeState())
  private val mutableReportState = MutableStateFlow(ReportRuntimeState())
  private val mutableSettingsState = MutableStateFlow(SettingsRuntimeState())
  private val mutableRecordsState = MutableStateFlow(TrainingRecordsRuntimeState(recordSummary = TrainingRecordSummary()))
  private val mutableAccountState = MutableStateFlow(AccountRuntimeState())

  var exitCalled = false
  val finishedReports = mutableListOf<TrainingReport>()

  override val trainingState: StateFlow<TrainingRuntimeState> = mutableTrainingState.asStateFlow()
  override val reportState: StateFlow<ReportRuntimeState> = mutableReportState.asStateFlow()
  override val settingsState: StateFlow<SettingsRuntimeState> = mutableSettingsState.asStateFlow()
  override val recordsState: Flow<TrainingRecordsRuntimeState> = mutableRecordsState
  override val accountState: Flow<AccountRuntimeState> = mutableAccountState

  override fun currentTrainingState(): TrainingRuntimeState = mutableTrainingState.value

  override fun currentReportState(): ReportRuntimeState = mutableReportState.value

  override fun currentSettingsState(): SettingsRuntimeState = mutableSettingsState.value

  override suspend fun currentRecordsState(): TrainingRecordsRuntimeState = mutableRecordsState.value

  override suspend fun currentAccountState(): AccountRuntimeState = mutableAccountState.value

  override suspend fun currentConfiguration(): AppConfiguration = configuration

  override suspend fun currentFeatureConfiguration(feature: ConfigurationFeature): FeatureConfiguration = FeatureConfiguration(feature)

  override fun observeFeatureConfiguration(feature: ConfigurationFeature): Flow<FeatureConfiguration> = MutableStateFlow(FeatureConfiguration(feature))

  override suspend fun startTraining(numbers: List<Int>) {
    mutableTrainingState.value = TrainingRuntimeState(numbers = numbers)
  }

  override suspend fun updateElapsedMillis(elapsedMillis: Long) {
    mutableTrainingState.value = mutableTrainingState.value.copy(elapsedMillis = elapsedMillis)
  }

  override suspend fun recordCorrectCell(value: Int, completedNumbers: Set<Int>, nextTarget: Int) {
    mutableTrainingState.value = mutableTrainingState.value.copy(
      completedNumbers = completedNumbers,
      currentTarget = nextTarget,
      lastFeedback = CellFeedback(value, isCorrect = true),
    )
  }

  override suspend fun recordIncorrectCell(value: Int) {
    mutableTrainingState.value = mutableTrainingState.value.copy(
      errorCount = mutableTrainingState.value.errorCount + 1,
      lastFeedback = CellFeedback(value, isCorrect = false),
    )
  }

  override suspend fun clearFeedbackIfMatches(value: Int) {
    if (mutableTrainingState.value.lastFeedback?.value == value) {
      mutableTrainingState.value = mutableTrainingState.value.copy(lastFeedback = null)
    }
  }

  override suspend fun finishTraining(report: TrainingReport) {
    finishedReports += report
  }

  override suspend fun exitTraining() {
    exitCalled = true
  }
}
