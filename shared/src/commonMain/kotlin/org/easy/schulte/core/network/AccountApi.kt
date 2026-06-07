package org.easy.schulte.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.easy.schulte.core.model.Gender
import org.easy.schulte.core.model.UserAccount
import org.easy.schulte.core.platform.currentTimeMillis

internal class AccountApi(
  private val client: HttpClient,
  private val config: AccountApiConfig,
) {
  suspend fun register(
    nickname: String,
    password: String,
    gender: Gender,
    acceptedTerms: Boolean,
  ): AuthSession {
    val response = client.post("${config.baseUrl}/auth/register") {
      jsonRequest()
      setBody(
        RegisterRequest(
          password = password,
          nickname = nickname,
          gender = gender.toApiGender(),
          acceptedTerms = acceptedTerms,
          device = deviceRequest(),
        ),
      )
    }.body<JsonObject>()
    return response.toAuthSession(fallbackNickname = nickname, fallbackGender = gender, password = password)
  }

  suspend fun login(
    registrationId: String,
    password: String,
  ): AuthSession {
    val response = client.post("${config.baseUrl}/auth/login") {
      jsonRequest()
      setBody(
        LoginRequest(
          registrationId = registrationId,
          password = password,
          device = deviceRequest(),
        ),
      )
    }.body<JsonObject>()
    return response.toAuthSession(fallbackRegisterId = registrationId, password = password)
  }

  suspend fun me(accessToken: String): UserAccount {
    val response = client.get("${config.baseUrl}/me") {
      header(HttpHeaders.Accept, ContentType.Application.Json)
      bearerAuth(accessToken)
    }.body<JsonObject>()
    return response.userPayload().toUserAccount(password = "")
  }

  suspend fun updateMe(
    accessToken: String,
    nickname: String,
    gender: Gender,
    avatarUrl: String? = null,
  ): UserAccount {
    val response = client.patch("${config.baseUrl}/me") {
      jsonRequest()
      bearerAuth(accessToken)
      setBody(
        UpdateProfileRequest(
          nickname = nickname,
          gender = gender.toApiGender(),
          avatarUrl = avatarUrl,
        ),
      )
    }.body<JsonObject>()
    return response.userPayload().toUserAccount(password = "")
  }

  suspend fun logout(accessToken: String, refreshToken: String) {
    client.post("${config.baseUrl}/auth/logout") {
      jsonRequest()
      bearerAuth(accessToken)
      setBody(LogoutRequest(refreshToken))
    }
  }

  suspend fun associateGuest(accessToken: String, guestId: String) {
    client.post("${config.baseUrl}/me/training-records:associateGuest") {
      jsonRequest()
      bearerAuth(accessToken)
      setBody(AssociateGuestRequest(guestId))
    }
  }

  suspend fun clearTrainingRecords(accessToken: String) {
    client.delete("${config.baseUrl}/me/training-records") {
      jsonRequest()
      bearerAuth(accessToken)
      setBody(ClearTrainingRecordsRequest())
    }
  }

  private fun deviceRequest(): DeviceRequest = DeviceRequest(
    deviceName = config.deviceName,
    platform = when (config.platform) {
      ApiPlatform.Android -> "ANDROID"
      ApiPlatform.Ios -> "IOS"
    },
  )
}

internal data class AuthSession(
  val accessToken: String,
  val refreshToken: String,
  val user: UserAccount,
)

internal suspend fun Throwable.toAccountErrorMessage(): String = runCatching {
  when (this) {
    is ClientRequestException -> response.body<JsonObject>().errorMessage() ?: "账号或密码不正确"
    is ServerResponseException -> "服务器暂时不可用，请稍后再试"
    is ResponseException -> response.body<JsonObject>().errorMessage() ?: "请求失败，请稍后再试"
    else -> message?.takeIf { it.isNotBlank() } ?: "网络请求失败，请检查服务是否已启动"
  }
}.getOrElse {
  when (this) {
    is ServerResponseException -> "服务器暂时不可用，请稍后再试"
    is ClientRequestException -> "账号或密码不正确"
    else -> "网络请求失败，请检查服务是否已启动"
  }
}

