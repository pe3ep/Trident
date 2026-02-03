package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.feature.Introduction;
import cc.pe3epwithyou.trident.feature.crafting.CraftingNotifications;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public abstract class OnlineServerEntryMixin {
    @Shadow
    public abstract ServerData getServerData();

    @WrapMethod(method = "join")
    private void joining(Operation<Void> original) {
        try {
            ServerData serverData = getServerData();
            if (!serverData.ip.toLowerCase().contains("mccisland.net")) {
                original.call();
                return;
            }
            Introduction.INSTANCE.displayIntroductionIfNeeded(original);
        } catch (Exception e) {
            original.call();
        }
    }

    @Inject(method = "renderContent", at = @At("HEAD"))
    void injectRenderContent(GuiGraphics guiGraphics, int i, int j, boolean bl, float f, CallbackInfo ci) {
        ServerSelectionList.OnlineServerEntry thisEntry = (ServerSelectionList.OnlineServerEntry)(Object) this;
        CraftingNotifications.renderServerListIndicator(guiGraphics, i, j, thisEntry.getContentX(), thisEntry.getContentY(), getServerData());
    }
}
