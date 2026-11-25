package cc.pe3epwithyou.trident.feature.dojo

import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.background
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.popped
import cc.pe3epwithyou.trident.utils.main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Component
import kotlin.time.Duration.Companion.milliseconds

object DojoListener {
    fun register() {
        ClientReceiveMessageEvents.MODIFY_GAME.register { component, isActionBar ->
            if (!MCCIState.isOnIsland()) return@register component
            if (MCCIState.game != Game.PARKOUR_WARRIOR_DOJO) return@register component
            if (isActionBar) return@register component

            Regex("""\[.] Challenge run started!""").matchEntire(component.string)?.let {
                DojoInterface.onRunStarted()
            }

            Regex("""\[.] (Run ended!|You restarted the course!)""").matchEntire(component.string)?.let {
                DojoInterface.onRunEnded()
            }

            Regex("""\[.] You've been teleported back to your last intersection.""").matchEntire(component.string)?.let {
                DojoInterface.onCourseLeft()
            }

            return@register component
        }
    }

    @JvmStatic
    fun modifySubtitle(component: Component): Component {
        if (MCCIState.game != Game.PARKOUR_WARRIOR_DOJO) return component

        if (component.string.startsWith("+1")) {
            val elapsed = DojoInterface.onCourseCompleted() ?: return component


            val modified = component.copy().append(
                Component.literal(" (${DojoStorage.formattedTime(elapsed)})").defaultFont().popped()
            )

            // Leave the current course if user doesn't enter another one in 2.5s
            background().launch {
                delay(2500.milliseconds)
                val course = DojoInterface.getCurrentCourseState()
                if (course == null) {
                    main {
                        DojoInterface.onCourseLeft()
                    }
                }
            }

            return modified
        }

        Regex("""\[(\w\d-\d)] """).find(component.string)?.let { matchResult ->
            val courseId = matchResult.groupValues[1].takeIf { it.isNotBlank() } ?: return component
            DojoInterface.onCourseStarted(courseId)
        }

        return component
    }

    // TODO: Save the last split, send breakdown message in chat
    @JvmStatic
    fun checkTitle(component: Component) {
        if (MCCIState.game != Game.PARKOUR_WARRIOR_DOJO) return
        if (component.string == "Run Complete!") {
            DojoInterface.onRunEnded()
        }
    }
}