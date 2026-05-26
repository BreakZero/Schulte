package org.easy.schulte.feature.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.ui.FocusBlue
import org.easy.schulte.core.ui.InfoCard
import org.easy.schulte.core.ui.KeyValueRow
import org.easy.schulte.core.ui.QuietText
import org.easy.schulte.core.ui.SchulteCard
import org.easy.schulte.core.ui.SchulteScaffold
import org.easy.schulte.core.ui.SectionTitle
import org.easy.schulte.core.ui.SuccessGreen
import org.easy.schulte.core.ui.SwitchRow

@Composable
internal fun SettingsScreen(
    state: SchulteState,
    onAction: (SettingsAction) -> Unit,
) {
    SchulteScaffold(
        title = "设置",
        navigationText = "返回",
        onNavigationClick = { onAction(SettingsAction.BackFromSettings) },
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
                    onCheckedChange = { onAction(SettingsAction.ToggleAssistSetting(it)) },
                )
            }
            SchulteCard {
                SectionTitle("AI 设置")
                SwitchRow(
                    title = "启用 AI 分析",
                    subtitle = "只影响训练后的增强报告",
                    checked = state.aiSettings.aiEnabled,
                    onCheckedChange = { onAction(SettingsAction.ToggleAiEnabled(it)) },
                )
                OutlinedTextField(
                    value = state.aiSettings.apiKey,
                    onValueChange = { onAction(SettingsAction.UpdateApiKey(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("API Key") },
                    placeholder = { Text("请输入你的 API Key") },
                    visualTransformation = if (state.apiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        TextButton(onClick = { onAction(SettingsAction.ToggleApiKeyVisibility) }) {
                            Text(if (state.apiKeyVisible) "隐藏" else "显示")
                        }
                    },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = state.aiSettings.baseUrl,
                    onValueChange = { onAction(SettingsAction.UpdateBaseUrl(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Base URL") },
                    placeholder = { Text("例如：https://api.openai.com/v1") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = state.aiSettings.modelName,
                    onValueChange = { onAction(SettingsAction.UpdateModelName(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("模型名称") },
                    placeholder = { Text("例如：gpt-4o-mini") },
                    singleLine = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    FilledTonalButton(
                        onClick = { onAction(SettingsAction.TestAiConnection) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text("测试连接")
                    }
                    OutlinedButton(
                        onClick = { onAction(SettingsAction.ClearAiSettings) },
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
                onClick = { onAction(SettingsAction.SaveSettings) },
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
