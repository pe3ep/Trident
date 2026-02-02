package cc.pe3epwithyou.trident.mixin.accessors;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(InventoryScreen.class)
@SuppressWarnings("unused")
public interface InventoryScreenAccessor {
    @Invoker("extractRenderState")
    static EntityRenderState trident$extractRenderState(LivingEntity livingEntity) {
        throw new AssertionError();
    }
}
