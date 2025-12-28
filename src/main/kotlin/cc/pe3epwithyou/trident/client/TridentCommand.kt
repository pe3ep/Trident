package cc.pe3epwithyou.trident.client

import cc.pe3epwithyou.trident.client.TridentClient.Companion.playerState
import cc.pe3epwithyou.trident.client.TridentCommand.debugDialogs
import cc.pe3epwithyou.trident.client.listeners.FishingSpotListener
import cc.pe3epwithyou.trident.feature.api.ApiProvider
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.exchange.ExchangeHandler
import cc.pe3epwithyou.trident.feature.exchange.ExchangeLookup
import cc.pe3epwithyou.trident.feature.fishing.OverclockHandlers
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.experiment.TabbedDialog
import cc.pe3epwithyou.trident.interfaces.fishing.ResearchDialog
import cc.pe3epwithyou.trident.interfaces.fishing.SuppliesDialog
import cc.pe3epwithyou.trident.interfaces.fishing.WayfinderDialog
import cc.pe3epwithyou.trident.interfaces.questing.QuestingDialog
import cc.pe3epwithyou.trident.interfaces.updatechecker.DisappointedCatDialog
import cc.pe3epwithyou.trident.state.AugmentContainer
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.state.PlayerState
import cc.pe3epwithyou.trident.state.PlayerStateIO
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.AugmentStatus
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.Command
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import cc.pe3epwithyou.trident.utils.extensions.CoroutineScopeExt.main
import com.mojang.brigadier.CommandDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.ChatFormatting
import net.minecraft.Util
import net.minecraft.network.chat.Component

object TridentCommand {
    private val debugDialogs = mutableMapOf(
        "supplies" to ::SuppliesDialog,
        "questing" to ::QuestingDialog,
        "grumpycat" to ::DisappointedCatDialog,
    )

    private fun notOnIsland(): Boolean {
        if (!Config.Debug.enableLogging && !MCCIState.isOnIsland()) {
            ChatUtils.sendMessage(
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
                    val c = Component.literal("Saved dialog positions have been successfully ")
                        .withSwatch(TridentFont.TRIDENT_COLOR).append(
                            Component.literal("reset").withSwatch(TridentFont.ERROR)
                        )
                    ChatUtils.sendMessage(c)
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

                    val c =
                        Component.literal("Player state has been successfully ")
                            .withSwatch(TridentFont.TRIDENT_COLOR)
                            .append(
                                Component.literal("reset").withSwatch(TridentFont.ERROR)
                            )
                    ChatUtils.sendMessage(c)
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
                            ChatUtils.sendMessage("Requesting autofish.jar...")
                        }

                        delay(4000)
                        main {
                            ChatUtils.sendMessage("Received a response from the server")
                        }

                        delay(2000)
                        main {
                            ChatUtils.sendMessage("It says the following:")
                        }

                        delay(3000)
                        main {
                            ChatUtils.sendMessage(
                                Component.literal("Did you really just try to enable autofishing?")
                                    .withStyle(ChatFormatting.AQUA)
                            )
                        }

                        delay(3000)
                        main {
                            ChatUtils.sendMessage(
                                Component.literal("Are we serious right meow bro?")
                                    .withStyle(ChatFormatting.AQUA)
                            )
                        }

                        delay(3000)
                        main {
                            ChatUtils.sendMessage(
                                Component.literal("This incident will be reported.")
                                    .withSwatch(TridentFont.ERROR)
                                    .withStyle(ChatFormatting.BOLD)
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
                            ChatUtils.sendMessage("Successfully set the token. You can now use API features")
                        }
                    }
                }
                literal("resetToken") {
                    executes {
                        Config.handler.instance().apiKey = ""
                        Config.handler.instance().globalApiProvider = ApiProvider.TRIDENT
                        Config.handler.save()
                        ChatUtils.sendMessage(
                            Component.literal("Your API token has been ")
                                .withSwatch(TridentFont.TRIDENT_COLOR)
                                .append(Component.literal("reset").withSwatch(TridentFont.ERROR))
                        )
                    }
                }
            }

        }.register(dispatcher)

        if (!Config.Debug.enableLogging) return

        // Debug dialogs, should only be enabled for cool people (devs)
        debugDialogs["research"] = ::ResearchDialog
        debugDialogs["wayfinder"] = ::WayfinderDialog
        debugDialogs["experiment_tabbed"] = ::TabbedDialog

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
                        ChatUtils.sendMessage("Starting fake overclock $key")
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
                    ChatUtils.sendMessage("——————— PLAYERSTATE BEGIN ———————", false)
                    ChatUtils.sendMessage(text, false)
                    ChatUtils.sendMessage("———————— PLAYERSTATE END ————————", false)
                }
            }

            literal("dump_lowest_prices") {
                executes {
                    ChatUtils.sendMessage("—————— LOWEST PRICE BEGIN ——————", false)
                    ExchangeHandler.exchangeDeals.forEach { (key, value) ->
                        ChatUtils.sendMessage("$key costs $value", false)
                    }
                    ChatUtils.sendMessage("——————— LOWEST PRICE END ———————", false)
                }
            }

            literal("dump_islandstate") {
                executes {
                    ChatUtils.sendMessage("—————— ISLAND BEGIN ——————", false)
                    ChatUtils.sendMessage("CURRENT GAME: ${MCCIState.game}")
                    ChatUtils.sendMessage("——————— ISLAND END ———————", false)
                }
            }

            literal("send_exchange_req") {
                executes {
                    ExchangeLookup.lookup()
                }
            }

            literal("send_current_spot") {
                executes {
                    ChatUtils.sendMessage("${FishingSpotListener.currentSpot}")
                }
            }

            literal("fake_augment") {
                argument("augment") {
                    suggests { _, builder ->
                        Augment.entries.forEach { builder.suggest(it.name) }
                        builder.buildFuture()
                    }
                    executes {
                        val augmentString = it.getArgument("augment", String::class.java)
                        val augment = Augment.valueOf(augmentString)
                        playerState.supplies.augments.add(
                            AugmentContainer(
                                augment,
                                AugmentStatus.PAUSED
                            )
                        )
                        ChatUtils.sendMessage("Fake augment created: ${augment.name}", false)
                        DialogCollection.refreshOpenedDialogs()
                    }
                }
            }
        }.register(dispatcher)
    }

}