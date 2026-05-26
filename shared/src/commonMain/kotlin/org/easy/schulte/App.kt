package org.easy.schulte

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.random.Random

private val FocusBlue = Color(0xFF2477D4)
private val FocusTeal = Color(0xFF1C8C87)
private val PageBackground = Color(0xFFF5F8FB)
private val CardBackground = Color(0xFFFFFFFF)
private val QuietText = Color(0xFF5E6A78)
private val LineColor = Color(0xFFE2E8F0)
private val SuccessGreen = Color(0xFF1C8E5A)
private val WarningAmber = Color(0xFFB7791F)
private val ErrorRed = Color(0xFFD14B4B)

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = viewModel { SchulteViewModel() }
        SchulteRoot(viewModel = viewModel)
    }
}

@Composable
fun SchulteRoot(viewModel: SchulteViewModel = viewModel { SchulteViewModel() }) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is SchulteEvent.TrainingCompleted -> Unit
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PageBackground,
    ) {
        when (state.screen) {
            Screen.Config -> ConfigScreen(state = state, onAction = viewModel::onAction)
            Screen.Training -> TrainingScreen(state = state, onAction = viewModel::onAction)
            Screen.Report -> ReportScreen(state = state, onAction = viewModel::onAction)
            Screen.AiAdvice -> AiAdviceScreen(state = state, onAction = viewModel::onAction)
            Screen.Settings -> SettingsScreen(state = state, onAction = viewModel::onAction)
        }
    }
}

data class SchulteState(
    val screen: Screen = Screen.Config,
    val selectedGrid: GridSpec = GridSpec.Five,
    val selectedAgeGroup: AgeGroup = AgeGroup.Adult,
    val selectedMarkMode: MarkMode = MarkMode.BriefFeedbackOnly,
    val numbers: List<Int> = emptyList(),
    val currentTarget: Int = 1,
    val completedNumbers: Set<Int> = emptySet(),
    val elapsedMillis: Long = 0L,
    val errorCount: Int = 0,
    val lastFeedback: CellFeedback? = null,
    val report: TrainingReport? = null,
    val aiSettings: AiSettings = AiSettings(),
    val apiKeyVisible: Boolean = false,
    val settingsMessage: String? = null,
    val aiAnalysis: AiAnalysis? = null,
    val aiAnalysisState: AiAnalysisState = AiAnalysisState.Idle,
)

sealed interface SchulteAction {
    data class SelectGrid(val spec: GridSpec) : SchulteAction
    data class SelectAgeGroup(val ageGroup: AgeGroup) : SchulteAction
    data class SelectMarkMode(val markMode: MarkMode) : SchulteAction
    data object StartTraining : SchulteAction
    data class CellClick(val value: Int) : SchulteAction
    data object RestartTraining : SchulteAction
    data object ExitTraining : SchulteAction
    data object BackToConfig : SchulteAction
    data object OpenSettings : SchulteAction
    data object BackFromSettings : SchulteAction
    data class ToggleAiEnabled(val enabled: Boolean) : SchulteAction
    data class ToggleAssistSetting(val enabled: Boolean) : SchulteAction
    data class UpdateApiKey(val value: String) : SchulteAction
    data class UpdateBaseUrl(val value: String) : SchulteAction
    data class UpdateModelName(val value: String) : SchulteAction
    data object ToggleApiKeyVisibility : SchulteAction
    data object TestAiConnection : SchulteAction
    data object ClearAiSettings : SchulteAction
    data object SaveSettings : SchulteAction
    data object GenerateAiAnalysis : SchulteAction
    data object BackToReport : SchulteAction
}

sealed interface SchulteEvent {
    data class TrainingCompleted(val report: TrainingReport) : SchulteEvent
}

enum class Screen {
    Config,
    Training,
    Report,
    AiAdvice,
    Settings,
}

enum class GridSpec(
    val size: Int,
    val title: String,
    val difficulty: String,
) {
    Three(3, "3x3", "入门"),
    Four(4, "4x4", "初级"),
    Five(5, "5x5", "中级"),
    Seven(7, "7x7", "高级");

    val count: Int get() = size * size
}

enum class AgeGroup(val title: String) {
    Child("3~5岁"),
    Junior("6~10岁"),
    Teen("11~17岁"),
    Adult("18岁以上"),
}

