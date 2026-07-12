package org.easy.schulte

import androidx.compose.ui.window.ComposeUIViewController
import org.easy.schulte.core.data.IosDatabaseDriverFactory
import org.easy.schulte.core.security.IosSecureSecretStore

@Suppress("FunctionName")
fun MainViewController() = ComposeUIViewController {
  App(
    databaseDriverFactory = IosDatabaseDriverFactory(),
    secureSecretStore = IosSecureSecretStore(),
  )
}
