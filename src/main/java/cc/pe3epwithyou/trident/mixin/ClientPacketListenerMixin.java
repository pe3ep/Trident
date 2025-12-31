package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.feature.FocusGame;
import cc.pe3epwithyou.trident.feature.questing.QuestListener;
import cc.pe3epwithyou.trident.utils.Logger;
import cc.pe3epwithyou.trident.utils.ScreenManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "setSubtitleText", at = @At("TAIL"))
    private void subtitleText(ClientboundSetSubtitleTextPacket clientboundSetSubtitleTextPacket, CallbackInfo ci) {
        FocusGame.INSTANCE.handleSubtitle(clientboundSetSubtitleTextPacket.text().getString());
    }

    @Inject(method = "handlePlayerCombatKill", at = @At("TAIL"))
    private void playerCombatKill(ClientboundPlayerCombatKillPacket clientboundPlayerCombatKillPacket, CallbackInfo ci) {
        Logger.INSTANCE.debugLog("Received death event for id: " + clientboundPlayerCombatKillPacket.playerId());
    }

    @Inject(method = "handleContainerSetSlot", at = @At("TAIL"))
    private void containerSetSlot(ClientboundContainerSetSlotPacket clientboundContainerSetSlotPacket, CallbackInfo ci) {
        QuestListener.INSTANCE.handleRefreshTasksItem(clientboundContainerSetSlotPacket.getItem());
        ScreenManager.Companion.setWaitingForItems(false);
    }
}
