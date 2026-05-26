package org.easy.schulte.feature.advice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.easy.schulte.core.domain.createLocalAiAnalysis
import org.easy.schulte.core.model.AiAnalysisState
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.model.Screen

internal class AdviceViewModel(
    private val state: () -> SchulteState,
    private val updateState: (((SchulteState) -> SchulteState) -> Unit),
    private val scope: CoroutineScope,
    private val restartTraining: () -> Unit,
) {
    fun onAction(action: AdviceAction) {
        when (action) {
            AdviceAction.RestartTraining -> restartTraining()
            AdviceAction.BackToReport -> updateState { it.copy(screen = Screen.Report) }
        }
    }

    fun generateAiAnalysis() {
        val report = state().report ?: return
        val settings = state().aiSettings
        if (!settings.isConfigured) {
            updateState { it.copy(aiAnalysisState = AiAnalysisState.NeedsSettings) }
            return
        }

        updateState { it.copy(aiAnalysisState = AiAnalysisState.Loading) }
        scope.launch {
            delay(700)
            updateState {
                it.copy(
                    screen = Screen.AiAdvice,
                    aiAnalysisState = AiAnalysisState.Success,
                    aiAnalysis = createLocalAiAnalysis(report),
                )
            }
        }
    }
}
