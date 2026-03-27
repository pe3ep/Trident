package cc.pe3epwithyou.trident.feature.recording

import cc.pe3epwithyou.trident.utils.minecraft

class Run(
    val course: String
) {
    val id: String
        get() = minecraft().gameProfile.name + "-" + course

    var liveRecording: Recording? = null
    var ghostRecording: Recording? = null

    fun stop() {
        liveRecording?.stop(true)
        ghostRecording?.stop()
    }

    fun start() {
        RecordingManager.loadRecording(id) {
            ghostRecording = it
        }
        RecordingManager.createAndStart(course)?.let {
            liveRecording = it
        }
    }
}