package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.client.events.KillChatListener;
import cc.pe3epwithyou.trident.feature.FocusGame;
import cc.pe3epwithyou.trident.state.MCCGame;
import cc.pe3epwithyou.trident.state.MCCIslandState;
import cc.pe3epwithyou.trident.utils.ChatUtils;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class PacketListenerMixin {
    @Inject(method = "setSubtitleText", at = @At("HEAD"))
    private void titleText(ClientboundSetSubtitleTextPacket clientboundSetSubtitleTextPacket, CallbackInfo ci) {
        FocusGame.INSTANCE.handleSubtitle(clientboundSetSubtitleTextPacket.text().getString());
    }
}
