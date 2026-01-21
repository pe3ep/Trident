package cc.pe3epwithyou.trident.feature.debug

import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Resources
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer
import net.minecraft.client.gui.components.debug.DebugScreenEntry
import net.minecraft.resources.Identifier
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.LevelChunk

class TridentDebugEntry : DebugScreenEntry {
    private companion object {
        val GROUP: Identifier = Resources.trident("debug_group")
    }

    override fun isAllowed(reducedDebugInfo: Boolean): Boolean = true

    override fun display(
        displayer: DebugScreenDisplayer,
        serverOrClientLevel: Level?,
        clientChunk: LevelChunk?,
        serverChunk: LevelChunk?
    ) {
        if (!MCCIState.isOnIsland()) return
        val container = FabricLoader.getInstance().getModContainer("trident")
        val currentVersion = container.get().metadata.version

        val versionString = String.format(
            "%s[Trident]%s %s",
            ChatFormatting.AQUA,
            ChatFormatting.RESET,
            currentVersion
        )
        val messageString = String.format(
            "%s[Trident]%s %s",
            ChatFormatting.AQUA,
            ChatFormatting.RESET,
            DebugScreen.getMessage()
        )

        displayer.addToGroup(GROUP, listOf(versionString, messageString))
    }
}