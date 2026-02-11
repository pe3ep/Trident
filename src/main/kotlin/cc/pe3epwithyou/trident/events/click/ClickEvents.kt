package cc.pe3epwithyou.trident.events.click

import cc.pe3epwithyou.trident.events.StopExecution
import cc.pe3epwithyou.trident.state.MCCIState
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

object ClickEvents {
    val CLICK: Event<ClickEventCallback> = EventFactory.createArrayBacked(ClickEventCallback::class.java) { listeners ->
        ClickEventCallback { ctx -> listeners.forEach { it.invoke(ctx) } }
    }

    fun onClick(block: ContainerClickContext.() -> Unit) = CLICK.registerWithContext(block)

    fun interface ClickEventCallback { fun invoke(ctx: ContainerClickContext) }

    private fun Event<ClickEventCallback>.registerWithContext(block: ContainerClickContext.() -> Unit) {
        register { ctx ->
            try {
                if (!MCCIState.isOnIsland()) return@register
                ctx.block()
            } catch (_: StopExecution) {}
        }
    }
}