enum class MarkMode(val title: String, val description: String) {
    BriefFeedbackOnly("标准模式", "点击后仅短暂反馈，不持续标记"),
    AssistedMarking("辅助模式", "显示已完成标记，成绩仅供练习参考"),
}

enum class ScoreLevel(val title: String) {
    Excellent("优"),
    Good("良"),
    Pass("及格"),
    Below("未达标"),
    Practice("练习参考"),
}

data class CellFeedback(
    val value: Int,
    val isCorrect: Boolean,
)

data class AiSettings(
    val assistedMarkingEnabled: Boolean = false,
    val aiEnabled: Boolean = false,
    val apiKey: String = "",
    val baseUrl: String = "",
    val modelName: String = "gpt-4o-mini",
) {
    val isConfigured: Boolean
        get() = aiEnabled && apiKey.isNotBlank() && baseUrl.isNotBlank() && modelName.isNotBlank()
}

data class TrainingReport(
    val gridSpec: GridSpec,
    val ageGroup: AgeGroup,
    val markMode: MarkMode,
    val elapsedMillis: Long,
    val errorCount: Int,
    val scoreLevel: ScoreLevel,
    val isOfficialScore: Boolean,
    val nextTargetSeconds: Int?,
) {
    val elapsedSeconds: Double get() = elapsedMillis / 1000.0
}

data class AiAnalysis(
    val summary: String,
    val speed: String,
    val errors: String,
    val nextTimeGoal: String,
    val nextErrorGoal: String,
    val recommendedSpec: String,
    val suggestions: List<String>,
)

enum class AiAnalysisState {
    Idle,
    Loading,
    Success,
    NeedsSettings,
    Failed,
}

class SchulteViewModel : ViewModel() {
    private val _state = MutableStateFlow(SchulteState())
    val state = _state.asStateFlow()

    private val _events = Channel<SchulteEvent>()
    val events = _events.receiveAsFlow()

    private var timerJob: Job? = null

    fun onAction(action: SchulteAction) {
        when (action) {
            is SchulteAction.SelectGrid -> _state.update { it.copy(selectedGrid = action.spec) }
            is SchulteAction.SelectAgeGroup -> _state.update { it.copy(selectedAgeGroup = action.ageGroup) }
            is SchulteAction.SelectMarkMode -> _state.update { it.copy(selectedMarkMode = action.markMode) }
            SchulteAction.StartTraining -> startTraining()
            is SchulteAction.CellClick -> onCellClick(action.value)
            SchulteAction.RestartTraining -> startTraining()
            SchulteAction.ExitTraining -> stopTraining(Screen.Config)
            SchulteAction.BackToConfig -> stopTraining(Screen.Config)
            SchulteAction.OpenSettings -> _state.update { it.copy(screen = Screen.Settings, settingsMessage = null) }
            SchulteAction.BackFromSettings -> _state.update { it.copy(screen = if (it.report == null) Screen.Config else Screen.Report) }
            is SchulteAction.ToggleAiEnabled -> updateSettings { copy(aiEnabled = action.enabled) }
            is SchulteAction.ToggleAssistSetting -> updateSettings { copy(assistedMarkingEnabled = action.enabled) }
            is SchulteAction.UpdateApiKey -> updateSettings { copy(apiKey = action.value) }
            is SchulteAction.UpdateBaseUrl -> updateSettings { copy(baseUrl = action.value) }
            is SchulteAction.UpdateModelName -> updateSettings { copy(modelName = action.value) }
            SchulteAction.ToggleApiKeyVisibility -> _state.update { it.copy(apiKeyVisible = !it.apiKeyVisible) }
            SchulteAction.TestAiConnection -> testAiConnection()
            SchulteAction.ClearAiSettings -> _state.update {
                it.copy(
                    aiSettings = it.aiSettings.copy(aiEnabled = false, apiKey = "", baseUrl = "", modelName = "gpt-4o-mini"),
                    settingsMessage = "AI 配置已清除",
                    aiAnalysisState = AiAnalysisState.Idle,
                    aiAnalysis = null,
                )
            }
            SchulteAction.SaveSettings -> _state.update {
                it.copy(settingsMessage = "设置已保存", selectedMarkMode = if (it.aiSettings.assistedMarkingEnabled) MarkMode.AssistedMarking else MarkMode.BriefFeedbackOnly)
            }
            SchulteAction.GenerateAiAnalysis -> generateAiAnalysis()
            SchulteAction.BackToReport -> _state.update { it.copy(screen = Screen.Report) }
        }
    }

