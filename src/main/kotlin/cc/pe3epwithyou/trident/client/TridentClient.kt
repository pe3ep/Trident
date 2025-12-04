package cc.pe3epwithyou.trident.client

import cc.pe3epwithyou.trident.client.events.FishingSpotEvents
import cc.pe3epwithyou.trident.client.events.QuestingEvents
import cc.pe3epwithyou.trident.client.listeners.ChatEventListener
import cc.pe3epwithyou.trident.client.listeners.ChestScreenListener
import cc.pe3epwithyou.trident.client.listeners.FishingSpotListener
import cc.pe3epwithyou.trident.client.listeners.KillChatListener
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.fishing.DepletedDisplay
import cc.pe3epwithyou.trident.feature.fishing.OverclockClock
import cc.pe3epwithyou.trident.feature.questing.QuestListener
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.state.PlayerState
import cc.pe3epwithyou.trident.state.PlayerStateIO
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.Resources
import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW

class TridentClient : ClientModInitializer {
    companion object {
        var playerState = PlayerState()
        var hasFailedToLoadConfig: Boolean = false
        lateinit var settingsKeymapping: KeyMapping
    }

    override fun onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            TridentCommand.registerCommands(dispatcher)
        }

        settingsKeymapping = KeyBindingHelper.registerKeyBinding(
            KeyMapping(
                "key.trident.config", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_U, KeyMapping.Category.register(
                    Resources.trident("keys"))
            )
        )

        /* Convert deprecated config entries to their new counterpart */
        Config.convertDeprecated()

        ChatEventListener.register()
        ChestScreenListener.register()
        KillChatListener.register()
        DelayedAction.init()
        QuestListener.register()
        OverclockClock.register()

//        Register keybinding
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: Minecraft ->
            if (!MCCIState.isOnIsland()) return@EndTick
            if (!settingsKeymapping.consumeClick() || client.player == null) return@EndTick
            client.setScreen(Config.getScreen(client.screen))
        })

        ClientTickEvents.END_CLIENT_TICK.register {
            if (!MCCIState.isOnIsland()) return@register
            FishingSpotListener.handle()
        }

//        Register Questing events
        QuestingEvents.INCREMENT_ACTIVE.register {
            QuestStorage.applyIncrement(it)
        }

        try {
            DialogCollection.loadAllDialogs()
            playerState = PlayerStateIO.load()
        } catch (e: Exception) {
            hasFailedToLoadConfig = true
            ChatUtils.error("FATAL ERROR OCCURRED WHEN LOADING CONFIGS")
            ChatUtils.error(e.message ?: "No error message")
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register { onShutdownClient() }

        FishingSpotEvents.CAST.register {
            if (Config.Debug.enableLogging) {
                ChatUtils.sendMessage("Cast into spot $it")
            }
        }
    }

    private fun onShutdownClient() {
        try {
            if (!hasFailedToLoadConfig) PlayerStateIO.save()
        } catch (e: Exception) {
            ChatUtils.error("Failed to save data on shutdown: ${e.message}")
        }
    }
}
