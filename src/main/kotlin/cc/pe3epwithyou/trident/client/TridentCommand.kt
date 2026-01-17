package cc.pe3epwithyou.trident.client

import cc.pe3epwithyou.trident.Trident.Companion.playerState
import cc.pe3epwithyou.trident.client.TridentCommand.debugDialogs
import cc.pe3epwithyou.trident.client.listeners.FishingSpotListener
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.api.ApiProvider
import cc.pe3epwithyou.trident.feature.exchange.ExchangeHandler
import cc.pe3epwithyou.trident.feature.fishing.OverclockHandlers
import cc.pe3epwithyou.trident.feature.killfeed.KillMethod
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.debug.StateDialog
import cc.pe3epwithyou.trident.interfaces.experiment.TabbedDialog
import cc.pe3epwithyou.trident.interfaces.fishing.ResearchDialog
import cc.pe3epwithyou.trident.interfaces.fishing.SuppliesDialog
import cc.pe3epwithyou.trident.interfaces.fishing.WayfinderDialog
import cc.pe3epwithyou.trident.interfaces.killfeed.KillFeedDialog
import cc.pe3epwithyou.trident.interfaces.killfeed.widgets.KillWidget
import cc.pe3epwithyou.trident.interfaces.questing.QuestingDialog
import cc.pe3epwithyou.trident.interfaces.updatechecker.DisappointedCatDialog
import cc.pe3epwithyou.trident.state.AugmentContainer
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.state.PlayerState
import cc.pe3epwithyou.trident.state.PlayerStateIO
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.AugmentStatus
import cc.pe3epwithyou.trident.utils.Command
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import cc.pe3epwithyou.trident.utils.extensions.CoroutineScopeExt.main
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.noxcrew.sheeplib.DialogContainer
import com.noxcrew.sheeplib.util.opacity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.util.Util

object TridentCommand {
    private val debugDialogs = mutableMapOf(
        "supplies" to ::SuppliesDialog,
        "questing" to ::QuestingDialog,
        "grumpycat" to ::DisappointedCatDialog,
        "wayfinder" to ::WayfinderDialog
    )

    private fun notOnIsland(): Boolean {
        if (!Config.Debug.developerMode && !MCCIState.isOnIsland()) {
            Logger.sendMessage(
                Component.translatable("trident.not_island").withSwatch(TridentFont.TRIDENT_COLOR)
            )
            return true
        }
        return false
    }

    var jokeCooldown: Boolean = false