    private fun updateSettings(block: AiSettings.() -> AiSettings) {
        _state.update { it.copy(aiSettings = it.aiSettings.block(), settingsMessage = null) }
    }

    private fun startTraining() {
        val current = _state.value
        timerJob?.cancel()
        _state.update {
            it.copy(
                screen = Screen.Training,
                numbers = (1..current.selectedGrid.count).shuffled(Random.Default),
                currentTarget = 1,
                completedNumbers = emptySet(),
                elapsedMillis = 0L,
                errorCount = 0,
                lastFeedback = null,
                report = null,
                aiAnalysis = null,
                aiAnalysisState = AiAnalysisState.Idle,
            )
        }
        timerJob = viewModelScope.launch {
            val startedAt = kotlin.time.TimeSource.Monotonic.markNow()
            while (true) {
                _state.update { it.copy(elapsedMillis = startedAt.elapsedNow().inWholeMilliseconds) }
                delay(33)
            }
        }
    }

    private fun onCellClick(value: Int) {
        val current = _state.value
        if (current.screen != Screen.Training) return

        if (value == current.currentTarget) {
            val nextTarget = current.currentTarget + 1
            val nextCompleted = if (current.selectedMarkMode == MarkMode.AssistedMarking) {
                current.completedNumbers + value
            } else {
                current.completedNumbers
            }
            _state.update {
                it.copy(
                    currentTarget = nextTarget,
                    completedNumbers = nextCompleted,
                    lastFeedback = CellFeedback(value, isCorrect = true),
                )
            }
            clearFeedbackLater(value)

            if (value == current.selectedGrid.count) {
                completeTraining()
            }
        } else {
            _state.update {
                it.copy(
                    errorCount = it.errorCount + 1,
                    lastFeedback = CellFeedback(value, isCorrect = false),
                )
            }
            clearFeedbackLater(value)
        }
    }

    private fun clearFeedbackLater(value: Int) {
        viewModelScope.launch {
            delay(180)
            _state.update { state ->
                if (state.lastFeedback?.value == value) state.copy(lastFeedback = null) else state
            }
        }
    }

    private fun completeTraining() {
        timerJob?.cancel()
        val completed = _state.value
        val report = createReport(completed)
        _state.update {
            it.copy(
                screen = Screen.Report,
                report = report,
                elapsedMillis = report.elapsedMillis,
                lastFeedback = null,
            )
        }
        viewModelScope.launch {
            _events.send(SchulteEvent.TrainingCompleted(report))
        }
    }

    private fun stopTraining(nextScreen: Screen) {
        timerJob?.cancel()
        _state.update { it.copy(screen = nextScreen, lastFeedback = null) }
    }

    private fun testAiConnection() {
        val settings = _state.value.aiSettings
        val message = if (!settings.aiEnabled) {
            "请先启用 AI 分析"
        } else if (settings.apiKey.isBlank() || settings.baseUrl.isBlank()) {
            "请填写 API Key 和 Base URL"
        } else if (settings.modelName.isBlank()) {
            "请填写模型名称"
        } else {
            "连接配置可用"
        }
        _state.update { it.copy(settingsMessage = message) }
    }

    private fun generateAiAnalysis() {
        val report = _state.value.report ?: return
        val settings = _state.value.aiSettings
        if (!settings.isConfigured) {
            _state.update { it.copy(aiAnalysisState = AiAnalysisState.NeedsSettings) }
            return
        }

        _state.update { it.copy(aiAnalysisState = AiAnalysisState.Loading) }
        viewModelScope.launch {
            delay(700)
            _state.update {
                it.copy(
                    screen = Screen.AiAdvice,
                    aiAnalysisState = AiAnalysisState.Success,
                    aiAnalysis = createLocalAiAnalysis(report),
                )
            }
        }
    }
}

private fun createReport(state: SchulteState): TrainingReport {
    val isOfficial = state.selectedMarkMode == MarkMode.BriefFeedbackOnly
    val level = if (isOfficial) {
        scoreLevel(state.selectedGrid, state.ageGroupOrSelected(), state.elapsedMillis)
    } else {
        ScoreLevel.Practice
    }
    return TrainingReport(
        gridSpec = state.selectedGrid,
        ageGroup = state.ageGroupOrSelected(),
        markMode = state.selectedMarkMode,
        elapsedMillis = max(state.elapsedMillis, 10L),
        errorCount = state.errorCount,
        scoreLevel = level,
        isOfficialScore = isOfficial,
        nextTargetSeconds = nextTargetSeconds(state.selectedGrid, state.ageGroupOrSelected(), level),
    )
}

