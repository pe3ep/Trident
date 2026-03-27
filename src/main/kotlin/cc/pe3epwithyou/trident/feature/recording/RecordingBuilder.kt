package cc.pe3epwithyou.trident.feature.recording

import java.util.UUID

class RecordingBuilder {
    var uuid: UUID? = null
    var name: String? = null
    var courseTitle: String? = null
    var isPrivate: Boolean = false
    var skin: String? = null

    fun build(): Recording {
        val id = requireNotNull(uuid) { "UUID is required" }
        val courseName = requireNotNull(courseTitle) { "Course name is required" }
        val userName = requireNotNull(name) { "Course name is required" }
        val skin = requireNotNull(skin) { "Skin is required" }
        require(skin.isNotBlank()) { "Skin cannot be blank" }
        require(userName.isNotBlank()) { "Name cannot be blank" }
        require(courseName.isNotBlank()) { "Course Name cannot be blank" }
        val recording = Recording()
        recording.apply {
            ownerUUID = id
            ownerUsername = userName
            this.courseName = courseName
            ownerSkin = skin
            isAnonymous = isPrivate
        }
        return recording
    }
}

fun buildRecording(block: RecordingBuilder.() -> Unit): Recording = RecordingBuilder().apply(block).build()