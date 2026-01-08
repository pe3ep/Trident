package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.feature.FocusGame;
import cc.pe3epwithyou.trident.feature.fishing.WayfinderModule;
import cc.pe3epwithyou.trident.feature.questing.QuestListener;
import cc.pe3epwithyou.trident.state.MCCIState;
import cc.pe3epwithyou.trident.utils.ScreenManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "setSubtitleText", at = @At("TAIL"))
    private void subtitleText(ClientboundSetSubtitleTextPacket clientboundSetSubtitleTextPacket, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        FocusGame.INSTANCE.handleSubtitle(clientboundSetSubtitleTextPacket.text().getString());
    }

    @Inject(method = "handleContainerSetSlot", at = @At("TAIL"))
    private void containerSetSlot(ClientboundContainerSetSlotPacket clientboundContainerSetSlotPacket, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        QuestListener.INSTANCE.handleRefreshTasksItem(clientboundContainerSetSlotPacket.getItem());
        ScreenManager.Companion.setWaitingForItems(false);
    }

    @Inject(method = "handleBossUpdate", at = @At("TAIL"))
    private void handleBossUpdate(ClientboundBossEventPacket clientboundBossEventPacket, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        WayfinderModule.INSTANCE.handleBossbarEvent();
    }
}