@Serializable
private data class RegisterRequest(
  val password: String,
  val nickname: String,
  val gender: String,
  val acceptedTerms: Boolean,
  val device: DeviceRequest,
)

@Serializable
private data class LoginRequest(
  val registrationId: String,
  val password: String,
  val device: DeviceRequest,
)

@Serializable
private data class UpdateProfileRequest(
  val nickname: String,
  val gender: String,
  val avatarUrl: String? = null,
)

@Serializable
private data class LogoutRequest(
  val refreshToken: String,
)

@Serializable
private data class AssociateGuestRequest(
  val guestId: String,
)

@Serializable
private data class ClearTrainingRecordsRequest(
  val confirm: String = "CLEAR_MY_TRAINING_RECORDS",
)

@Serializable
private data class DeviceRequest(
  val deviceName: String,
  val platform: String,
)

private fun io.ktor.client.request.HttpRequestBuilder.jsonRequest() {
  contentType(ContentType.Application.Json)
  header(HttpHeaders.Accept, ContentType.Application.Json)
}

private fun JsonObject.toAuthSession(
  fallbackRegisterId: String = "",
  fallbackNickname: String = "",
  fallbackGender: Gender = Gender.Private,
  password: String,
): AuthSession {
  val userObject = nestedObject("user") ?: nestedObject("account") ?: this
  val tokenObject = nestedObject("tokens") ?: nestedObject("token") ?: this
  val registerId = userObject.string("registrationId", "registerId", "registrationID")
    ?: string("registrationId", "registerId", "registrationID")
    ?: fallbackRegisterId
  val user = userObject.toUserAccount(
    fallbackRegisterId = registerId,
    fallbackNickname = fallbackNickname,
    fallbackGender = fallbackGender,
    password = password,
  )
  return AuthSession(
    accessToken = tokenObject.string("accessToken", "access_token") ?: "",
    refreshToken = tokenObject.string("refreshToken", "refresh_token") ?: "",
    user = user,
  )
}

private fun JsonObject.userPayload(): JsonObject = nestedObject("user") ?: nestedObject("account") ?: this

private fun JsonObject.toUserAccount(
  fallbackRegisterId: String = "",
  fallbackNickname: String = "",
  fallbackGender: Gender = Gender.Private,
  password: String,
): UserAccount = UserAccount(
  userId = string("userId", "id") ?: fallbackRegisterId.ifBlank { "remote_${currentTimeMillis()}" },
  registerId = string("registrationId", "registerId", "registrationID") ?: fallbackRegisterId,
  nickname = string("nickname", "name") ?: fallbackNickname.ifBlank { "舒尔特用户" },
  password = password,
  gender = string("gender")?.toGender() ?: fallbackGender,
  createdAt = currentTimeMillis(),
)

private fun JsonObject.errorMessage(): String? = nestedObject("error")?.string("message") ?: string("message")

private fun JsonObject.nestedObject(key: String): JsonObject? = get(key)?.jsonObjectOrNull()

private fun JsonObject.string(vararg keys: String): String? = keys.firstNotNullOfOrNull { key ->
  get(key)?.stringOrNull()?.takeIf { it.isNotBlank() }
}

private fun JsonElement.jsonObjectOrNull(): JsonObject? = this as? JsonObject

private fun JsonElement.stringOrNull(): String? = when (this) {
  is JsonPrimitive -> jsonPrimitive.contentOrNull
  else -> null
}

private fun Gender.toApiGender(): String = when (this) {
  Gender.Male -> "MALE"
  Gender.Female -> "FEMALE"
  Gender.Private -> "UNDISCLOSED"
}

private fun String.toGender(): Gender = when (uppercase()) {
  "MALE" -> Gender.Male
  "FEMALE" -> Gender.Female
  else -> Gender.Private
}
