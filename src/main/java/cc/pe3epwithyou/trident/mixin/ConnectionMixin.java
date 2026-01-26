package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.client.PacketHandler;
import cc.pe3epwithyou.trident.feature.discord.ActivityManager;
import cc.pe3epwithyou.trident.interfaces.DialogCollection;
import cc.pe3epwithyou.trident.state.PlayerStateIO;
import cc.pe3epwithyou.trident.utils.DelayedAction;
import cc.pe3epwithyou.trident.utils.Logger;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
    @Shadow
    public abstract SocketAddress getRemoteAddress();

    @Shadow
    private volatile @Nullable PacketListener packetListener;

    @Inject(method = "disconnect(Lnet/minecraft/network/chat/Component;)V", at = @At("TAIL"))
    private void onDisconnect(Component component, CallbackInfo ci) {
        SocketAddress remoteAddress = getRemoteAddress();
        if (remoteAddress == null) return;
        if (remoteAddress instanceof InetSocketAddress inetSocketAddress) {
            String ip = inetSocketAddress.getAddress().getHostName();
            if (ip == null) return;
            Minecraft minecraft = Minecraft.getInstance();
            if (ip.contains("mccisland.net") && packetListener == minecraft.getConnection()) {
                minecraft.execute(() -> {
                    DelayedAction.INSTANCE.closeAllPendingTasks();
                    DialogCollection.INSTANCE.saveAllDialogs();
                    PlayerStateIO.INSTANCE.save();
                    ActivityManager.INSTANCE.hideActivity();
                    Logger.INSTANCE.info("Disconnected from MCC Island at IP: " + ip);
                });
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V", cancellable = true)
    public void channelRead(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        PacketHandler.handle(packet, ci);
    }
}
