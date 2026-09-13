package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.state.FontCollection;
import net.minecraft.client.gui.font.providers.BitmapProvider;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BitmapProvider.Definition.class)
public class FontLoaderMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void trident$init(Identifier file, int height, int ascent, int[][] codepointGrid, CallbackInfo ci) {
        try {
            if (!FontCollection.INSTANCE.isGameRunning()) return;

            String namespace = file.getNamespace();
            String path = file.getPath();
            if (!namespace.equals("mcc") || !path.startsWith("_fonts/")) return;
            if (codepointGrid.length == 0 || codepointGrid[0].length == 0) return;
            int[] c = codepointGrid[0];
            StringBuilder builder = new StringBuilder();
            for (int point : c) {
                builder.appendCodePoint(point);
            }

            String character = builder.toString();
            FontCollection.INSTANCE.loadDefinition(file, character, ascent, height);
        } catch (Throwable ignored) {}
    }
}