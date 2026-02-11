package cc.pe3epwithyou.trident.test

import cc.pe3epwithyou.trident.events.click.ClickEvents
import cc.pe3epwithyou.trident.events.container.ContainerEvents
import cc.pe3epwithyou.trident.utils.Logger

fun registerTestEvents() {
    ContainerEvents.onOpen {
        titleHas("NAVIGATOR")
        val fishing = item(49) ?: return@onOpen
        Logger.sendMessage("feeshing item: ${fishing.hoverName.string}")
    }

    ClickEvents.onClick {
        titleHas("NAVIGATOR")
        Logger.sendMessage("clicked, doubleClick: ${doubleClick()} key: ${key()} left: ${left()}, right: ${right()}, shift: ${shift()}, ctrl: ${ctrl()}, alt: ${alt()}")
    }
}