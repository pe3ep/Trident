package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.feature.dmlock.ReplyLock;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChatComponent.class)
public class ChatListenerMixin {
    @WrapMethod(method = "addMessage(Lnet/minecraft/network/chat/Component;)V")
    public void wrapHandleDisguisedChatMessage(Component component, Operation<Void> original) {
        original.call(ReplyLock.INSTANCE.modifyComponent(component));
    }
}
