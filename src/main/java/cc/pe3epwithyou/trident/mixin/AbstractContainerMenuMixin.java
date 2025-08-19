package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.client.events.SlotClickListener;
import cc.pe3epwithyou.trident.state.MCCIslandState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {

    @Inject(method = "clicked", at = @At("HEAD"))
    public void clicked(int i, int j, ClickType clickType, Player player, CallbackInfo ci) {
        if (!MCCIslandState.INSTANCE.isOnIsland()) return;
//        When user clicks off-screen, the slot is negative. If we don't return here, the game will crash
        if (i < 0) return;
        Minecraft client = Minecraft.getInstance();
        if (client.screen instanceof ContainerScreen screen) {
            Slot slot = screen.getMenu().getSlot(i);
            boolean isLeftClick = j == 0;
            SlotClickListener.INSTANCE.handleClick(slot, clickType, isLeftClick);
        }
    }
}
