package org.easy.schulte.core.model.configuration

import kotlin.test.Test
import kotlin.test.assertEquals
import org.easy.schulte.core.model.configuration.enums.ConfigurationFeature
import org.easy.schulte.core.model.training.enums.LayoutMode

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
}
