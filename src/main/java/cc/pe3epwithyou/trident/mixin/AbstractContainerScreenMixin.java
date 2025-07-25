package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.client.events.ChestScreenListener;
import cc.pe3epwithyou.trident.config.Config;
import cc.pe3epwithyou.trident.feature.RaritySlot;
import cc.pe3epwithyou.trident.state.MCCIslandState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin extends Screen {
    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "renderSlot", at = @At(value = "HEAD",
            target = "Lnet/minecraft/client/gui/GuiGraphics;renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"
    ))
    public void renderSlot(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci)
    {
        if (Config.Global.INSTANCE.getRarityOverlay() && MCCIslandState.INSTANCE.isOnIsland()) {
            RaritySlot.INSTANCE.render(guiGraphics, slot);
        }
    }

    @Inject(method = "onClose", at = @At(value = "HEAD"))
    public void onClose(CallbackInfo ci)
    {
        if (!MCCIslandState.INSTANCE.isOnIsland()) return;

        Minecraft client = Minecraft.getInstance();
        if (client.screen instanceof ContainerScreen s) {
            if (s.getTitle().getString().contains("FISHING SUPPLIES")) {
                ChestScreenListener.INSTANCE.findAugments(s);
            }
        }
    }

}
