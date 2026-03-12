package cc.pe3epwithyou.trident.mixin.chat;

import cc.pe3epwithyou.trident.feature.dmlock.ReplyLock;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @WrapMethod(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V")
    public void wrapAddMessage(Component component, MessageSignature messageSignature, GuiMessageTag guiMessageTag, Operation<Void> original) {
        original.call(ReplyLock.INSTANCE.modifyComponent(component), messageSignature, guiMessageTag);
    }
}
