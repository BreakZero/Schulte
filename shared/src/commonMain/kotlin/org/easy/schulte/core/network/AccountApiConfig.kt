package org.easy.schulte.core.network

internal data class AccountApiConfig(
  val baseUrl: String = defaultAccountApiBaseUrl(),
  val deviceName: String = defaultDeviceName(),
  val platform: ApiPlatform = defaultApiPlatform(),
)

internal enum class ApiPlatform {
  Android,
  Ios,
}

internal expect fun defaultAccountApiBaseUrl(): String

internal expect fun defaultDeviceName(): String

internal expect fun defaultApiPlatform(): ApiPlatform
