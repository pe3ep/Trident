package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.feature.doll.back.CosmeticDollRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements CosmeticDollRenderState {
    @Unique
    public boolean trident$cosmeticDoll = false;

    @Unique
    @Override
    public boolean trident$isCosmeticDoll() {
        return trident$cosmeticDoll;
    }

    @Unique
    @Override
    public void trident$setCosmeticDoll(boolean value) {
        trident$cosmeticDoll = value;
    }
}
