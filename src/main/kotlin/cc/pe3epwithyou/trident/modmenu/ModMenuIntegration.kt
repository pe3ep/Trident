package cc.pe3epwithyou.trident.modmenu

import cc.pe3epwithyou.trident.config.Config
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.minecraft.client.gui.screens.Screen

class ModMenuIntegration : ModMenuApi {

    override fun getModConfigScreenFactory(): ConfigScreenFactory<Screen>? {
        return ConfigScreenFactory(Config.Companion::getScreen)
    }
}