package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.OverclockTexture
import cc.pe3epwithyou.trident.state.fishing.getAugmentByName
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import net.minecraft.world.item.ItemStack

object ItemParser {
    fun getActiveOverclock(item: ItemStack): Augment? {
        var beginSearch = false
        item.getLore().forEach { line ->
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
        item.getLore().forEach { line ->
//            Get overclock duration, as it can be different depending on the upgrade
            if ("Duration: " in line.string) {
                val minutes = line.string.split(": ")[1]
                if (minutes.split(" ").size == 1) {
                    val minuteInt = minutes.dropLast(1).toInt()
                    Trident.playerState.supplies.overclocks.unstable.state.duration =
                        minuteInt * 60L
                } else {
                    val minuteInt = minutes.split(" ")[1].dropLast(1).toInt()
                    Trident.playerState.supplies.overclocks.unstable.state.duration =
                        minuteInt * 60L
                }
            }
            if ("Overclocked Perk:" in line.string) {
                beginSearch = true
            }
            if (beginSearch && ">" in line.string) {
                // this is horrible but it works
                val perk = line.string.split("> ")[1].split(" ")[1]
                return when (perk) {
                    "Elusive" -> OverclockTexture.STRONG_UNSTABLE
                    "Wayfinder" -> OverclockTexture.WISE_UNSTABLE
                    "Pearl" -> OverclockTexture.GLIMMERING_UNSTABLE
                    "Treasure" -> OverclockTexture.GREEDY_UNSTABLE
                    "Spirit" -> OverclockTexture.LUCKY_UNSTABLE
                    else -> null
                }
            }
        }
        return null
    }
}