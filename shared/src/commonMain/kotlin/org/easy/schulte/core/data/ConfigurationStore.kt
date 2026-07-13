package org.easy.schulte.core.data

import kotlinx.coroutines.flow.Flow
import org.easy.schulte.core.model.configuration.AppConfiguration
import org.easy.schulte.core.model.configuration.FeatureConfiguration
import org.easy.schulte.core.model.configuration.enums.ConfigurationFeature

internal interface ConfigurationStore {
  suspend fun getConfiguration(): AppConfiguration
  suspend fun getFeatureConfiguration(feature: ConfigurationFeature): FeatureConfiguration
  fun observeFeatureConfiguration(feature: ConfigurationFeature): Flow<FeatureConfiguration>
  suspend fun updateConfiguration(configuration: AppConfiguration)
}
