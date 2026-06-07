package org.easy.schulte.core.data

import kotlinx.coroutines.flow.Flow
import org.easy.schulte.core.model.AppConfiguration
import org.easy.schulte.core.model.ConfigurationFeature
import org.easy.schulte.core.model.FeatureConfiguration

internal interface ConfigurationStore {
  fun getConfiguration(): AppConfiguration
  fun observeFeatureConfiguration(feature: ConfigurationFeature): Flow<FeatureConfiguration>
  fun updateConfiguration(configuration: AppConfiguration)
}
