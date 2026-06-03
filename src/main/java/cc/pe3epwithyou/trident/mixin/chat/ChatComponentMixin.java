package cc.pe3epwithyou.trident.mixin.chat;

import cc.pe3epwithyou.trident.feature.chat.dmlock.ReplyLock;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @WrapMethod(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V")
    public void wrapAddMessage(Component contents, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, Operation<Void> original) {
        original.call(ReplyLock.INSTANCE.modifyComponent(contents), signature, source, tag);
    }
}
