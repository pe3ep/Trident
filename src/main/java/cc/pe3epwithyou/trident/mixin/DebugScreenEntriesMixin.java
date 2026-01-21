package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.Trident;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(DebugScreenEntries.class)
public class DebugScreenEntriesMixin {
    @Shadow
    @Final
    @Mutable
    public static Map<DebugScreenProfile, Map<Identifier, DebugScreenEntryStatus>> PROFILES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void modifyDefaultProfile(CallbackInfo ci) {
        Map<DebugScreenProfile, Map<Identifier, DebugScreenEntryStatus>> newProfiles = new HashMap<>(PROFILES);
        Map<Identifier, DebugScreenEntryStatus> defaultProfile = new HashMap<>(newProfiles.get(DebugScreenProfile.DEFAULT));

        defaultProfile.put(Trident.Companion.getTridentDebugEntry(), DebugScreenEntryStatus.IN_OVERLAY);

        newProfiles.put(DebugScreenProfile.DEFAULT, Map.copyOf(defaultProfile));
        PROFILES = Map.copyOf(newProfiles);
    }

}
