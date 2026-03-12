package cc.pe3epwithyou.trident.events.container

import cc.pe3epwithyou.trident.events.StopExecution
import cc.pe3epwithyou.trident.state.MCCIState
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

object ContainerEvents {
    /**
     * Registers a listener triggered when a container opens.
     * Waits a few ticks for items to arrive before being called
     *
     * @param block a [ContainerContext] lambda
     */
    fun onOpen(block: ContainerContext.() -> Unit) = OPEN.registerWithContext(block)

    /**
     * Registers a listener triggered when a container is closed.
     *
     * @param block a [ContainerContext] lambda
     */
    fun onClose(block: ContainerContext.() -> Unit) = CLOSE.registerWithContext(block)

    /**
     * Registers a listener triggered when a container screen is initialized.
     *
     * @param block a [ContainerContext] lambda
     */
    fun onInit(block: ContainerContext.() -> Unit) = INIT.registerWithContext(block)

    val OPEN: Event<ContainerEventCallback> = EventFactory.createArrayBacked(ContainerEventCallback::class.java) { listeners ->
        ContainerEventCallback { ctx -> listeners.forEach { it.invoke(ctx) } }
    }

    val CLOSE: Event<ContainerEventCallback> = EventFactory.createArrayBacked(ContainerEventCallback::class.java) { listeners ->
        ContainerEventCallback { ctx -> listeners.forEach { it.invoke(ctx) } }
    }

    val INIT: Event<ContainerEventCallback> = EventFactory.createArrayBacked(ContainerEventCallback::class.java) { listeners ->
        ContainerEventCallback { ctx -> listeners.forEach { it.invoke(ctx) } }
    }

    fun interface ContainerEventCallback { fun invoke(ctx: ContainerContext) }

    private fun Event<ContainerEventCallback>.registerWithContext(block: ContainerContext.() -> Unit) {
        register { ctx ->
            try {
                if (!MCCIState.isOnIsland()) return@register
                ctx.block()
            } catch (_: StopExecution) {}
        }
    }
}