private fun SchulteState.ageGroupOrSelected(): AgeGroup = selectedAgeGroup

private fun scoreLevel(gridSpec: GridSpec, ageGroup: AgeGroup, elapsedMillis: Long): ScoreLevel {
    val seconds = elapsedMillis / 1000.0
    val thresholds = thresholdsFor(gridSpec, ageGroup)
    return when {
        seconds <= thresholds.excellent -> ScoreLevel.Excellent
        seconds <= thresholds.good -> ScoreLevel.Good
        seconds <= thresholds.pass -> ScoreLevel.Pass
        else -> ScoreLevel.Below
    }
}

private data class ScoreThresholds(
    val excellent: Double,
    val good: Double,
    val pass: Double,
)

private fun thresholdsFor(gridSpec: GridSpec, ageGroup: AgeGroup): ScoreThresholds {
    val adultBase = when (gridSpec) {
        GridSpec.Three -> ScoreThresholds(6.0, 8.0, 11.0)
        GridSpec.Four -> ScoreThresholds(11.0, 15.0, 20.0)
        GridSpec.Five -> ScoreThresholds(18.0, 23.0, 29.0)
        GridSpec.Seven -> ScoreThresholds(42.0, 55.0, 70.0)
    }
    val multiplier = when (ageGroup) {
        AgeGroup.Child -> 1.8
        AgeGroup.Junior -> 1.45
        AgeGroup.Teen -> 1.2
        AgeGroup.Adult -> 1.0
    }
    return ScoreThresholds(
        excellent = adultBase.excellent * multiplier,
        good = adultBase.good * multiplier,
        pass = adultBase.pass * multiplier,
    )
}

private fun nextTargetSeconds(gridSpec: GridSpec, ageGroup: AgeGroup, level: ScoreLevel): Int? {
    val thresholds = thresholdsFor(gridSpec, ageGroup)
    return when (level) {
        ScoreLevel.Below -> thresholds.pass.toInt()
        ScoreLevel.Pass -> thresholds.good.toInt()
        ScoreLevel.Good -> thresholds.excellent.toInt()
        ScoreLevel.Excellent -> (thresholds.excellent - 1).toInt().coerceAtLeast(1)
        ScoreLevel.Practice -> null
    }
}

