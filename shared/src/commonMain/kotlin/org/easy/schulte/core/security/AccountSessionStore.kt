package org.easy.schulte.core.security

/**
 * Persists an authenticated account session without exposing its tokens to the app database.
 *
 * The implementation is common code; [SecureSecretStore] provides Android Keystore and iOS
 * Keychain protection for the individual values.
 */
internal data class AccountSession(
  val accessToken: String,
  val refreshToken: String,
  val userId: String,
)

internal interface AccountSessionStore {
  fun read(): AccountSession?

  fun save(session: AccountSession)

  fun clear()
}

internal class SecureAccountSessionStore(
  private val secretStore: SecureSecretStore,
) : AccountSessionStore {
  override fun read(): AccountSession? {
    val accessToken = secretStore.read(ACCESS_TOKEN_KEY)
    val refreshToken = secretStore.read(REFRESH_TOKEN_KEY)
    val userId = secretStore.read(USER_ID_KEY)
    if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank() || userId.isNullOrBlank()) {
      clear()
      return null
    }
    return AccountSession(accessToken, refreshToken, userId)
  }

  override fun save(session: AccountSession) {
    secretStore.write(ACCESS_TOKEN_KEY, session.accessToken)
    secretStore.write(REFRESH_TOKEN_KEY, session.refreshToken)
    secretStore.write(USER_ID_KEY, session.userId)
  }

  override fun clear() {
    secretStore.remove(ACCESS_TOKEN_KEY)
    secretStore.remove(REFRESH_TOKEN_KEY)
    secretStore.remove(USER_ID_KEY)
  }

  private companion object {
    const val ACCESS_TOKEN_KEY = "account_access_token"
    const val REFRESH_TOKEN_KEY = "account_refresh_token"
    const val USER_ID_KEY = "account_user_id"
  }
}
