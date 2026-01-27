package cc.pe3epwithyou.trident.config

import cc.pe3epwithyou.trident.config.groups.*
import cc.pe3epwithyou.trident.feature.ChatSwitcherButtons
import cc.pe3epwithyou.trident.feature.api.ApiProvider
import cc.pe3epwithyou.trident.feature.discord.ActivityManager
import cc.pe3epwithyou.trident.feature.killfeed.KillfeedPosition
import cc.pe3epwithyou.trident.feature.rarityslot.DisplayType
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.fishing.WayfinderModuleDisplay
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemes
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.Resources
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.dsl.YetAnotherConfigLib
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class Config {
    @SerialEntry
    var globalCallToHome: Boolean = true

    @Deprecated("This option has been moved to a separate group")
    @SerialEntry
    var globalRarityOverlay: Boolean? = null

    @SerialEntry
    var globalApiProvider: ApiProvider = ApiProvider.TRIDENT

    @SerialEntry
    var globalBlueprintIndicators: Boolean = true

    @SerialEntry
    var globalCraftableIndicators: Boolean = true

    @SerialEntry
    var globalUpgradeIndicators: Boolean = true

    @SerialEntry
    var globalExchangeImprovements: Boolean = true

    @SerialEntry
    var globalChatChannelButtons: Boolean = false

    @SerialEntry
    var globalReplyLock: Boolean = true

    @SerialEntry
    var globalEffectBar: Boolean = true

    @SerialEntry
    var globalCurrentTheme: TridentThemes = TridentThemes.DEFAULT


    @SerialEntry
    var raritySlotEnabled: Boolean = false

    @SerialEntry
    var raritySlotDisplayType: DisplayType = DisplayType.OUTLINE

    @SerialEntry
    var fishingSuppliesModule: Boolean = true

    @SerialEntry
    var fishingSuppliesModuleShowAugmentDurability: Boolean = false

    @SerialEntry
    var fishingShowAugmentStatusInInterface: Boolean = true

    @SerialEntry
    var fishingWayfinderModule: Boolean = true

    @SerialEntry
    var fishingWayfinderModuleDisplay: WayfinderModuleDisplay = WayfinderModuleDisplay.FULL

    @SerialEntry
    var fishingFlashIfDepleted: Boolean = true

    @SerialEntry
    var fishingIslandIndicators: Boolean = true

    @SerialEntry
    var debugEnableLogging: Boolean = false

    @SerialEntry
    var debugDrawSlotNumber: Boolean = false

    @Deprecated("Option is no longer used")
    @SerialEntry
    var debugLogForScrapers: Boolean = false

    @SerialEntry
    var debugBypassOnIsland: Boolean = false

    @SerialEntry
    var gamesAutoFocus: Boolean = false


    @SerialEntry
    var killfeedEnabled: Boolean = true

    @SerialEntry
    var killfeedHideKills: Boolean = false

    @SerialEntry
    var killfeedClearAfterRound: Boolean = true

    @SerialEntry
    var killfeedShowYouInKill: Boolean = true

    @SerialEntry
    var killfeedReverseOrder: Boolean = false

    @SerialEntry
    var killfeedPositionSide: KillfeedPosition = KillfeedPosition.RIGHT

    @SerialEntry
    var killfeedPositionY: Int = 20

    @SerialEntry
    var killfeedRemoveKillTime: Int = 10

    @SerialEntry
    var killfeedMaxKills: Int = 5

    @SerialEntry
    var killfeedShowKillStreaks: Boolean = true

    @SerialEntry
    var questingEnabled: Boolean = true

    @SerialEntry
    var questingRarityColorName: Boolean = true

    @SerialEntry
    var questingShowInLobby: Boolean = true

    @SerialEntry
    var questingShowLeft: Boolean = true

    @SerialEntry
    var questingHideIfNoQuests: Boolean = false


    @SerialEntry
    var discordEnabled: Boolean = true

    @SerialEntry
    var discordPrivateMode: Boolean = false

    @SerialEntry
    var discordDisplayExtraInfo: Boolean = true

    @SerialEntry
    var discordDisplayParty: Boolean = true


    @SerialEntry
    var apiKey: String = ""

    @SerialEntry
    var sawIntroduction: Boolean = false

    object Global {
        val callToHome: Boolean
            get() = handler.instance().globalCallToHome
        val apiProvider: ApiProvider
            get() = handler.instance().globalApiProvider
        val blueprintIndicators: Boolean
            get() = handler.instance().globalBlueprintIndicators
        val chatChannelButtons: Boolean
            get() = handler.instance().globalChatChannelButtons
        val currentTheme: TridentThemes
            get() = handler.instance().globalCurrentTheme
        val craftableIndicators: Boolean
            get() = handler.instance().globalCraftableIndicators
        val upgradeIndicators: Boolean
            get() = handler.instance().globalUpgradeIndicators
        val replyLock: Boolean
            get() = handler.instance().globalReplyLock
        val exchangeImprovements: Boolean
            get() = handler.instance().globalExchangeImprovements
        val effectBar: Boolean
            get() = handler.instance().globalEffectBar
    }

    object RaritySlot {
        val enabled: Boolean
            get() = handler.instance().raritySlotEnabled
        val displayType: DisplayType
            get() = handler.instance().raritySlotDisplayType
    }

    object Debug {
        val developerMode: Boolean
            get() = handler.instance().debugEnableLogging
        val drawSlotNumber: Boolean
            get() = handler.instance().debugDrawSlotNumber
        val bypassOnIsland: Boolean
            get() = handler.instance().debugBypassOnIsland
    }

    object Fishing {
        val suppliesModule: Boolean
            get() = handler.instance().fishingSuppliesModule
        val suppliesModuleShowAugmentDurability: Boolean
            get() = handler.instance().fishingSuppliesModuleShowAugmentDurability
        val showAugmentStatusInInterface: Boolean
            get() = handler.instance().fishingShowAugmentStatusInInterface
        val flashIfDepleted: Boolean
            get() = handler.instance().fishingFlashIfDepleted
        val islandIndicators: Boolean
            get() = handler.instance().fishingIslandIndicators
        val wayfinderModule: Boolean
            get() = handler.instance().fishingWayfinderModule
        val wayfinderModuleDisplay: WayfinderModuleDisplay
            get() = handler.instance().fishingWayfinderModuleDisplay
    }

    object Games {
        val autoFocus: Boolean
            get() = handler.instance().gamesAutoFocus
    }

    object KillFeed {
        val enabled: Boolean
            get() = handler.instance().killfeedEnabled
        val hideKills: Boolean
            get() = handler.instance().killfeedHideKills
        val clearAfterRound: Boolean
            get() = handler.instance().killfeedClearAfterRound
        val showYouInKill: Boolean
            get() = handler.instance().killfeedShowYouInKill
        val reverseOrder: Boolean
            get() = handler.instance().killfeedReverseOrder
        val positionSide: KillfeedPosition
            get() = handler.instance().killfeedPositionSide
        val positionY: Int
            get() = handler.instance().killfeedPositionY
        val removeKillTime: Int
            get() = handler.instance().killfeedRemoveKillTime
        val maxKills: Int
            get() = handler.instance().killfeedMaxKills
        val showKillstreaks: Boolean
            get() = handler.instance().killfeedShowKillStreaks
    }

    object Questing {
        val enabled: Boolean
            get() = handler.instance().questingEnabled
        val rarityColorName: Boolean
            get() = handler.instance().questingRarityColorName
        val showInLobby: Boolean
            get() = handler.instance().questingShowInLobby
        val showLeft: Boolean
            get() = handler.instance().questingShowLeft
        val hideIfNoQuests: Boolean
            get() = handler.instance().questingHideIfNoQuests
    }

    object Discord {
        val enabled: Boolean
            get() = handler.instance().discordEnabled
        val privateMode: Boolean
            get() = handler.instance().discordPrivateMode
        val displayExtraInfo: Boolean
            get() = handler.instance().discordDisplayExtraInfo
        val displayParty: Boolean
            get() = handler.instance().discordDisplayParty
    }

    object Api {
        val key: String
            get() = handler.instance().apiKey
    }

    companion object {
        val handler: ConfigClassHandler<Config> by lazy {
            ConfigClassHandler.createBuilder(Config::class.java).id(Resources.trident("config"))
                .serializer { config ->
                    GsonConfigSerializerBuilder.create(config)
                        .setPath(FabricLoader.getInstance().configDir.resolve("trident.json"))
                        .build()
                }.build()
        }

        @Suppress("DEPRECATION")
        fun convertDeprecated() {
            val rarityOverlayPrev = handler.instance().globalRarityOverlay
            if (rarityOverlayPrev != null) {
                Logger.warn("Detected a deprecated config value for rarity overlay, converting it")

                handler.instance().raritySlotEnabled =
                    rarityOverlayPrev /* Reset the old value to null */
                handler.instance().globalRarityOverlay = null
            }

            // Check Island Utils compatibility
            if (handler.instance().globalChatChannelButtons && !ChatSwitcherButtons.checkCompatibility()) {
                handler.instance().globalChatChannelButtons = false
            }

            handler.save()
        }

        fun init() {
            handler.load()
        }

        fun getScreen(parentScreen: Screen?): Screen = YetAnotherConfigLib("trident") {
            title(Component.translatable("config.trident"))
            save {
                handler.save()
                DialogCollection.refreshOpenedDialogs()
                ActivityManager.updateCurrentActivity()
            }

            generalCategory(categories)
            discordCategory(categories)
            indicatorsCategory(categories)
            killfeedCategory(categories)
            questingCategory(categories)
            fishingCategory(categories)
            debugCategory(categories)


        }.generateScreen(parentScreen)
    }
}