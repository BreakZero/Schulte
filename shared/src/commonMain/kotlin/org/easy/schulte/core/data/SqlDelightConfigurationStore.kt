package org.easy.schulte.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.easy.schulte.core.model.AgeGroup
import org.easy.schulte.core.model.AiSettings
import org.easy.schulte.core.model.AppConfiguration
import org.easy.schulte.core.model.ConfigurationFeature
import org.easy.schulte.core.model.FeatureConfiguration
import org.easy.schulte.core.model.GridSpec
import org.easy.schulte.core.model.MarkMode
import org.easy.schulte.core.model.RecordGridFilter
import org.easy.schulte.core.model.RecordModeFilter
import org.easy.schulte.core.model.RecordTimeFilter
import org.easy.schulte.core.model.toFeatureConfiguration
import org.easy.schulte.core.platform.currentTimeMillis

internal class SqlDelightConfigurationStore(
  databaseProvider: SchulteDatabaseProvider,
) : ConfigurationStore {
  private val queries = databaseProvider.database.schulteDatabaseQueries
  private val configuration = MutableStateFlow(readConfiguration())

  override fun getConfiguration(): AppConfiguration = configuration.value

  override fun getFeatureConfiguration(feature: ConfigurationFeature): FeatureConfiguration =
    configuration.value.toFeatureConfiguration(feature)

  override fun observeFeatureConfiguration(feature: ConfigurationFeature): Flow<FeatureConfiguration> = configuration.map { it.toFeatureConfiguration(feature) }

  override fun updateConfiguration(configuration: AppConfiguration) {
    queries.transaction {
      KEY_VALUES.forEach { key ->
        queries.upsertConfiguration(
          feature = GLOBAL_FEATURE,
          config_key = key,
          config_value = configuration.valueFor(key),
          updated_at = currentTimeMillis(),
        )
      }
    }
    this.configuration.update { configuration }
  }

  private fun readConfiguration(): AppConfiguration {
    val values = queries.selectFeatureConfigurations(GLOBAL_FEATURE)
      .executeAsList()
      .associate { it.config_key to it.config_value }
    return AppConfiguration(
      selectedGrid = values.enumValue(KEY_SELECTED_GRID, GridSpec.Five),
      selectedAgeGroup = values.enumValue(KEY_SELECTED_AGE_GROUP, AgeGroup.Adult),
      selectedMarkMode = values.enumValue(KEY_SELECTED_MARK_MODE, MarkMode.BriefFeedbackOnly),
      aiSettings = AiSettings(
        assistedMarkingEnabled = values.booleanValue(KEY_ASSISTED_MARKING_ENABLED, false),
        aiEnabled = values.booleanValue(KEY_AI_ENABLED, false),
        apiKey = values[KEY_AI_API_KEY].orEmpty(),
        baseUrl = values[KEY_AI_BASE_URL].orEmpty(),
        modelName = values[KEY_AI_MODEL_NAME] ?: "gpt-4o-mini",
      ),
      recordGridFilter = values.enumValue(KEY_RECORD_GRID_FILTER, RecordGridFilter.All),
      recordModeFilter = values.enumValue(KEY_RECORD_MODE_FILTER, RecordModeFilter.All),
      recordTimeFilter = values.enumValue(KEY_RECORD_TIME_FILTER, RecordTimeFilter.All),
    )
  }

  private fun AppConfiguration.valueFor(key: String): String = when (key) {
    KEY_SELECTED_GRID -> selectedGrid.name
    KEY_SELECTED_AGE_GROUP -> selectedAgeGroup.name
    KEY_SELECTED_MARK_MODE -> selectedMarkMode.name
    KEY_ASSISTED_MARKING_ENABLED -> aiSettings.assistedMarkingEnabled.toString()
    KEY_AI_ENABLED -> aiSettings.aiEnabled.toString()
    KEY_AI_API_KEY -> aiSettings.apiKey
    KEY_AI_BASE_URL -> aiSettings.baseUrl
    KEY_AI_MODEL_NAME -> aiSettings.modelName
    KEY_RECORD_GRID_FILTER -> recordGridFilter.name
    KEY_RECORD_MODE_FILTER -> recordModeFilter.name
    KEY_RECORD_TIME_FILTER -> recordTimeFilter.name
    else -> error("Unsupported configuration key: $key")
  }
}

private const val GLOBAL_FEATURE = "global"
private const val KEY_SELECTED_GRID = "selected_grid"
private const val KEY_SELECTED_AGE_GROUP = "selected_age_group"
private const val KEY_SELECTED_MARK_MODE = "selected_mark_mode"
private const val KEY_ASSISTED_MARKING_ENABLED = "assisted_marking_enabled"
private const val KEY_AI_ENABLED = "ai_enabled"
private const val KEY_AI_API_KEY = "ai_api_key"
private const val KEY_AI_BASE_URL = "ai_base_url"
private const val KEY_AI_MODEL_NAME = "ai_model_name"
private const val KEY_RECORD_GRID_FILTER = "record_grid_filter"
private const val KEY_RECORD_MODE_FILTER = "record_mode_filter"
private const val KEY_RECORD_TIME_FILTER = "record_time_filter"

private val KEY_VALUES = listOf(
  KEY_SELECTED_GRID,
  KEY_SELECTED_AGE_GROUP,
  KEY_SELECTED_MARK_MODE,
  KEY_ASSISTED_MARKING_ENABLED,
  KEY_AI_ENABLED,
  KEY_AI_API_KEY,
  KEY_AI_BASE_URL,
  KEY_AI_MODEL_NAME,
  KEY_RECORD_GRID_FILTER,
  KEY_RECORD_MODE_FILTER,
  KEY_RECORD_TIME_FILTER,
)

private inline fun <reified T : Enum<T>> Map<String, String>.enumValue(key: String, default: T): T = get(key)?.let { value ->
  enumValues<T>().firstOrNull { it.name == value }
} ?: default

private fun Map<String, String>.booleanValue(key: String, default: Boolean): Boolean = get(key)?.toBooleanStrictOrNull() ?: default
