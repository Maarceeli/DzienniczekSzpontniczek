package io.github.szpontium.api

import io.github.szpontium.api.hebe.appOS
import io.github.szpontium.api.hebe.appUserAgent
import io.github.szpontium.api.hebe.appVersion
import io.github.szpontium.api.hebe.appVersionCode
import io.github.szpontium.api.hebe.hebeCeIdentity
import io.github.szpontium.api.hebe.hebeIdentity
import io.github.szpontium.api.hebe.models.KindergartenAbsence
import io.github.szpontium.api.hebe.models.KindergartenPresence
import io.github.szpontium.api.hebe.models.Teacher
import io.github.szpontium.api.prometheus.models.ApiApResponse
import io.github.szpontium.api.prometheus.models.MailboxRole
import io.github.szpontium.api.prometheus.models.VulcanMailboxName
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ApiModelsTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    @Test
    fun testMailboxNameParsing() {
        // Teacher with code
        val teacherMailbox = "Nowak Jan [NJ] - P - (Liceum Ogólnokształcące nr 1)"
        val parsedTeacher = VulcanMailboxName.parse(teacherMailbox)
        assertNotNull(parsedTeacher)
        assertEquals("Nowak Jan", parsedTeacher.name)
        assertEquals("NJ", parsedTeacher.code)
        assertEquals(MailboxRole.TEACHER, parsedTeacher.role)
        assertEquals("Liceum Ogólnokształcące nr 1", parsedTeacher.shortSchoolName)
        assertNull(parsedTeacher.studentName)

        // Parent with student name
        val parentMailbox = "Kowalska Anna - R - Jan Kowalski - (Szkoła Podstawowa nr 2)"
        val parsedParent = VulcanMailboxName.parse(parentMailbox)
        assertNotNull(parsedParent)
        assertEquals("Kowalska Anna", parsedParent.name)
        assertEquals(MailboxRole.PARENT, parsedParent.role)
        assertEquals("Jan Kowalski", parsedParent.studentName)
        assertEquals("Szkoła Podstawowa nr 2", parsedParent.shortSchoolName)

        // Student mailbox
        val studentMailbox = "Kowalski Jan - U - (Szkoła Podstawowa nr 2)"
        val parsedStudent = VulcanMailboxName.parse(studentMailbox)
        assertNotNull(parsedStudent)
        assertEquals("Kowalski Jan", parsedStudent.name)
        assertEquals(MailboxRole.STUDENT, parsedStudent.role)
        assertEquals("Szkoła Podstawowa nr 2", parsedStudent.shortSchoolName)
    }

    @Test
    fun testApiApResponseSerialization() {
        val rawJson = """
            {
                "Success": true,
                "Tokens": ["header.payload.signature"],
                "Alias": "jankowalski",
                "Email": "jan@example.com",
                "IsConsentAccepted": true,
                "CanAcceptConsent": false,
                "AccessToken": "test_access_token",
                "ErrorMessage": null
            }
        """.trimIndent()

        val parsed = json.decodeFromString<ApiApResponse>(rawJson)
        assertEquals(true, parsed.success)
        assertEquals(1, parsed.tokens.size)
        assertEquals("test_access_token", parsed.accessToken)
        assertEquals(true, parsed.isConsentAccepted)
    }

    @Test
    fun testKindergartenPresenceSerializationWithNullHourTo() {
        val presence = KindergartenPresence(
            id = 101,
            globalKey = "key-101",
            pupilId = 1,
            dateAt = LocalDate(2026, 9, 4),
            hourFrom = LocalTime(8, 0, 0),
            hourTo = null,
            createdAt = LocalDateTime(2026, 9, 4, 8, 5, 0),
            modifiedAt = LocalDateTime(2026, 9, 4, 8, 5, 0),
            teacher = Teacher(
                description = "Wychowawca",
                position = 1,
                boxId = "box-1",
                id = 10,
                surname = "Nowak",
                name = "Ewa",
                displayName = "Ewa Nowak"
            )
        )

        val encoded = json.encodeToString(KindergartenPresence.serializer(), presence)
        val decoded = json.decodeFromString(KindergartenPresence.serializer(), encoded)
        assertEquals(101, decoded.id)
        assertEquals(LocalTime(8, 0, 0), decoded.hourFrom)
        assertNull(decoded.hourTo)
    }

    @Test
    fun testHebeHttpIdentityResolution() {
        // Android
        assertEquals("26.07.00 (G)", hebeIdentity.appVersion("Android Phone"))
        assertEquals("988", hebeIdentity.appVersionCode("Android Phone"))
        assertEquals("Android", hebeIdentity.appOS("Android Phone"))
        assertEquals("Dart/3.11 (dart:io)", hebeIdentity.appUserAgent("Android Phone"))

        // iOS / iPhone
        assertEquals("26.07.00", hebeIdentity.appVersion("iPhone 15 Pro"))
        assertEquals("988", hebeIdentity.appVersionCode("iPhone 15 Pro"))
        assertEquals("iOS", hebeIdentity.appOS("iPhone 15 Pro"))
        assertEquals("Dart/3.11 (dart:io)", hebeIdentity.appUserAgent("iPhone 15 Pro"))

        // HebeCE
        assertEquals("26.06.02 (G)", hebeCeIdentity.appVersion("Pixel 9"))
        assertEquals("998", hebeCeIdentity.appVersionCode("Pixel 9"))
        assertEquals("26.06.02", hebeCeIdentity.appVersion("iPhone 16"))
        assertEquals("998", hebeCeIdentity.appVersionCode("iPhone 16"))
    }

    @Test
    fun testLibrusMapperToHebeAccount() {
        val synergiaAccount = io.github.szpontium.api.librus.models.LibrusSynergiaAccount(
            id = 12345,
            login = "12345u",
            studentName = "Jan Kowalski",
            schoolName = "Szkoła Podstawowa nr 1"
        )
        val me = io.github.szpontium.api.librus.models.LibrusMeResponse(
            me = io.github.szpontium.api.librus.models.LibrusMeData(
                account = io.github.szpontium.api.librus.models.LibrusAccountData(
                    id = 1,
                    userId = 12345,
                    firstName = "Jan",
                    lastName = "Kowalski",
                    email = "jan@example.com"
                ),
                user = io.github.szpontium.api.librus.models.LibrusUserData(
                    id = 1,
                    firstName = "Jan",
                    lastName = "Kowalski"
                )
            )
        )

        val account = io.github.szpontium.api.librus.LibrusMapper.toHebeAccount(synergiaAccount, me)
        assertEquals("Jan", account.pupil.firstName)
        assertEquals("Kowalski", account.pupil.surname)
        assertEquals("Szkoła Podstawowa nr 1", account.unit.name)
        assertEquals("librus", account.unit.restUrl)
    }

    @Test
    fun testLibrusMapperMessages() {
        val lMsg = io.github.szpontium.api.librus.models.api.LibrusMessage(
            id = 999,
            subject = "Ważna wiadomość",
            date = "2026-09-04 12:30:00",
            sender = io.github.szpontium.api.librus.models.api.LibrusIdNameReference(id = 1, name = "Anna Nauczyciel"),
            isRead = false,
            hasAttachment = true,
            content = "Treść wiadomości"
        )
        val uiMessages = io.github.szpontium.api.librus.LibrusMapper.mapMessages(listOf(lMsg))
        assertEquals(1, uiMessages.size)
        val mapped = uiMessages.first()
        assertEquals("999", mapped.id)
        assertEquals("Ważna wiadomość", mapped.title)
        assertEquals("Anna Nauczyciel", mapped.senderOrRecipient)
        assertEquals(true, mapped.isUnread)
        assertEquals(true, mapped.hasAttachments)
        assertEquals("Treść wiadomości", mapped.content)
    }
}
