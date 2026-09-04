@file:UseSerializers(VulcanDateTimeSerializer::class)

package io.github.szpontium.api.hebe.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class HebeSendMessage(
    @SerialName("Id")
    val id: String,
    @SerialName("GlobalKey")
    val globalKey: String,
    @SerialName("Partition")
    val partition: String,
    @SerialName("ThreadKey")
    val threadKey: String,
    @SerialName("Subject")
    val subject: String,
    @SerialName("Content")
    val content: String,
    @SerialName("Status")
    val status: Int,
    @SerialName("Owner")
    val owner: String,
    @SerialName("SentAt")
    val sentAt: LocalDateTime,
    @SerialName("ReadAt")
    val readAt: LocalDateTime? = null,
    @SerialName("Sender")
    val sender: HebeMessageSender,
    @SerialName("Receiver")
    val receiver: List<HebeMessageReceiver>,
    @SerialName("Attachments")
    val attachments: List<Attachment>? = null,
    @SerialName("Importance")
    val importance: Int = 0,
    @SerialName("Withdrawn")
    val withdrawn: Boolean = false,
    @SerialName("Folder")
    val folder: Int = 1
)

@Serializable
object HebeSenderExtras

@Serializable
data class HebeMessageSender(
    @SerialName("Id")
    val id: String,
    @SerialName("Partition")
    val partition: String,
    @SerialName("Owner")
    val owner: String,
    @SerialName("GlobalKey")
    val globalKey: String,
    @SerialName("Name")
    val name: String,
    @SerialName("Initials")
    val initials: String,
    @SerialName("HasRead")
    val hasRead: Int = 0,
    @SerialName("Header")
    val header: Boolean = false,
    @SerialName("Extras")
    val extras: HebeSenderExtras = HebeSenderExtras
)

@Serializable
data class HebeMessageReceiver(
    @SerialName("Id")
    val id: String,
    @SerialName("Partition")
    val partition: String,
    @SerialName("Owner")
    val owner: String? = null,
    @SerialName("GlobalKey")
    val globalKey: String,
    @SerialName("Name")
    val name: String,
    @SerialName("Group")
    val group: String,
    @SerialName("Initials")
    val initials: String,
    @SerialName("HasRead")
    val hasRead: Int = 0,
    @SerialName("Header")
    val header: Boolean = false,
    @SerialName("Extras")
    val extras: HebeSenderExtras = HebeSenderExtras
)

@Serializable
data class HebeReadMessage(
    @SerialName("BoxKey")
    val boxKey: String,
    @SerialName("MessageKey")
    val messageKey: String,
    @SerialName("Status")
    val status: Int = 1
)
