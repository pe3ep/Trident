package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.feature.crosshair.CrosshairHUD;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "renderCrosshair", at = @At("HEAD"))
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Options options = minecraft.options;
        if (!options.getCameraType().isFirstPerson()) return;
        if (minecraft.gameMode != null && minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) return;
        CrosshairHUD.INSTANCE.render(guiGraphics);
    }
}
