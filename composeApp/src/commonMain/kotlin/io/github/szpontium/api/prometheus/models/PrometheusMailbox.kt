package io.github.szpontium.api.prometheus.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrometheusMailbox(
    @SerialName("globalKey")
    val globalKey: String,
    @SerialName("nazwa")
    val nazwa: String,
    @SerialName("typUzytkownika")
    val typUzytkownika: Int
)
