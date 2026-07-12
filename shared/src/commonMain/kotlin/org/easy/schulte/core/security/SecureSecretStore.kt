package org.easy.schulte.core.security

/**
 * Stores secrets that must not be written to the application database.
 *
 * Platform implementations use Android Keystore and iOS Keychain respectively.
 * A missing key is represented by [read] returning `null`.
 */
interface SecureSecretStore {
  fun read(key: String): String?

  fun write(key: String, value: String)

  fun remove(key: String)
}
