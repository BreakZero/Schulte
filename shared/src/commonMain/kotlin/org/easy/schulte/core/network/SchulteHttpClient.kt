package org.easy.schulte.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal val SchulteJson = Json {
  ignoreUnknownKeys = true
  isLenient = true
  explicitNulls = false
}

internal expect fun createSchulteHttpClient(): HttpClient

internal fun HttpClientConfig.installSchulteDefaults() {
  expectSuccess = true
  install(ContentNegotiation) {
    json(SchulteJson)
  }
}

internal typealias HttpClientConfig = io.ktor.client.HttpClientConfig<*>
