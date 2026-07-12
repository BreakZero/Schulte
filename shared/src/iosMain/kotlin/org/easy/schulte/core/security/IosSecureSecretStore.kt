package org.easy.schulte.core.security

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosSecureSecretStore : SecureSecretStore {
  override fun read(key: String): String? = memScoped {
    val result = alloc<CFTypeRefVar>()
    val status = SecItemCopyMatching(query(key, includeData = true), result.ptr)
    when (status) {
      errSecSuccess -> (result.ptr.pointed.value?.let(::CFBridgingRelease) as? NSData)
        ?.toByteArray()
        ?.decodeToString()

      errSecItemNotFound -> null

      else -> null
    }
  }

  override fun write(key: String, value: String) {
    val attributes = CFDictionaryCreateMutable(null, 0, null, null)
      ?: error("Unable to create Keychain attributes")
    CFDictionaryAddValue(attributes, kSecValueData, CFBridgingRetain(value.encodeToByteArray().toNSData()))

    val updateStatus = SecItemUpdate(query(key), attributes)
    if (updateStatus == errSecItemNotFound) {
      val addQuery = query(key)
      CFDictionaryAddValue(addQuery, kSecValueData, CFBridgingRetain(value.encodeToByteArray().toNSData()))
      SecItemAdd(addQuery, null)
    }
  }

  override fun remove(key: String) {
    SecItemDelete(query(key))
  }

  private fun query(key: String, includeData: Boolean = false): CFMutableDictionaryRef {
    val query = CFDictionaryCreateMutable(null, 0, null, null)
      ?: error("Unable to create Keychain query")
    CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
    CFDictionaryAddValue(query, kSecAttrService, CFBridgingRetain(SERVICE_NAME))
    CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(key))
    if (includeData) {
      CFDictionaryAddValue(query, kSecReturnData, kCFBooleanTrue)
      CFDictionaryAddValue(query, kSecMatchLimit, kSecMatchLimitOne)
    }
    return query
  }

  private fun ByteArray.toNSData(): NSData = usePinned { pinned ->
    NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
  }

  private fun NSData.toByteArray(): ByteArray = ByteArray(length.toInt()).also { bytes ->
    bytes.usePinned { pinned ->
      memcpy(pinned.addressOf(0), this.bytes, length)
    }
  }

  private companion object {
    const val SERVICE_NAME = "org.easy.schulte.secure-secrets"
  }
}
