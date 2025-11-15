package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.modrinth.UpdateChecker;
import cc.pe3epwithyou.trident.interfaces.DialogCollection;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.protocol.login.ClientboundLoginFinishedPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHandshakePacketListenerImpl.class)
public class JoinIslandMixin {
    @Shadow @Final private @Nullable ServerData serverData;

    @Inject(method = "handleLoginFinished", at = @At("HEAD"))
    private void loginFinished(ClientboundLoginFinishedPacket clientboundLoginFinishedPacket, CallbackInfo ci) {
//        Close all dialogs before joining the server
        DialogCollection.INSTANCE.clear();
        if (this.serverData == null) return;
        if (!this.serverData.ip.toLowerCase().contains("mccisland.net")) return;
        UpdateChecker.INSTANCE.checkForUpdates();
    }
}
