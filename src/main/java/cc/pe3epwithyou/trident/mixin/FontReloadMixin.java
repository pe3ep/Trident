package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.state.FontCollection;
import cc.pe3epwithyou.trident.utils.Logger;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(Minecraft.class)
public class FontReloadMixin {
    @Inject(method = "reloadResourcePacks()Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private void trident$reloadResourcePacks(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        try {
            FontCollection.INSTANCE.clear();
            Logger.INSTANCE.info("Clearing font collection");
        } catch (Throwable ignored) {}
    }
}
