package org.easy.schulte.core.ui

import androidx.compose.runtime.Composable
import org.easy.schulte.core.model.report.enums.ScoreLevel
import org.easy.schulte.core.model.settings.enums.SettingsMessage
import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.LayoutMode
import org.easy.schulte.core.model.training.enums.MarkMode
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import schulte.shared.generated.resources.*

@Composable
internal fun GridSpec.titleText(): String = stringResource(titleResource)

@Composable
internal fun GridSpec.difficultyText(): String = stringResource(difficultyResource)

@Composable
internal fun AgeGroup.titleText(): String = stringResource(titleResource)

@Composable
internal fun MarkMode.titleText(): String = stringResource(titleResource)

@Composable
internal fun MarkMode.trainingTitleText(): String = stringResource(trainingTitleResource)

@Composable
internal fun MarkMode.descriptionText(): String = stringResource(descriptionResource)

@Composable
internal fun LayoutMode.titleText(): String = stringResource(titleResource)

@Composable
internal fun LayoutMode.descriptionText(): String = stringResource(descriptionResource)

@Composable
internal fun ScoreLevel.titleText(): String = stringResource(titleResource)

@Composable
internal fun SettingsMessage.text(): String = stringResource(textResource)

@Composable
internal fun formatSecondsText(millis: Long): String {
  val seconds = millis / 1000
  val centis = (millis % 1000) / 10
  return stringResource(Res.string.seconds_format, seconds, centis.twoDigits())
}

private val GridSpec.titleResource: StringResource
  get() = when (this) {
    GridSpec.Three -> Res.string.grid_spec_three_title
    GridSpec.Four -> Res.string.grid_spec_four_title
    GridSpec.Five -> Res.string.grid_spec_five_title
    GridSpec.Seven -> Res.string.grid_spec_seven_title
  }

private val GridSpec.difficultyResource: StringResource
  get() = when (this) {
    GridSpec.Three -> Res.string.grid_spec_three_difficulty
    GridSpec.Four -> Res.string.grid_spec_four_difficulty
    GridSpec.Five -> Res.string.grid_spec_five_difficulty
    GridSpec.Seven -> Res.string.grid_spec_seven_difficulty
  }

private val AgeGroup.titleResource: StringResource
  get() = when (this) {
    AgeGroup.Child -> Res.string.age_group_child
    AgeGroup.Junior -> Res.string.age_group_junior
    AgeGroup.Teen -> Res.string.age_group_teen
    AgeGroup.Adult -> Res.string.age_group_adult
  }

private val MarkMode.titleResource: StringResource
  get() = when (this) {
    MarkMode.BriefFeedbackOnly -> Res.string.mark_mode_standard_title
    MarkMode.AssistedMarking -> Res.string.mark_mode_assisted_title
  }

private val MarkMode.trainingTitleResource: StringResource
  get() = when (this) {
    MarkMode.BriefFeedbackOnly -> Res.string.mark_mode_standard_training_title
    MarkMode.AssistedMarking -> Res.string.mark_mode_assisted_training_title
  }

private val MarkMode.descriptionResource: StringResource
  get() = when (this) {
    MarkMode.BriefFeedbackOnly -> Res.string.mark_mode_standard_description
    MarkMode.AssistedMarking -> Res.string.mark_mode_assisted_description
  }

private val LayoutMode.titleResource: StringResource
  get() = when (this) {
    LayoutMode.Static -> Res.string.layout_mode_static_title
    LayoutMode.ShuffleAfterCorrectTap -> Res.string.layout_mode_dynamic_title
  }

private val LayoutMode.descriptionResource: StringResource
  get() = when (this) {
    LayoutMode.Static -> Res.string.layout_mode_static_description
    LayoutMode.ShuffleAfterCorrectTap -> Res.string.layout_mode_dynamic_description
  }

private val ScoreLevel.titleResource: StringResource
  get() = when (this) {
    ScoreLevel.Excellent -> Res.string.score_level_excellent
    ScoreLevel.Good -> Res.string.score_level_good
    ScoreLevel.Pass -> Res.string.score_level_pass
    ScoreLevel.Below -> Res.string.score_level_below
    ScoreLevel.Practice -> Res.string.score_level_practice
  }

private val SettingsMessage.textResource: StringResource
  get() = when (this) {
    SettingsMessage.AiCleared -> Res.string.settings_message_ai_cleared
    SettingsMessage.RecordsCleared -> Res.string.settings_message_records_cleared
    SettingsMessage.Saved -> Res.string.settings_message_saved
    SettingsMessage.EnableAiFirst -> Res.string.settings_message_enable_ai_first
    SettingsMessage.MissingApiConfig -> Res.string.settings_message_missing_api_config
    SettingsMessage.MissingModel -> Res.string.settings_message_missing_model
    SettingsMessage.ConnectionAvailable -> Res.string.settings_message_connection_available
  }
