package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.state.FontCollection;
import net.minecraft.client.gui.font.providers.BitmapProvider;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BitmapProvider.Definition.class)
public class FontLoaderMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(ResourceLocation resourceLocation, int height, int ascent, int[][] chars, CallbackInfo ci) {
        String namespace = resourceLocation.getNamespace();
        String path = resourceLocation.getPath();
        if (!namespace.equals("mcc") || !path.startsWith("_fonts/")) return;
        int[] c = chars[0];
        StringBuilder builder = new StringBuilder();
        for (int point : c) {
            builder.appendCodePoint(point);
        }

        String character = builder.toString();
        FontCollection.INSTANCE.loadDefinition(resourceLocation, character, ascent, height);
    }
}
