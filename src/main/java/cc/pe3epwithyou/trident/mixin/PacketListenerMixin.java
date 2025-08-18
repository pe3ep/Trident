package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.client.events.questing.QuestListener;
import cc.pe3epwithyou.trident.feature.FocusGame;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class PacketListenerMixin {
    @Inject(method = "setSubtitleText", at = @At("HEAD"))
    private void subtitleText(ClientboundSetSubtitleTextPacket clientboundSetSubtitleTextPacket, CallbackInfo ci) {
        FocusGame.INSTANCE.handleSubtitle(clientboundSetSubtitleTextPacket.text().getString());
        QuestListener.INSTANCE.handleSubtitle(clientboundSetSubtitleTextPacket.text());
    }
}
