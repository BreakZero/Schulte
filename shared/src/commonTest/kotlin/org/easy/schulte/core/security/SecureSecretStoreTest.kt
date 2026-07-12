package org.easy.schulte.core.security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SecureSecretStoreTest {
  @Test
  fun writeThenReadReturnsTheSavedValue() {
    val store = FakeSecureSecretStore()

    store.write(key = "access_token", value = "secret-value")

    assertEquals("secret-value", store.read("access_token"))
  }

  @Test
  fun removeMakesTheValueUnavailable() {
    val store = FakeSecureSecretStore().apply {
      write(key = "access_token", value = "secret-value")
    }

    store.remove("access_token")

    assertNull(store.read("access_token"))
  }

  @Test
  fun readingAnUnknownKeyReturnsNull() {
    assertNull(FakeSecureSecretStore().read("missing"))
  }
}

private class FakeSecureSecretStore : SecureSecretStore {
  private val values = mutableMapOf<String, String>()

  override fun read(key: String): String? = values[key]

  override fun write(key: String, value: String) {
    values[key] = value
  }

  override fun remove(key: String) {
    values.remove(key)
  }
}
