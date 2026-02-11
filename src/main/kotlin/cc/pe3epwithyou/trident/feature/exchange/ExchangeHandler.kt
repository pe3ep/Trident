package cc.pe3epwithyou.trident.feature.exchange

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.events.click.ClickEvents
import cc.pe3epwithyou.trident.events.container.ContainerContext
import cc.pe3epwithyou.trident.events.container.ContainerEvents
import cc.pe3epwithyou.trident.interfaces.exchange.ExchangeFilter
import cc.pe3epwithyou.trident.utils.Model
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.findInLore
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import java.time.Instant

object ExchangeHandler {
    data class Listing(
        val name: String,
        val amount: Int,
    )

    enum class FetchProgress {
        NO_DATA, LOADING, COMPLETED, FAILED;

        fun isLoading() = this == NO_DATA || this == LOADING
    }

    var fetchingProgress: FetchProgress = FetchProgress.NO_DATA
    val exchangeDeals = hashMapOf<Listing, Long>()

    val ownedCosmetics = mutableSetOf<String>()

    fun handleScreen(ctx: ContainerContext) = with(ctx) {
        requireTitle("ISLAND EXCHANGE")
        if (!Config.Global.exchangeImprovements) return@with

        val now = Instant.now().toEpochMilli()

        if (ExchangeLookup.exchangeLookupCacheExpiresIn != null && now >= ExchangeLookup.exchangeLookupCacheExpiresIn!!) {
            ExchangeLookup.clearCache()
        }

        if (ExchangeLookup.exchangeLookupCache == null) {
            fetchingProgress = FetchProgress.LOADING
            ExchangeLookup.lookup()
        } else {
            updatePrices()
        }

        handledScreen.menu.slots.forEach { slot ->
            if (!inSlotBoundary(slot)) return@forEach
            val item = slot.item

            val price = getItemPrice(item) ?: return@forEach

            val itemName = item.displayName.string.replace(" Token", "")
            val listing = Listing(itemName, slot.item.count)
            val current = exchangeDeals[listing]
            if (current == null || price < current) {
                exchangeDeals[listing] = price
            }
        }
    }

    fun register() {
        ContainerEvents.onOpen(::handleScreen)
        ClickEvents.onClick {
            requireTitle("ISLAND EXCHANGE")
            val clickedItem = clickedItem() ?: return@onClick
            val itemName = clickedItem.displayName.string
            if (itemName.contains("Refresh Listings", ignoreCase = true) && left) {
                clickedItem.findInLore(Regex("Click to Refresh"))?.let {
                    ExchangeLookup.clearCache()
                    handleScreen(this)
                }
            }
        }
    }


    fun updatePrices() {
        ExchangeLookup.exchangeLookupCache!!.data.activeIslandExchangeListings.forEach { (cost, asset, amount) ->
            val name = "[${asset.name}]".replace(" Token", "")
            val listing = Listing(name, amount)
            val current = exchangeDeals[listing]
            if (current == null || cost < current) {
                exchangeDeals[listing] = cost
            }
        }
    }

    fun updateCosmetics() {
        val collections = ExchangeLookup.exchangeLookupCache!!.data.player.collections ?: return
        val filtered = collections.cosmetics.filter { (owned, _) -> owned }
        ownedCosmetics.clear()
        filtered.forEach { (_, cosmetic) ->
            ownedCosmetics.add("[${cosmetic.name}]")
        }
    }

    fun shouldRenderTooltip(slot: Slot): Boolean {
        if (!Config.Global.exchangeImprovements) return true
        val screen = minecraft().screen ?: return true
        if ("ISLAND EXCHANGE" !in screen.title.string) return true
        if (!inSlotBoundary(slot)) return true
        if (fetchingProgress.isLoading()) return true
        if (ExchangeFilter.showOwnedItems) return true

        val itemName = slot.item.displayName.string.replace(" Token", "")
        return !ownedCosmetics.contains(itemName)
    }

    fun renderSlot(graphics: GuiGraphics, slot: Slot) {
        if (!Config.Global.exchangeImprovements) return
        val screen = minecraft().screen ?: return
        if ("ISLAND EXCHANGE" !in screen.title.string) return
        if (!inSlotBoundary(slot)) return
        if (fetchingProgress.isLoading()) return
        if (fetchingProgress == FetchProgress.FAILED) return

        val itemName = slot.item.displayName.string.replace(" Token", "")
        if (ownedCosmetics.contains(itemName) && !ExchangeFilter.showOwnedItems) {
            graphics.fill(
                slot.x, slot.y, slot.x + 16, slot.y + 16, 0x325591 opacity 128
            )
            return
        }

        val price = getItemPrice(slot.item) ?: return
        val listing = Listing(itemName, slot.item.count)
        if (exchangeDeals.contains(listing) && exchangeDeals[listing] == price) {
            Texture(
                Resources.trident("textures/interface/exchange/star.png"),
                width = 7,
                height = 6,
            ).blit(
                graphics, x = slot.x + 10, y = slot.y - 1
            )
        }
    }

    fun renderBackground(graphics: GuiGraphics, left: Int, top: Int) {
        if (!Config.Global.exchangeImprovements) return
        if (!fetchingProgress.isLoading()) return
        Model(
            modelPath = Resources.trident("interface/loading"), width = 8, height = 8
        ).render(
            graphics,
            left + 160,
            top - 30,
        )
    }

    private fun inSlotBoundary(slot: Slot): Boolean {
        val index = slot.index
        return !(index !in 10..16 && index !in 19..25 && index !in 28..34 && index !in 37..43 && index !in 46..52)
    }

    private fun getItemPrice(item: ItemStack): Long? {
        val priceLines = item.getLore().reversed()
        if (priceLines.isEmpty()) return null
        priceLines.forEach { priceLine ->
            val match = Regex("""Listed Price: .((?:\d+|,)+)""").matchEntire(priceLine.string)
                ?: return@forEach
            val price = match.groups[1]?.value?.replace(",", "")?.toLongOrNull() ?: return null
            return price
        }
        return null
    }
}