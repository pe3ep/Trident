package cc.pe3epwithyou.trident.mixin.accessors;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor
    int getImageHeight();

    @Accessor
    int getLeftPos();

    @Accessor
    int getTopPos();

    @Accessor
    Slot getHoveredSlot();
}
