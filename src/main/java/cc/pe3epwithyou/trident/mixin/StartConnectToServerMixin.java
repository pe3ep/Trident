package cc.pe3epwithyou.trident.mixin;

import cc.pe3epwithyou.trident.feature.Introduction;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public abstract class StartConnectToServerMixin {
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
}
