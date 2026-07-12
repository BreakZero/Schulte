package org.easy.schulte.core.security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AiApiKeyStoreTest {
  @Test
  fun saveThenReadRestoresTheApiKeyFromSecureStorage() {
    val store = AiApiKeyStore(FakeAiSecretStore())

    store.save("provider-key")

    assertEquals("provider-key", store.read())
    assertTrue(store.hasApiKey())
  }

  @Test
  fun savingBlankValueClearsTheApiKey() {
    val store = AiApiKeyStore(FakeAiSecretStore()).apply {
      save("provider-key")
    }

    store.save(" ")

    assertNull(store.read())
    assertFalse(store.hasApiKey())
  }

  @Test
  fun clearRemovesTheApiKey() {
    val store = AiApiKeyStore(FakeAiSecretStore()).apply {
      save("provider-key")
    }

    store.clear()

    assertNull(store.read())
  }
}

private class FakeAiSecretStore : SecureSecretStore {
  private val values = mutableMapOf<String, String>()

  override fun read(key: String): String? = values[key]

  override fun write(key: String, value: String) {
    values[key] = value
  }

  override fun remove(key: String) {
    values.remove(key)
  }
}
