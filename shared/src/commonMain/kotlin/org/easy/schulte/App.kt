package org.easy.schulte

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.easy.schulte.core.data.DatabaseDriverFactory
import org.easy.schulte.core.ui.PageBackground
import org.easy.schulte.di.appModule
import org.easy.schulte.navigation.SchulteNavGraph
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun App(databaseDriverFactory: DatabaseDriverFactory) {
  KoinApplication(
    configuration = koinConfiguration {
      modules(appModule(databaseDriverFactory))
    },
  ) {
    MaterialTheme {
      SchulteRoot()
    }
  }
}

@Composable
internal fun SchulteRoot() {
  Surface(
    modifier = Modifier.fillMaxSize(),
    color = PageBackground,
  ) {
    SchulteNavGraph(modifier = Modifier.fillMaxSize())
  }
}
