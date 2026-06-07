package org.easy.schulte.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

internal actual fun createSchulteHttpClient(): HttpClient = HttpClient(CIO) {
  installSchulteDefaults()
}
