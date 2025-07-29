package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.OverclockTexture
import cc.pe3epwithyou.trident.state.fishing.getAugmentByName
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag

object ItemParser {
    fun getLore(item: ItemStack): List<Component> {
        if (Minecraft.getInstance().player == null) return listOf(Component.empty())
        return item.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.Default.NORMAL)
    }

    fun getActiveOverclock(item: ItemStack): Augment? {
        var beginSearch = false
        getLore(item).forEach { line ->
//            Due to people having different upgrade costs, it's easier to simply go over each line and start
//            searching overclocks once we reach this text
            if ("Overclocked Perk:" in line.string) {
                beginSearch = true
            }
            if (beginSearch && line.string == "") {
                return null
            }
            if (beginSearch && ">" in line.string) {
                val name = line.string.split("> ")[1]
                val active = getAugmentByName(name)
                return active
            }
        }
        return null
    }

    fun getUnstableOverclock(item: ItemStack): OverclockTexture? {
        var beginSearch = false
        getLore(item).forEach { line ->
//            Get overclock duration, as it can be different depending on the upgrade
            if ("Duration: " in line.string) {
                val minutes = line.string.split(": ")[1]
                if (minutes.split(" ").size == 1) {
                    val minuteInt = minutes.dropLast(1).toInt()
                    ChatUtils.sendMessage("minutes: $minutes, minutesInt: $minuteInt")
                    TridentClient.playerState.supplies.overclocks.unstable.duration = minuteInt * 60L * 20L
                } else {
                    val minuteInt = minutes.split(" ")[1].dropLast(1).toInt()
                    ChatUtils.sendMessage("minutes: $minutes, minutesInt: $minuteInt")
                    TridentClient.playerState.supplies.overclocks.unstable.duration = minuteInt * 60L * 20L
                }
            }
            if ("Overclocked Perk:" in line.string) {
                beginSearch = true
            }
            if (beginSearch && ">" in line.string) {
                // this is horrible but it works
                val perk = line.string.split("> ")[1].split(" ")[1]
                when {
                    perk == "Elusive" -> return OverclockTexture.STRONG_UNSTABLE
                    perk == "Wayfinder" -> return OverclockTexture.WISE_UNSTABLE
                    perk == "Pearl" -> return OverclockTexture.GLIMMERING_UNSTABLE
                    perk == "Treasure" -> return OverclockTexture.GREEDY_UNSTABLE
                    perk == "Spirit" -> return OverclockTexture.LUCKY_UNSTABLE
                    else -> return null
                }
            }
        }
        return null
    }
}