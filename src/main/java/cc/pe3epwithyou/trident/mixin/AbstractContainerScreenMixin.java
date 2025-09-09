package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.client.listeners.ChestScreenListener;
import cc.pe3epwithyou.trident.config.Config;
import cc.pe3epwithyou.trident.feature.BlueprintIndicators;
import cc.pe3epwithyou.trident.feature.CraftableIndicator;
import cc.pe3epwithyou.trident.feature.fishing.TideWindIndicator;
import cc.pe3epwithyou.trident.feature.rarityslot.RaritySlot;
import cc.pe3epwithyou.trident.state.MCCIState;
import cc.pe3epwithyou.trident.utils.DebugDraw;
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

    @Inject(method = "renderSlot", at = @At(value = "HEAD"))
    public void renderSlot(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        RaritySlot.INSTANCE.render(guiGraphics, slot);
    }

    @Inject(method = "renderSlot", at = @At(value = "TAIL"))
    public void renderSlotTail(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        if (Config.Global.INSTANCE.getBlueprintIndicators()) {
            BlueprintIndicators.INSTANCE.checkLore(guiGraphics, slot);
        }
        if (Config.Debug.INSTANCE.getDrawSlotNumber()) {
            DebugDraw.INSTANCE.renderSlotNumber(guiGraphics, slot);
        }
        TideWindIndicator.INSTANCE.render(guiGraphics, slot);
        CraftableIndicator.INSTANCE.render(guiGraphics, slot);
    }

    @Inject(method = "onClose", at = @At(value = "HEAD"))
    public void onClose(CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;

        Minecraft client = Minecraft.getInstance();
        if (client.screen instanceof ContainerScreen s) {
            if (s.getTitle().getString().contains("FISHING SUPPLIES")) {
                ChestScreenListener.INSTANCE.findAugments(s);
            }
            if (s.getTitle().getString().contains("ISLAND REWARDS")) {
                ChestScreenListener.INSTANCE.findQuests(s);
            }
        }
    }

}
