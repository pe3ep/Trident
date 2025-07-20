package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.MCCIslandState
import com.noxcrew.noxesium.feature.skull.SkullContents
import com.noxcrew.noxesium.network.NoxesiumPackets
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.MutableComponent
import java.util.*

object NoxesiumUtils {
    fun skullComponent(uuid: UUID, grayscle: Boolean = false, advance: Int = 0, ascent: Int = 0, scale: Float = 1.0F): MutableComponent {
        return MutableComponent.create(SkullContents(Optional.of(uuid), Optional.empty(), grayscle, advance, ascent, scale))
    }

    fun registerListeners() {
        if (FabricLoader.getInstance().isModLoaded("noxesium")) {
            NoxesiumPackets.CLIENT_MCC_SERVER.addListener(this) { _, packet, _ ->
                val server = packet.serverType
                val type = packet.subType
                val game = packet.associatedGame

                val currentMCCGame = getCurrentGame(server, type, game)
                if (currentMCCGame != MCCIslandState.game) {
                    MCCIslandState.game = currentMCCGame
                    if (Config.Debug.enableLogging) {
                        ChatUtils.sendMessage("Current game: ${MCCIslandState.game.title}")
                    }
                }
            }
        }
    }

    private fun getCurrentGame(server: String, type: String, game: String): MCCGame {
        if (server == MCCGame.HUB.server) {
            when {
                type.contains("temperate") -> return MCCGame.FISHING
                type.contains("tropical") -> return MCCGame.FISHING
                type.contains("barren") -> return MCCGame.FISHING
            }
            return MCCGame.HUB
        }
        if (game == "parkour_warrior") {
//            Check if it's survivor, otherwise it's dojo
            if (type == MCCGame.PARKOUR_WARRIOR_SURVIVOR.subtype) return MCCGame.PARKOUR_WARRIOR_SURVIVOR
            return MCCGame.PARKOUR_WARRIOR_DOJO
        }

//        The rest of the games don't need any special checks, we can just match the server with the game
        MCCGame.entries.forEach { mccGame ->
//            Ignore PKW and Hub, as we already checked them
            if (mccGame == MCCGame.HUB) return@forEach
            if (mccGame == MCCGame.FISHING) return@forEach
            if (mccGame == MCCGame.PARKOUR_WARRIOR_DOJO) return@forEach
            if (mccGame == MCCGame.PARKOUR_WARRIOR_SURVIVOR) return@forEach

            if (mccGame.server == game) {
                return mccGame
            }
        }

//        Fallback to hub if game isn't found
        return MCCGame.HUB
    }

}