    fun registerCommands(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        /**
         * Main /trident command
         */
        Command("trident") {
            /**
             * Manually open a new dialog by key from [debugDialogs]
             */
            literal("open") {
                argument("dialog") {
                    suggests { _, builder ->
                        debugDialogs.keys.forEach(builder::suggest)
                        builder.buildFuture()
                    }
                    executes { context ->
                        if (notOnIsland()) return@executes
                        val key = context.getArgument("dialog", String::class.java)
                        debugDialogs[key]?.let {
                            DialogCollection.open(key, it(10, 10, key))
                        }
                    }
                }
            }

            /**
             * Manually close a dialog by a key found in [debugDialogs]
             */
            literal("close") {
                argument("dialog") {
                    suggests { _, builder ->
                        debugDialogs.keys.forEach(builder::suggest)
                        builder.buildFuture()
                    }
                    executes {
                        if (notOnIsland()) return@executes
                        val key = it.getArgument("dialog", String::class.java)
                        DialogCollection.close(key)
                    }
                }
            }

            /**
             * Resets dialog positions and automatically puts them next to each other
             */
            literal("resetDialogPositions") {
                executes {
                    DialogCollection.resetDialogPositions()
                    val c = Component.literal("Saved dialog positions have been successfully ")
                        .withSwatch(TridentFont.TRIDENT_COLOR).append(
                            Component.literal("reset").withSwatch(TridentFont.ERROR)
                        )
                    Logger.sendMessage(c)
                }
            }

            /**
             * Resets player state with default values. Should only be used when something is completely broken
             */
            literal("resetPlayerState") {
                executes {
                    playerState = PlayerState()
                    PlayerStateIO.load()
                    DialogCollection.refreshOpenedDialogs()

                    val c = Component.literal("Player state has been successfully ")
                        .withSwatch(TridentFont.TRIDENT_COLOR).append(
                            Component.literal("reset").withSwatch(TridentFont.ERROR)
                        )
                    Logger.sendMessage(c)
                }
            }

            /**
             * Joke command :p
             */
            literal("autofish") {
                executes {
                    if (jokeCooldown) return@executes
                    jokeCooldown = true

                    val scope = Util.backgroundExecutor().asCoroutineDispatcher()
                    CoroutineScope(scope).launch {
                        main {
                            Logger.sendMessage("Requesting autofish.jar...")
                        }

                        delay(4000)
                        main {
                            Logger.sendMessage("Received a response from the server")
                        }

                        delay(2000)
                        main {
                            Logger.sendMessage("It says the following:")
                        }

                        delay(3000)
                        main {
                            Logger.sendMessage(
                                Component.literal("Did you really just try to enable autofishing?")
                                    .withStyle(ChatFormatting.AQUA)
                            )
                        }

                        delay(3000)
                        main {
                            Logger.sendMessage(
                                Component.literal("Are we serious right meow bro?")
                                    .withStyle(ChatFormatting.AQUA)
                            )
                        }

                        delay(3000)
                        main {
                            Logger.sendMessage(
                                Component.literal("This incident will be reported.")
                                    .withSwatch(TridentFont.ERROR).withStyle(ChatFormatting.BOLD)
                            )
                            jokeCooldown = false
                        }


                    }
                }

            }

            literal("api") {
                literal("setToken") {
                    argument("token") {
                        suggests { _, builder ->
                            builder.suggest("Enter your API key")
                            builder.buildFuture()
                        }
                        executes {
                            val arg = it.getArgument("token", String::class.java)
                            Config.handler.instance().apiKey = arg
                            Config.handler.instance().globalApiProvider = ApiProvider.SELF_TOKEN
                            Config.handler.save()
                            Logger.sendMessage("Successfully set the token. You can now use API features")
                        }
                    }
                }
                literal("resetToken") {
                    executes {
                        Config.handler.instance().apiKey = ""
                        Config.handler.instance().globalApiProvider = ApiProvider.TRIDENT
                        Config.handler.save()
                        Logger.sendMessage(
                            Component.literal("Your API token has been ")
                                .withSwatch(TridentFont.TRIDENT_COLOR)
                                .append(Component.literal("reset").withSwatch(TridentFont.ERROR))
                        )
                    }
                }
            }

        }.register(dispatcher)

        if (!Config.Debug.developerMode) return

        // Debug dialogs should only be enabled for cool people (devs)
        debugDialogs["research"] = ::ResearchDialog
        debugDialogs["experiment_tabbed"] = ::TabbedDialog
        debugDialogs["killfeed"] = ::KillFeedDialog

        /**
         * Debug commands for trident. Registered only if logging is enabled.
         * Requires game restart.
         */
        Command("trident_debug") {
            literal("fake_overclock") {
                argument("overclock") {
                    suggests { _, builder ->
                        builder.suggest("supreme")
                        builder.suggest("unstable")
                        builder.buildFuture()
                    }
                    executes {
                        val key = it.getArgument("overclock", String::class.java)
                        Logger.sendMessage("Starting fake overclock $key")
                        if (key == "unstable") {
                            playerState.supplies.overclocks.unstable.state.isAvailable = true
                            OverclockHandlers.startTimedOverclock(
                                "Unstable", playerState.supplies.overclocks.unstable.state
                            )
                        }
                        if (key == "supreme") {
                            playerState.supplies.overclocks.supreme.state.isAvailable = true
                            OverclockHandlers.startTimedOverclock(
                                "Supreme", playerState.supplies.overclocks.supreme.state
                            )
                        }
                    }
                }
            }

            literal("dump_playerstate") {
                executes {
                    val json = Json { prettyPrint = true }
                    val serializable = playerState
                    val text = json.encodeToString(serializable)
                    Logger.sendMessage("——————— PLAYERSTATE BEGIN ———————", false)
                    Logger.sendMessage(text, false)
                    Logger.sendMessage("———————— PLAYERSTATE END ————————", false)
                }
            }

            literal("dump_lowest_prices") {
                executes {
                    Logger.sendMessage("—————— LOWEST PRICE BEGIN ——————", false)
                    ExchangeHandler.exchangeDeals.forEach { (key, value) ->
                        Logger.sendMessage("$key costs $value", false)
                    }
                    Logger.sendMessage("——————— LOWEST PRICE END ———————", false)
                }
            }

            literal("dump_islandstate") {
                executes {
                    Logger.sendMessage("—————— ISLAND BEGIN ——————", false)
                    Logger.sendMessage("CURRENT GAME: ${MCCIState.game}")
                    Logger.sendMessage("FISHING STATE: ${MCCIState.fishingState}")
                    Logger.sendMessage("——————— ISLAND END ———————", false)
                }
            }

            literal("send_current_spot") {
                executes {
                    Logger.sendMessage("${FishingSpotListener.currentSpot}")
                }
            }

            literal("fake_augment") {
                argument("augment") {
                    suggests { _, builder ->
                        Augment.entries.forEach { builder.suggest(it.name) }
                        builder.buildFuture()
                    }
                    argument("status") {
                        suggests { _, builder ->
                            AugmentStatus.entries.forEach { builder.suggest(it.name) }
                            builder.buildFuture()
                        }
                        executes {
                            val augmentString = it.getArgument("augment", String::class.java)
                            val statusString = it.getArgument("status", String::class.java)
                            val augment = Augment.valueOf(augmentString)
                            val status = AugmentStatus.valueOf(statusString)
                            playerState.supplies.augmentContainers.add(
                                AugmentContainer(
                                    augment, status
                                )
                            )
                            Logger.sendMessage("Fake augment created: ${augment.name}", false)
                            DialogCollection.refreshOpenedDialogs()
                        }
                    }
                }
            }

            literal("open_state_dialog") {
                executes {
                    DialogContainer += StateDialog(10, 10, "state")
                }
            }

            literal("add_fake_kill") {
                argument("method") {
                    suggests { _, builder ->
                        KillMethod.entries.forEach { builder.suggest(it.name) }
                        builder.buildFuture()
                    }
                    argument("streak", IntegerArgumentType.integer()) {
                        suggests { _, builder ->
                            (1..5).forEach { builder.suggest(it.toString()) }
                            builder.buildFuture()
                        }
                        argument("hasAssist", BoolArgumentType.bool()) {
                            executes {
                                val self = Minecraft.getInstance().gameProfile
                                val method =
                                    KillMethod.valueOf(it.getArgument("method", String::class.java))
                                KillFeedDialog.addKill(
                                    KillWidget(
                                        victim = self.name.toString(),
                                        killMethod = method,
                                        attacker = self.name.toString(),
                                        killColors = Pair(
                                            0x606060 opacity 128,
                                            0x606060 opacity 100
                                        ),
                                        streak = it.getArgument("streak", Int::class.java),
                                        hasAssist = it.getArgument("hasAssist", Boolean::class.java)
                                    )
                                )
                            }
                        }

                    }
                }
            }

            literal("force_load_config") {
                executes {
                    Config.handler.load()
                    Logger.sendMessage("Successfully reloaded config")
                }
            }
        }.register(dispatcher)
    }

}