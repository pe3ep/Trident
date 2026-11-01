package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.feature.statusbar.EffectBar;
import cc.pe3epwithyou.trident.state.MCCIState;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class GuiMixin {
    @Shadow
    @Nullable
    protected abstract Player getCameraPlayer();

    @Inject(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/HumanoidArm;getOpposite()Lnet/minecraft/world/entity/HumanoidArm;", shift = At.Shift.AFTER))
    public void trident$extractItemHotbar(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        if (getCameraPlayer() == null) return;
        EffectBar.render(graphics);
    }
}
