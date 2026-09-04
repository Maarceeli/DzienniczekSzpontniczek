@file:UseSerializers(VulcanDateTimeSerializer::class, VulcanDateSerializer::class)

package io.github.szpontium.api.hebe.models

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class KindergartenPresence(
    @SerialName("Id")
    val id: Int,
    @SerialName("GlobalKey")
    val globalKey: String,
    @SerialName("PupilId")
    val pupilId: Int,
    @SerialName("DateAt")
    val dateAt: LocalDate,
    @SerialName("HourFrom")
    @Serializable(with = VulcanLocalTimeSerializer::class)
    val hourFrom: LocalTime,
    @SerialName("HourTo")
    @Serializable(with = VulcanNullableLocalTimeSerializer::class)
    val hourTo: LocalTime? = null,
    @SerialName("CreatedAt")
    val createdAt: LocalDateTime,
    @SerialName("ModifiedAt")
    val modifiedAt: LocalDateTime,
    @SerialName("Teacher")
    val teacher: Teacher
)

@Serializable
data class KindergartenAbsence(
    @SerialName("Id")
    val id: Int,
    @SerialName("GlobalKey")
    val globalKey: String,
    @SerialName("PupilId")
    val pupilId: Int,
    @SerialName("From")
    val dateFrom: LocalDate,
    @SerialName("To")
    val dateTo: LocalDate,
    @SerialName("CreatedAt")
    val createdAt: LocalDateTime,
    @SerialName("ModifiedAt")
    val modifiedAt: LocalDateTime
)