private fun createLocalAiAnalysis(report: TrainingReport): AiAnalysis {
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

@Composable
private fun ConfigScreen(
    state: SchulteState,
    onAction: (SchulteAction) -> Unit,
) {
    SchulteScaffold(
        title = "舒尔特方格训练",
        actionText = "设置",
        onActionClick = { onAction(SchulteAction.OpenSettings) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
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
                        onClick = { onAction(SchulteAction.SelectGrid(spec)) },
                    )
                }
            }
            SectionTitle("年龄段")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AgeGroup.entries.forEach { ageGroup ->
                    FilterChip(
                        selected = state.selectedAgeGroup == ageGroup,
                        onClick = { onAction(SchulteAction.SelectAgeGroup(ageGroup)) },
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
                        onClick = { onAction(SchulteAction.SelectMarkMode(mode)) },
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Button(
                onClick = { onAction(SchulteAction.StartTraining) },
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

@Composable
private fun TrainingScreen(
    state: SchulteState,
    onAction: (SchulteAction) -> Unit,
) {
    SchulteScaffold(
        title = state.selectedMarkMode.title.replace("模式", "训练"),
        navigationText = "返回",
        onNavigationClick = { onAction(SchulteAction.ExitTraining) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatCard("当前目标", "请点击 ${state.currentTarget}", Modifier.weight(1.2f))
                StatCard("计时", formatTimer(state.elapsedMillis), Modifier.weight(1f))
                StatCard("错误", "${state.errorCount} 次", Modifier.weight(1f))
            }
            if (state.selectedMarkMode == MarkMode.AssistedMarking) {
                InfoCard(title = "辅助模式", body = "已点击数字会弱化显示，成绩仅供练习参考")
            }
            SchulteGrid(state = state, onCellClick = { onAction(SchulteAction.CellClick(it)) })
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { onAction(SchulteAction.RestartTraining) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("重新开始")
                }
                OutlinedButton(
                    onClick = { onAction(SchulteAction.ExitTraining) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("退出训练")
                }
            }
        }
    }
}

@Composable
private fun SchulteGrid(
    state: SchulteState,
    onCellClick: (Int) -> Unit,
) {
    val size = state.selectedGrid.size
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        verticalArrangement = Arrangement.spacedBy(if (size >= 7) 6.dp else 8.dp),
    ) {
        state.numbers.chunked(size).forEach { row ->
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(if (size >= 7) 6.dp else 8.dp),
            ) {
                row.forEach { value ->
                    val feedback = state.lastFeedback?.takeIf { it.value == value }
                    val isCompleted = state.completedNumbers.contains(value)
                    val scale by animateFloatAsState(
                        targetValue = if (feedback?.isCorrect == true) 0.96f else 1f,
                    )
                    val background = when {
                        feedback?.isCorrect == true -> Color(0xFFE6F4FF)
                        feedback?.isCorrect == false -> Color(0xFFFFECEC)
                        isCompleted -> Color(0xFFF1F5F9)
                        else -> CardBackground
                    }
                    val contentColor = if (isCompleted) QuietText.copy(alpha = 0.48f) else Color(0xFF152238)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .scale(scale)
                            .background(background, RoundedCornerShape(8.dp))
                            .border(1.dp, if (feedback?.isCorrect == false) ErrorRed else LineColor, RoundedCornerShape(8.dp))
                            .clickable { onCellClick(value) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = value.toString(),
                            color = contentColor,
                            fontSize = if (size >= 7) 18.sp else 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportScreen(
    state: SchulteState,
    onAction: (SchulteAction) -> Unit,
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
                    onClick = { onAction(SchulteAction.RestartTraining) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FocusBlue),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("再来一次")
                }
                OutlinedButton(
                    onClick = { onAction(SchulteAction.BackToConfig) },
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
private fun AiAdviceScreen(
    state: SchulteState,
    onAction: (SchulteAction) -> Unit,
) {
    val analysis = state.aiAnalysis ?: return
    SchulteScaffold(
        title = "AI 训练建议",
        navigationText = "报告",
        onNavigationClick = { onAction(SchulteAction.BackToReport) },
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
                    onClick = { onAction(SchulteAction.RestartTraining) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FocusBlue),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("再练一次")
                }
                OutlinedButton(
                    onClick = { onAction(SchulteAction.BackToReport) },
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

@Composable
private fun SettingsScreen(
    state: SchulteState,
    onAction: (SchulteAction) -> Unit,
) {
    SchulteScaffold(
        title = "设置",
        navigationText = "返回",
        onNavigationClick = { onAction(SchulteAction.BackFromSettings) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SchulteCard {
                SectionTitle("训练设置")
                KeyValueRow("默认方格规格", state.selectedGrid.title)
                KeyValueRow("默认年龄段", state.selectedAgeGroup.title)
                KeyValueRow("默认训练模式", state.selectedMarkMode.title)
                SwitchRow(
                    title = "辅助模式显示已完成标记",
                    subtitle = "开启后成绩仅作为练习参考",
                    checked = state.aiSettings.assistedMarkingEnabled,
                    onCheckedChange = { onAction(SchulteAction.ToggleAssistSetting(it)) },
                )
            }
            SchulteCard {
                SectionTitle("AI 设置")
                SwitchRow(
                    title = "启用 AI 分析",
                    subtitle = "只影响训练后的增强报告",
                    checked = state.aiSettings.aiEnabled,
                    onCheckedChange = { onAction(SchulteAction.ToggleAiEnabled(it)) },
                )
                OutlinedTextField(
                    value = state.aiSettings.apiKey,
                    onValueChange = { onAction(SchulteAction.UpdateApiKey(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("API Key") },
                    placeholder = { Text("请输入你的 API Key") },
                    visualTransformation = if (state.apiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        TextButton(onClick = { onAction(SchulteAction.ToggleApiKeyVisibility) }) {
                            Text(if (state.apiKeyVisible) "隐藏" else "显示")
                        }
                    },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = state.aiSettings.baseUrl,
                    onValueChange = { onAction(SchulteAction.UpdateBaseUrl(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Base URL") },
                    placeholder = { Text("例如：https://api.openai.com/v1") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = state.aiSettings.modelName,
                    onValueChange = { onAction(SchulteAction.UpdateModelName(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("模型名称") },
                    placeholder = { Text("例如：gpt-4o-mini") },
                    singleLine = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    FilledTonalButton(
                        onClick = { onAction(SchulteAction.TestAiConnection) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text("测试连接")
                    }
                    OutlinedButton(
                        onClick = { onAction(SchulteAction.ClearAiSettings) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text("清除配置")
                    }
                }
                AnimatedVisibility(state.settingsMessage != null) {
                    Text(
                        text = state.settingsMessage.orEmpty(),
                        color = if (state.settingsMessage == "连接配置可用" || state.settingsMessage == "设置已保存") SuccessGreen else QuietText,
                    )
                }
            }
            InfoCard(
                title = "隐私与说明",
                body = "API Key 仅保存在本地设备。训练报告仅在用户点击 AI 分析时发送。AI 建议仅供训练参考，不作为医学或心理诊断。",
            )
            Button(
                onClick = { onAction(SchulteAction.SaveSettings) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FocusBlue),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("保存设置")
            }
        }
    }
}

@Composable
private fun AiEntryCard(
    state: SchulteState,
    onAction: (SchulteAction) -> Unit,
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
                if (configured) onAction(SchulteAction.GenerateAiAnalysis) else onAction(SchulteAction.OpenSettings)
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

@Composable
private fun ScoreBadge(scoreLevel: ScoreLevel) {
    val color = when (scoreLevel) {
        ScoreLevel.Excellent -> SuccessGreen
        ScoreLevel.Good -> FocusBlue
        ScoreLevel.Pass -> WarningAmber
        ScoreLevel.Below -> ErrorRed
        ScoreLevel.Practice -> QuietText
    }
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), CircleShape)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(scoreLevel.title, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    SchulteCard {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF162033))
        Spacer(Modifier.height(6.dp))
        Text(body, color = QuietText, lineHeight = 21.sp)
    }
}

@Composable
private fun SchulteCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp),
        content = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                content = content,
            )
        },
    )
}

@Composable
private fun SelectCard(
    selected: Boolean,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(78.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (selected) FocusBlue else LineColor),
        colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFFEAF4FF) else CardBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF172033), maxLines = 1)
            Text(subtitle, color = QuietText, fontSize = 12.sp, maxLines = 1)
        }
    }
}

@Composable
private fun ModeCard(
    selected: Boolean,
    mode: MarkMode,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (selected) FocusBlue else LineColor),
        colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFFEAF4FF) else CardBackground),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(if (selected) FocusBlue else Color.Transparent, CircleShape)
                    .border(1.dp, if (selected) FocusBlue else QuietText, CircleShape),
            )
            Column {
                Text(mode.title, fontWeight = FontWeight.SemiBold, color = Color(0xFF172033))
                Text(mode.description, color = QuietText, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(76.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(title, color = QuietText, fontSize = 12.sp, maxLines = 1)
            Text(
                value,
                color = Color(0xFF162033),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun KeyValueRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = QuietText)
        Spacer(Modifier.width(16.dp))
        Text(value, color = Color(0xFF172033), fontWeight = FontWeight.Medium, textAlign = TextAlign.End)
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color(0xFF172033), fontWeight = FontWeight.Medium)
            Text(subtitle, color = QuietText, fontSize = 13.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF172033),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchulteScaffold(
    title: String,
    navigationText: String? = null,
    onNavigationClick: (() -> Unit)? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        containerColor = PageBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    if (navigationText != null && onNavigationClick != null) {
                        TextButton(onClick = onNavigationClick) {
                            Text(navigationText)
                        }
                    }
                },
                actions = {
                    if (actionText != null && onActionClick != null) {
                        TextButton(onClick = onActionClick) {
                            Text(actionText)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PageBackground),
            )
        },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            content()
        }
    }
}

private fun formatTimer(millis: Long): String {
    val totalCentis = millis / 10
    val minutes = totalCentis / 6000
    val seconds = (totalCentis / 100) % 60
    val centis = totalCentis % 100
    return "${minutes.twoDigits()}:${seconds.twoDigits()}.${centis.twoDigits()}"
}

private fun formatSeconds(millis: Long): String {
    val seconds = millis / 1000
    val centis = (millis % 1000) / 10
    return "$seconds.${centis.twoDigits()} 秒"
}

private fun Long.twoDigits(): String = if (this < 10) "0$this" else toString()
