package io.github.szpontium.api.hebe.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JustificationLessonRequest(
    @SerialName("Reason")
    val reason: String,
    @SerialName("LessonClassId")
    val lessonClassId: Int,
    @SerialName("PupilId")
    val pupilId: Int = 0
)

@Serializable
data class JustificationLessonResponse(
    @SerialName("Id")
    val id: Int? = null,
    @SerialName("Reason")
    val reason: String,
    @SerialName("LessonClassId")
    val lessonClassId: Int,
    @SerialName("PupilId")
    val pupilId: Int = 0,
    @SerialName("LoginId")
    val loginId: Int? = null
)
