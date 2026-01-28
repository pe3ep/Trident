package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.client.listeners.ChestScreenListener;
import cc.pe3epwithyou.trident.config.Config;
import cc.pe3epwithyou.trident.feature.disguise.Disguise;
import cc.pe3epwithyou.trident.feature.indicators.BlueprintIndicator;
import cc.pe3epwithyou.trident.feature.indicators.CraftableIndicator;
import cc.pe3epwithyou.trident.feature.exchange.ExchangeHandler;
import cc.pe3epwithyou.trident.feature.fishing.TideWindIndicator;
import cc.pe3epwithyou.trident.feature.indicators.UpgradeIndicator;
import cc.pe3epwithyou.trident.feature.rarityslot.RaritySlot;
import cc.pe3epwithyou.trident.interfaces.exchange.ExchangeFilter;
import cc.pe3epwithyou.trident.interfaces.fishing.AugmentStatusInterface;
import cc.pe3epwithyou.trident.state.MCCIState;
import cc.pe3epwithyou.trident.utils.DebugDraw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin extends Screen {
    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "renderSlot", at = @At(value = "HEAD"))
    public void renderSlot(GuiGraphics guiGraphics, Slot slot, int i, int j, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        RaritySlot.INSTANCE.render(guiGraphics, slot);
        TideWindIndicator.INSTANCE.renderOutline(guiGraphics, slot);
    }

    @Inject(method = "renderSlot", at = @At(value = "TAIL"))
    public void renderSlotTail(GuiGraphics guiGraphics, Slot slot, int i, int j, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        if (Config.Global.INSTANCE.getBlueprintIndicators()) {
            BlueprintIndicator.INSTANCE.checkLore(guiGraphics, slot);
        }
        if (Config.Debug.INSTANCE.getDrawSlotNumber()) {
            DebugDraw.INSTANCE.renderSlotNumber(guiGraphics, slot);
        }
        if (Config.Global.INSTANCE.getUpgradeIndicators()) {
            UpgradeIndicator.INSTANCE.render(guiGraphics, slot);
        }
        TideWindIndicator.INSTANCE.render(guiGraphics, slot);
        CraftableIndicator.INSTANCE.render(guiGraphics, slot);
        if (Config.Global.INSTANCE.getExchangeImprovements()) {
            ExchangeHandler.INSTANCE.renderSlot(guiGraphics, slot);
        }
        AugmentStatusInterface.INSTANCE.render(guiGraphics, slot);
    }

    @Inject(method = "onClose", at = @At(value = "HEAD"))
    public void onClose(CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;

        Minecraft client = Minecraft.getInstance();
        if (client.screen instanceof ContainerScreen s) {
            Disguise.checkActionbar();
            if (s.getTitle().getString().contains("FISHING SUPPLIES")) {
                ChestScreenListener.INSTANCE.findAugments(s);
            }
            if (s.getTitle().getString().contains("ISLAND REWARDS")) {
                ChestScreenListener.INSTANCE.findQuests(s);
            }
        }
    }

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    public void renderTooltip(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (!ExchangeHandler.INSTANCE.shouldRenderTooltip(hoveredSlot)) ci.cancel();
        }
    }

    @Inject(method = "renderBackground", at = @At(value = "TAIL"))
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        Minecraft client = Minecraft.getInstance();
        if (client.screen instanceof ContainerScreen s) {
            if (s.getTitle().getString().contains("ISLAND EXCHANGE")) {
                ExchangeHandler.INSTANCE.renderBackground(guiGraphics, leftPos, topPos);
            }
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        if (!Config.Global.INSTANCE.getExchangeImprovements()) return;
        if (this.getTitle().getString().contains("ISLAND EXCHANGE")) {
            int x = this.leftPos + 32;
            int y = this.topPos - 33;
            this.addRenderableWidget(new ExchangeFilter(x, y));
        }
    }

}
