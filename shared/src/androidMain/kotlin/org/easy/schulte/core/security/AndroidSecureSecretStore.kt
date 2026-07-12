package org.easy.schulte.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class AndroidSecureSecretStore(context: Context) : SecureSecretStore {
  private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

  override fun read(key: String): String? {
    val encodedValue = preferences.getString(key, null) ?: return null
    return runCatching { decrypt(encodedValue) }
      .getOrNull()
  }

  override fun write(key: String, value: String) {
    preferences.edit()
      .putString(key, encrypt(value))
      .apply()
  }

  override fun remove(key: String) {
    preferences.edit().remove(key).apply()
  }

  private fun encrypt(value: String): String {
    val cipher = Cipher.getInstance(TRANSFORMATION)
    cipher.init(Cipher.ENCRYPT_MODE, secretKey())
    val cipherText = cipher.doFinal(value.encodeToByteArray())
    return Base64.encodeToString(
      ByteBuffer.allocate(cipher.iv.size + cipherText.size)
        .put(cipher.iv)
        .put(cipherText)
        .array(),
      Base64.NO_WRAP,
    )
  }

  private fun decrypt(encodedValue: String): String {
    val encrypted = Base64.decode(encodedValue, Base64.NO_WRAP)
    require(encrypted.size > IV_SIZE_BYTES) { "Malformed encrypted secret" }
    val iv = encrypted.copyOfRange(0, IV_SIZE_BYTES)
    val cipherText = encrypted.copyOfRange(IV_SIZE_BYTES, encrypted.size)
    val cipher = Cipher.getInstance(TRANSFORMATION)
    cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(TAG_SIZE_BITS, iv))
    return cipher.doFinal(cipherText).decodeToString()
  }

  private fun secretKey(): SecretKey {
    val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
    (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

    return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE).apply {
      init(
        KeyGenParameterSpec.Builder(
          KEY_ALIAS,
          KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
          .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
          .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
          .setKeySize(KEY_SIZE_BITS)
          .build(),
      )
    }.generateKey()
  }

  private companion object {
    const val PREFERENCES_NAME = "schulte_secure_secrets"
    const val ANDROID_KEY_STORE = "AndroidKeyStore"
    const val KEY_ALIAS = "schulte_secure_secret_key_v1"
    const val TRANSFORMATION = "AES/GCM/NoPadding"
    const val IV_SIZE_BYTES = 12
    const val TAG_SIZE_BITS = 128
    const val KEY_SIZE_BITS = 256
  }
}
