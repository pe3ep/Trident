package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.feature.dmlock.ReplyLock;
import cc.pe3epwithyou.trident.feature.statusbar.EffectBar;
import cc.pe3epwithyou.trident.state.MCCIState;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    @Nullable
    protected abstract Player getCameraPlayer();

    @Inject(method = "renderItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/HumanoidArm;getOpposite()Lnet/minecraft/world/entity/HumanoidArm;", shift = At.Shift.AFTER))
    public void renderLock(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        if (getCameraPlayer() == null) return;
        ReplyLock.Icon.renderIcon(guiGraphics, getCameraPlayer());
        EffectBar.render(guiGraphics);
    }
}
