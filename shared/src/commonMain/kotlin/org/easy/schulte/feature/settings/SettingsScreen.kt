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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.easy.schulte.core.model.SchulteState
import org.easy.schulte.core.model.SettingsMessage
import org.easy.schulte.core.ui.FocusBlue
import org.easy.schulte.core.ui.InfoCard
import org.easy.schulte.core.ui.KeyValueRow
import org.easy.schulte.core.ui.QuietText
import org.easy.schulte.core.ui.SchulteCard
import org.easy.schulte.core.ui.SchulteScaffold
import org.easy.schulte.core.ui.SectionTitle
import org.easy.schulte.core.ui.SuccessGreen
import org.easy.schulte.core.ui.SwitchRow
import org.easy.schulte.core.ui.text
import org.easy.schulte.core.ui.titleText
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import schulte.shared.generated.resources.*

@Composable
internal fun SettingsRoot(
  onCloseSettings: () -> Unit,
  viewModel: SettingsViewModel = koinViewModel(),
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(viewModel) {
    viewModel.events.collect { event ->
      when (event) {
        SettingsEvent.CloseSettings -> onCloseSettings()
      }
    }
  }

  SettingsScreen(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun SettingsScreen(
  state: SchulteState,
  onAction: (SettingsAction) -> Unit,
) {
  SchulteScaffold(
    title = stringResource(Res.string.settings_title),
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
        SectionTitle(stringResource(Res.string.section_training_settings))
        KeyValueRow(stringResource(Res.string.settings_default_grid), state.selectedGrid.titleText())
        KeyValueRow(stringResource(Res.string.settings_default_age_group), state.selectedAgeGroup.titleText())
        KeyValueRow(stringResource(Res.string.settings_default_mark_mode), state.selectedMarkMode.titleText())
        SwitchRow(
          title = stringResource(Res.string.settings_assist_title),
          subtitle = stringResource(Res.string.settings_assist_subtitle),
          checked = state.aiSettings.assistedMarkingEnabled,
          onCheckedChange = { onAction(SettingsAction.ToggleAssistSetting(it)) },
        )
      }
      SchulteCard {
        SectionTitle(stringResource(Res.string.section_ai_settings))
        SwitchRow(
          title = stringResource(Res.string.settings_ai_enabled_title),
          subtitle = stringResource(Res.string.settings_ai_enabled_subtitle),
          checked = state.aiSettings.aiEnabled,
          onCheckedChange = { onAction(SettingsAction.ToggleAiEnabled(it)) },
        )
        if (state.aiSettings.aiEnabled) {
          OutlinedTextField(
            value = state.aiSettings.apiKey,
            onValueChange = { onAction(SettingsAction.UpdateApiKey(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.settings_api_key)) },
            placeholder = { Text(stringResource(Res.string.settings_api_key_placeholder)) },
            visualTransformation = if (state.apiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
              TextButton(onClick = { onAction(SettingsAction.ToggleApiKeyVisibility) }) {
                Text(
                  if (state.apiKeyVisible) {
                    stringResource(Res.string.action_hide)
                  } else {
                    stringResource(
                      Res.string.action_show,
                    )
                  },
                )
              }
            },
            singleLine = true,
          )
          OutlinedTextField(
            value = state.aiSettings.baseUrl,
            onValueChange = { onAction(SettingsAction.UpdateBaseUrl(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.settings_base_url)) },
            placeholder = { Text(stringResource(Res.string.settings_base_url_placeholder)) },
            singleLine = true,
          )
          OutlinedTextField(
            value = state.aiSettings.modelName,
            onValueChange = { onAction(SettingsAction.UpdateModelName(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.settings_model_name)) },
            placeholder = { Text(stringResource(Res.string.settings_model_name_placeholder)) },
            singleLine = true,
          )
          Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            FilledTonalButton(
              onClick = { onAction(SettingsAction.TestAiConnection) },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp),
            ) {
              Text(stringResource(Res.string.action_test_connection))
            }
            OutlinedButton(
              onClick = { onAction(SettingsAction.ClearAiSettings) },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp),
            ) {
              Text(stringResource(Res.string.action_clear_config))
            }
          }
        }
        AnimatedVisibility(state.settingsMessage != null) {
          Text(
            text = state.settingsMessage?.text().orEmpty(),
            color = if (state.settingsMessage?.isSuccess == true) SuccessGreen else QuietText,
          )
        }
      }
      InfoCard(
        title = stringResource(Res.string.settings_privacy_title),
        body = stringResource(Res.string.settings_privacy_body),
      )
      Button(
        onClick = { onAction(SettingsAction.SaveSettings) },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FocusBlue),
        shape = RoundedCornerShape(8.dp),
      ) {
        Text(stringResource(Res.string.action_save_settings))
      }
    }
  }
}

private val SettingsMessage.isSuccess: Boolean
  get() = this == SettingsMessage.ConnectionAvailable || this == SettingsMessage.Saved
