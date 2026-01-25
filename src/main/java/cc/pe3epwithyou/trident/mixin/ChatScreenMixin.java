package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.state.MCCIState;
import cc.pe3epwithyou.trident.utils.ChatDecorations;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    protected ChatScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        this.addRenderableWidget(new ChatDecorations.Widget());
    }
}
