package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.feature.dojo.DojoInterface;
import cc.pe3epwithyou.trident.feature.dojo.hud.DojoHudStateMachine;
import cc.pe3epwithyou.trident.feature.dojo.hud.states.VanillaState;
import cc.pe3epwithyou.trident.state.Game;
import cc.pe3epwithyou.trident.state.MCCIState;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {

    @Shadow
    @Final
    private Map<UUID, LerpingBossEvent> events;

    @WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/LerpingBossEvent;getName()Lnet/minecraft/network/chat/Component;"))
    public Component trident$editOriginalUI(LerpingBossEvent instance, Operation<Component> original) {
        if (!MCCIState.INSTANCE.isOnIsland()) return original.call(instance);
        if (MCCIState.INSTANCE.getGame() != Game.PARKOUR_WARRIOR_DOJO) return original.call(instance);

        if (this.events.values().iterator().next() == instance) {
            instance.setName(Component.empty()); // Remove original UI
        }

        return original.call(instance);
    }

    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"))
    public void trident$renderReplacementUI(GuiGraphicsExtractor graphics, CallbackInfo ci, @Local(name = "yOffset") LocalIntRef yOffset, @Local(name = "event") LerpingBossEvent event) {
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        if (MCCIState.INSTANCE.getGame() != Game.PARKOUR_WARRIOR_DOJO) return;

        if (this.events.values().iterator().next() == event) {
            int height = DojoInterface.extractRenderState(graphics, yOffset.get()).getSecond();
            yOffset.set(yOffset.get() + height - VanillaState.HEIGHT);
        }
    }
}
