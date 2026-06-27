package org.easy.schulte.core.model.configuration

import org.easy.schulte.core.model.configuration.enums.ConfigurationFeature
import org.easy.schulte.core.model.records.enums.RecordLayoutFilter
import org.easy.schulte.core.model.training.enums.LayoutMode
import kotlin.test.Test
import kotlin.test.assertEquals

class AppConfigurationMappersTest {
  @Test
  fun defaultsLayoutModeToStatic() {
    assertEquals(LayoutMode.Static, AppConfiguration().selectedLayoutMode)
  }

  @Test
  fun mapsLayoutModeToTrainingConfigurationFeatures() {
    val configuration = AppConfiguration(
      selectedLayoutMode = LayoutMode.ShuffleAfterCorrectTap,
    )

    listOf(
      ConfigurationFeature.Config,
      ConfigurationFeature.Training,
      ConfigurationFeature.Settings,
    ).forEach { feature ->
      assertEquals(
        LayoutMode.ShuffleAfterCorrectTap,
        configuration.toFeatureConfiguration(feature).selectedLayoutMode,
      )
    }
  }

  @Test
  fun mapsIndependentLayoutFilterOnlyToRecordsConfiguration() {
    val configuration = AppConfiguration(
      recordLayoutFilter = RecordLayoutFilter.Dynamic,
    )

    assertEquals(
      RecordLayoutFilter.Dynamic,
      configuration.toFeatureConfiguration(ConfigurationFeature.Records).recordLayoutFilter,
    )
    assertEquals(
      null,
      configuration.toFeatureConfiguration(ConfigurationFeature.Training).recordLayoutFilter,
    )
  }
}
