package cc.pe3epwithyou.trident.mixin.connection;

import cc.pe3epwithyou.trident.feature.FocusGame;
import cc.pe3epwithyou.trident.feature.dojo.DojoListener;
import cc.pe3epwithyou.trident.feature.fishing.WayfinderModule;
import cc.pe3epwithyou.trident.feature.questing.QuestListener;
import cc.pe3epwithyou.trident.state.MCCIState;
import cc.pe3epwithyou.trident.utils.ScreenManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "setSubtitleText", at = @At("TAIL"))
    private void trident$setSubtitleText(ClientboundSetSubtitleTextPacket packet, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        Component component = packet.text();
        FocusGame.INSTANCE.handleSubtitle(component.getString());
    }

    @Inject(method = "setTitleText", at = @At("TAIL"))
    private void trident$setTitleText(ClientboundSetTitleTextPacket packet, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        DojoListener.checkTitle(packet.text());
    }

    @WrapOperation(method = "setSubtitleText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;setSubtitle(Lnet/minecraft/network/chat/Component;)V"))
    private void trident$modifySubtitleText(Hud instance, Component subtitle, Operation<Void> original) {
        if (!MCCIState.INSTANCE.isOnIsland()) {
            original.call(instance, subtitle);
            return;
        }

        Component modified = DojoListener.modifySubtitle(subtitle);
        original.call(instance, modified);
    }

    @Inject(method = "handleContainerSetSlot", at = @At("TAIL"))
    private void trident$handleContainerSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        QuestListener.INSTANCE.handleRefreshTasksItem(packet.getItem());
        ScreenManager.setWaiting(false);
    }

    @Inject(method = "handleBossUpdate", at = @At("TAIL"))
    private void trident$handleBossUpdate(ClientboundBossEventPacket packet, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        WayfinderModule.INSTANCE.handleBossbarEvent();
    }
}
