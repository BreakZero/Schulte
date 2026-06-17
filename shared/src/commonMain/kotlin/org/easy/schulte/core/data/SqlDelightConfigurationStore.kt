package org.easy.schulte.core.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.easy.schulte.core.model.ai.AiSettings
import org.easy.schulte.core.model.configuration.AppConfiguration
import org.easy.schulte.core.model.configuration.FeatureConfiguration
import org.easy.schulte.core.model.configuration.enums.ConfigurationFeature
import org.easy.schulte.core.model.configuration.toFeatureConfiguration
import org.easy.schulte.core.model.records.enums.RecordGridFilter
import org.easy.schulte.core.model.records.enums.RecordModeFilter
import org.easy.schulte.core.model.records.enums.RecordTimeFilter
import org.easy.schulte.core.model.training.enums.AgeGroup
import org.easy.schulte.core.model.training.enums.GridSpec
import org.easy.schulte.core.model.training.enums.MarkMode
import org.easy.schulte.core.platform.currentTimeMillis

internal class SqlDelightConfigurationStore(
  databaseProvider: SchulteDatabaseProvider,
) : ConfigurationStore {
  private val queries = databaseProvider.database.schulteDatabaseQueries

  override fun getConfiguration(): AppConfiguration = readConfiguration()

  override fun getFeatureConfiguration(feature: ConfigurationFeature): FeatureConfiguration = getConfiguration().toFeatureConfiguration(feature)

  override fun observeFeatureConfiguration(feature: ConfigurationFeature): Flow<FeatureConfiguration> = queries
    .selectFeatureConfigurations(GLOBAL_FEATURE)
    .asFlow()
    .mapToList(Dispatchers.IO)
    .map { rows ->
      rows.associate { it.config_key to it.config_value }
        .toConfiguration()
        .toFeatureConfiguration(feature)
    }

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
  }

  private fun readConfiguration(): AppConfiguration {
    val values = queries.selectFeatureConfigurations(GLOBAL_FEATURE)
      .executeAsList()
      .associate { it.config_key to it.config_value }
    return values.toConfiguration()
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

private fun Map<String, String>.toConfiguration(): AppConfiguration = AppConfiguration(
  selectedGrid = enumValue(KEY_SELECTED_GRID, GridSpec.Five),
  selectedAgeGroup = enumValue(KEY_SELECTED_AGE_GROUP, AgeGroup.Adult),
  selectedMarkMode = enumValue(KEY_SELECTED_MARK_MODE, MarkMode.BriefFeedbackOnly),
  aiSettings = AiSettings(
    assistedMarkingEnabled = booleanValue(KEY_ASSISTED_MARKING_ENABLED, false),
    aiEnabled = booleanValue(KEY_AI_ENABLED, false),
    apiKey = get(KEY_AI_API_KEY).orEmpty(),
    baseUrl = get(KEY_AI_BASE_URL).orEmpty(),
    modelName = get(KEY_AI_MODEL_NAME) ?: "gpt-4o-mini",
  ),
  recordGridFilter = enumValue(KEY_RECORD_GRID_FILTER, RecordGridFilter.All),
  recordModeFilter = enumValue(KEY_RECORD_MODE_FILTER, RecordModeFilter.All),
  recordTimeFilter = enumValue(KEY_RECORD_TIME_FILTER, RecordTimeFilter.All),
)

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
