package org.easy.schulte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.easy.schulte.core.data.AndroidDatabaseDriverFactory
import org.easy.schulte.core.security.AndroidSecureSecretStore

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    setContent {
      App(
        databaseDriverFactory = AndroidDatabaseDriverFactory(this),
        secureSecretStore = AndroidSecureSecretStore(this),
      )
    }
  }
}
