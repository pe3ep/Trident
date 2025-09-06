package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.feature.questing.QuestListener;
import cc.pe3epwithyou.trident.state.FontCollection;
import cc.pe3epwithyou.trident.state.PlayerStateIO;
import cc.pe3epwithyou.trident.utils.ChatUtils;
import cc.pe3epwithyou.trident.utils.DelayedAction;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Inject(method = "disconnect(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"))
    private void onDisconnect(Component component, CallbackInfo ci) {
        ChatUtils.INSTANCE.info("Disconnected from a server, cancelling all pending tasks");
        QuestListener.INSTANCE.interruptTasks();
        DelayedAction.INSTANCE.closeAllPendingTasks();
    }
}
