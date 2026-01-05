package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.state.MCCIState;
import cc.pe3epwithyou.trident.utils.OffsetFormatter;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.network.chat.Style;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin is a copy of Noxesium's TextOffsetMixin.
 * This was included because Trident needed to support custom offsets for text rendering.
 * <a href="https://github.com/Noxcrew/noxesium/blob/e9837bdb19239c25eb8d97cceba6aee4cdf98401/fabric/src/main/java/com/noxcrew/noxesium/core/fabric/mixin/feature/TextOffsetMixin.java">Link to source code</a>
 * </br>
 * </br>
 * Mixes in to fonts and reads an optional x, y rendering offset from the
 * style's insertion value.
 */
@Mixin(Font.PreparedTextBuilder.class)
public class OffsetMixin {

    @Shadow
    float x;

    @WrapOperation(
            method = "accept(ILnet/minecraft/network/chat/Style;Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;)Z",
            at =
            @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/Font$PreparedTextBuilder;x:F",
                    opcode = Opcodes.GETFIELD))
    public float redirectGetX(
            Font.PreparedTextBuilder instance, Operation<Float> original, @Local(argsOnly = true) Style style) {
        if (!MCCIState.INSTANCE.isOnIsland()) return original.call(instance);
        var offset = OffsetFormatter.INSTANCE.parseX(style.getInsertion());
        if (offset != null) {
            return original.call(instance) + offset;
        }
        return original.call(instance);
    }

    @Inject(
            method = "accept(ILnet/minecraft/network/chat/Style;Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;)Z",
            at = @At("TAIL"))
    public void fixXValue(int ignoredX, Style style, BakedGlyph glyph, CallbackInfoReturnable<Boolean> cir) {
        // The last line is this.x += advance which calls the getX() redirect which adds the offset,
        // so we need to reduce it by the offset to compensate.
        if (!MCCIState.INSTANCE.isOnIsland()) return;
        var offset = OffsetFormatter.INSTANCE.parseX(style.getInsertion());
        if (offset != null) {
            this.x -= offset;
        }
    }

    @WrapOperation(
            method = "accept(ILnet/minecraft/network/chat/Style;Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;)Z",
            at =
            @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/Font$PreparedTextBuilder;y:F",
                    opcode = Opcodes.GETFIELD))
    public float redirectGetY(
            Font.PreparedTextBuilder instance, Operation<Float> original, @Local(argsOnly = true) Style style) {
        if (!MCCIState.INSTANCE.isOnIsland()) return original.call(instance);
        var offset = OffsetFormatter.INSTANCE.parseY(style.getInsertion());
        if (offset != null) {
            return original.call(instance) + offset;
        }
        return original.call(instance);
    }
}