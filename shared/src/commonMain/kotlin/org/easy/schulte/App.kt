package org.easy.schulte

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.easy.schulte.core.data.DatabaseDriverFactory
import org.easy.schulte.core.security.SecureSecretStore
import org.easy.schulte.core.ui.SchulteTheme
import org.easy.schulte.di.appModule
import org.easy.schulte.navigation.SchulteNavGraph
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun App(
  databaseDriverFactory: DatabaseDriverFactory,
  secureSecretStore: SecureSecretStore,
) {
  KoinApplication(
    configuration = koinConfiguration {
      modules(appModule(databaseDriverFactory, secureSecretStore))
    },
  ) {
    SchulteTheme {
      SchulteRoot()
    }
  }
}

@Composable
internal fun SchulteRoot() {
  Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background,
  ) {
    SchulteNavGraph(modifier = Modifier.fillMaxSize())
  }
}
