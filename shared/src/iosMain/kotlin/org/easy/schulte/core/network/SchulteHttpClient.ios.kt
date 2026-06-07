package org.easy.schulte.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

internal actual fun createSchulteHttpClient(): HttpClient = HttpClient(Darwin) {
  installSchulteDefaults()
}
