package cc.pe3epwithyou.trident.client

import cc.pe3epwithyou.trident.feature.friends.FriendsInServer
import cc.pe3epwithyou.trident.utils.SuggestionPacket
import cc.pe3epwithyou.trident.utils.minecraft
import net.minecraft.network.protocol.Packet
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object PacketHandler {
    @JvmStatic
    fun handle(packet: Packet<*>, ci: CallbackInfo) {
        minecraft().execute {
            FriendsInServer.processTabPacket(packet)
            SuggestionPacket.handlePacket(packet, ci)
        }
    }
}