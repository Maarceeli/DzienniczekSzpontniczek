package io.github.szpontium.api.prometheus.models

enum class MailboxRole {
    TEACHER,
    PARENT,
    STUDENT
}

data class VulcanMailboxName(
    val name: String,
    val code: String?,
    val studentName: String?,
    val role: MailboxRole,
    val shortSchoolName: String
) {
    companion object {
        fun parse(input: String): VulcanMailboxName? {
            val regex = Regex(
                """\s*(.*?)\s*(?:\[([^]]+)])?\s*-\s*([OPRU])\s*-\s*(.+\s*-\s*)?\((.+)\)"""
            )

            val match = regex.find(input) ?: return null
            val groups = match.groupValues

            val name = groups[1].trim()
            val code = groups[2].trim().takeIf { it.isNotEmpty() }
            val roleLetter = groups[3]
            val studentNameRaw = groups.getOrNull(4)?.trim()?.removeSuffix("-")?.trim()?.takeIf { it.isNotEmpty() }
            val shortSchoolName = groups[5].trim()

            return VulcanMailboxName(
                name = name,
                code = code,
                studentName = studentNameRaw,
                role = when (roleLetter) {
                    "P" -> MailboxRole.TEACHER
                    "O", "R" -> MailboxRole.PARENT
                    "U" -> MailboxRole.STUDENT
                    else -> return null
                },
                shortSchoolName = shortSchoolName
            )
        }
    }
}
