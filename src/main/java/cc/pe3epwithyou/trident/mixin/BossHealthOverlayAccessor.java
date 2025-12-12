package cc.pe3epwithyou.trident.mixin;

import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(BossHealthOverlay.class)
@SuppressWarnings("unused")
public interface BossHealthOverlayAccessor {
    @Accessor
    @Final
    Map<UUID, LerpingBossEvent> getEvents();
}
