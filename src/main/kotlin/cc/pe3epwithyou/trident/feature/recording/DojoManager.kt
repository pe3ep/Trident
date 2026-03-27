package cc.pe3epwithyou.trident.feature.recording

import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.minecraft
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

object DojoManager {
    fun register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register { component, _ ->
            if (!MCCIState.isOnIsland()) return@register true
            if (MCCIState.game != Game.PARKOUR_WARRIOR_DOJO) return@register true
            Regex("""^\[.] Challenge run started!""").matchEntire(component.string)?.let {
                val courseName = MCCIState.gameTypes.lastOrNull() ?: return@let
                val userName = minecraft().gameProfile.name
                RecordingManager.createAndStart(courseName)
                try {
                    RecordingManager.loadRecording("$userName-$courseName.nbt") { PlaybackManager.startPlaying() }
                } catch (_: Exception) {
                }
            }
            Regex("""^\[.] Run ended!""").matchEntire(component.string)?.let {
                MCCIState.gameTypes.lastOrNull() ?: return@let
                RecordingManager.currentRecording?.stop(true)
                PlaybackManager.stopPlaying()
            }
            true
        }
    }
}