package cc.pe3epwithyou.trident.feature.crafting

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.state.PlayerStateIO
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import cc.pe3epwithyou.trident.utils.useScreen
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import java.time.Instant

object CraftingNotifications {
    enum class Source {
        ASSEMBLER, FUSION;

        companion object {
            fun from(screen: ContainerScreen) = when {
                "BLUEPRINT ASSEMBLER" in screen.title.string -> ASSEMBLER
                "FUSION FORGE" in screen.title.string -> FUSION
                else -> throw IllegalStateException("Unknown screen: ${screen.title.string}")
            }
        }
    }

    @Serializable
    data class Notification(val source: Source, val itemName: String, val rarity: Rarity, val hours: Int, val minutes: Int, var count: Int = 1, var endTime: Long? = null, var isFinished: Boolean = false) {
        fun start() {
            if (isFinished) return

            val now = Instant.now().toEpochMilli()
            endTime = now.plus(hours.toLong() * 60 * 60 * 1000 + minutes.toLong() * 60 * 1000)
            Logger.debugLog("Started $source notification for ${itemName}, ends in ${hours}h ${minutes}m")
        }

        fun check() {
            if (isFinished) return

            val now = Instant.now().toEpochMilli()
            endTime?.let { end ->
                if (now >= end) {
                    isFinished = true
                    send(this)
                }
            }
        }
    }

    fun add(notifications: List<Notification>, source: Source) {
        notifications.forEach { it.start() }
        when (source) {
            Source.ASSEMBLER -> Trident.playerState.craftingNotifications.assembler = notifications
            Source.FUSION -> Trident.playerState.craftingNotifications.fusion = notifications
        }
        PlayerStateIO.save()
    }

    private fun fromItem(item: ItemStack, source: Source): Notification? {
        item.getLore().find { it.string.endsWith(" remaining") }?.let {
            Regex("""(?:(\d+)h )?(\d+)m|< 1m""").find(it.string)?.let { matchResult ->
                val rarity = Rarity.getFromItem(item) ?: Rarity.COMMON
                val itemName = item.hoverName.string
                if (matchResult.value == "< 1m") return Notification(source,
                    itemName, rarity, hours = 0, minutes = 1, count = item.count)
                val hours = matchResult.groups[1]?.value?.toIntOrNull() ?: 0
                val minutes = matchResult.groups[2]?.value?.toIntOrNull() ?: 0
                return Notification(source, itemName, rarity, hours, minutes + 1, count = item.count)
            }
        }
        return null
    }

    fun send(notification: Notification) {
        if (!Config.Global.craftingNotifications) return
        val nameComponent = Component.literal(notification.itemName).withColor(notification.rarity.color)

        val msg = Component.empty().append(
            nameComponent
        ).append(
            Component.literal(" has finished crafting ").withSwatch(
                TridentFont.TRIDENT_ACCENT
            )
        )

        Minecraft.getInstance().toastManager.addToast(CraftingToast(notification))

        Logger.sendMessage(msg)
    }

    @JvmStatic
    fun handleScreen(screen: ContainerScreen) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.craftingNotifications) return
        if (!listOf("BLUEPRINT ASSEMBLER", "FUSION FORGE").any { it in screen.title.string }) return

        useScreen(screen) {
            val items = mutableListOf<Notification>()
            val source = Source.from(screen)
            (19..25).forEach {
                val item = getItem(it)
                val notification = fromItem(item, source) ?: return@forEach
                items.add(notification)
            }
            add(items, source)
        }
    }
}
