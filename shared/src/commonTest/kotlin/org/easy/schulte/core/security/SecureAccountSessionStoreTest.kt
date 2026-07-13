package org.easy.schulte.core.security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SecureAccountSessionStoreTest {
  @Test
  fun saveThenReadRestoresBothTokens() {
    val store = SecureAccountSessionStore(FakeSessionSecretStore())

    store.save(AccountSession(accessToken = "access", refreshToken = "refresh", userId = "user-1"))

    assertEquals(
      AccountSession(accessToken = "access", refreshToken = "refresh", userId = "user-1"),
      store.read(),
    )
  }

  @Test
  fun readWithOnlyOneStoredTokenClearsThePartialSession() {
    val secrets = FakeSessionSecretStore().apply {
      write("account_access_token", "access")
    }
    val store = SecureAccountSessionStore(secrets)

    assertNull(store.read())
    assertNull(secrets.read("account_access_token"))
  }

  @Test
  fun clearRemovesBothTokens() {
    val secrets = FakeSessionSecretStore()
    val store = SecureAccountSessionStore(secrets).apply {
      save(AccountSession(accessToken = "access", refreshToken = "refresh", userId = "user-1"))
    }

    store.clear()

    assertNull(secrets.read("account_access_token"))
    assertNull(secrets.read("account_refresh_token"))
  }
}

private class FakeSessionSecretStore : SecureSecretStore {
  private val values = mutableMapOf<String, String>()

  override fun read(key: String): String? = values[key]

  override fun write(key: String, value: String) {
    values[key] = value
  }

  override fun remove(key: String) {
    values.remove(key)
  }
}
