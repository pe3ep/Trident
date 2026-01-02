package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.feature.DebugScreen;
import cc.pe3epwithyou.trident.utils.Resources;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    private Collection<String> addDebugMessage(Map<Identifier, Collection<String>> instance, Operation<Collection<String>> original) {

        if (this.minecraft.debugEntries.isOverlayVisible()) {
            if (minecraft.player == null) return List.of();
            String message = DebugScreen.INSTANCE.getMessage();

            Optional<ModContainer> container = FabricLoader.getInstance().getModContainer("trident");
            Version currentVersion = null;

            if (container.isPresent()) {
                currentVersion = container.get().getMetadata().getVersion();
            }


            if (currentVersion != null) {
                instance.computeIfAbsent(Resources.INSTANCE.trident("debug"), idx -> new ArrayList<>()).add(String.format("%s[Trident]%s %s", ChatFormatting.AQUA, ChatFormatting.RESET, currentVersion));
            }
            instance.computeIfAbsent(Resources.INSTANCE.trident("debug"), idx -> new ArrayList<>()).add(String.format("%s[Trident]%s %s", ChatFormatting.AQUA, ChatFormatting.RESET, message));
        }
        return original.call(instance);
    }
}
