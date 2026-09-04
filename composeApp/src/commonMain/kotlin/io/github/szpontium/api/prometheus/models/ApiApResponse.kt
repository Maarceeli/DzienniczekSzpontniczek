package io.github.szpontium.api.prometheus.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiApResponse(
    @SerialName("Success")
    val success: Boolean,
    @SerialName("Tokens")
    val tokens: List<String> = emptyList(),
    @SerialName("Alias")
    val alias: String = "",
    @SerialName("Email")
    val email: String? = null,
    @SerialName("GivenName")
    val givenName: String? = null,
    @SerialName("Surname")
    val surname: String? = null,
    @SerialName("IsConsentAccepted")
    val isConsentAccepted: Boolean = true,
    @SerialName("CanAcceptConsent")
    val canAcceptConsent: Boolean = false,
    @SerialName("AccessToken")
    val accessToken: String = "",
    @SerialName("ErrorMessage")
    val errorMessage: String? = null
)
