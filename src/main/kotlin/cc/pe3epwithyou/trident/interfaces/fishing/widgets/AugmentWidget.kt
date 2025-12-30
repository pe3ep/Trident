package cc.pe3epwithyou.trident.interfaces.fishing.widgets

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.state.AugmentContainer
import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.state.fishing.AugmentStatus
import cc.pe3epwithyou.trident.utils.*
import com.noxcrew.sheeplib.util.lighten
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class AugmentWidget(
    val textureWidth: Int, textureHeight: Int, val container: AugmentContainer, marginRight: Int
) : AbstractWidget(
    0,
    0,
    textureWidth + marginRight,
    textureHeight + getExtraHeight(),
    Component.empty()
) {
    private companion object {
        val REPAIR_AUGMENT = Resources.trident("textures/interface/repair_augment.png")
        val BROKEN_AUGMENT = Resources.trident("textures/interface/broken_augment.png")
        val PAUSED_AUGMENT = Resources.trident("textures/interface/paused_augment.png")

        val NORMAL_COLOR = 0x93ea2c.opaqueColor()
        val REPAIRED_COLOR = 0xffc900.opaqueColor()
        val BROKEN_COLOR = 0xea2c2c.opaqueColor()

        private const val WARNING_COLOR = 0xfca600
        private const val WARNING_COLOR_ALT = 0xf27500

        fun getExtraHeight() = if (Config.Fishing.suppliesModuleShowAugmentDurability) 8 else 0
    }

    init {
        setupTooltip()
    }

    private fun setupTooltip() {
        if (container.augment == Augment.EMPTY_AUGMENT) return
        val name = Component.literal(container.augment.augmentName + "\n\n").withColor(
            when (container.status) {
                AugmentStatus.NEW -> NORMAL_COLOR
                AugmentStatus.BROKEN -> BROKEN_COLOR
                else -> REPAIRED_COLOR
            }
        )

        val supplyInfo = Component.literal("Supply Item Info\n").withColor(0xFEE761.opaqueColor())

        val uses = Component.literal(" ▪ ").withColor(0x505050.opaqueColor()).append(
            Component.literal("Uses: ").withColor(0xA8B0B0.opaqueColor())
        ).append(
            Component.literal("${container.augment.uses}\n").withColor(0xFFFFFF.opaqueColor())
        )

        val trigger = Component.literal(" ▪ ").withColor(0x505050.opaqueColor()).append(
            Component.literal("Use Condition: ").withColor(0xA8B0B0.opaqueColor())
        ).append(
            Component.literal("${container.augment.useTrigger.lore}\n\n")
                .withColor(0xFFFFFF.opaqueColor())
        )

        val grotto =
            if (container.augment.worksInGrotto) Component.empty() else Component.literal(" ▪ ")
                .withColor(0x505050.opaqueColor()).append(
                    Component.literal("Does not work in Grottos.").withColor(0xA8B0B0.opaqueColor())
                )

        val useProgress = when (container.status) {
            AugmentStatus.BROKEN -> {
                Component.literal("This item is out of uses! You've already repaired it once, so cannot do so again.")
                    .withColor(0xFF5556.opaqueColor())
            }

            AugmentStatus.NEEDS_REPAIRING -> {
                Component.literal("This item is out of uses! You can ").withColor(
                    0xFF5556.opaqueColor()
                ).append(Component.literal("Repair").withColor(0xffffff.opaqueColor())).append(
                    Component.literal(" it to restore all its uses, you can only do it once per item.")
                        .withColor(0xFF5556.opaqueColor())
                )
            }

            else -> {
                val c =
                    Component.literal("Uses Remaining: ").withColor(0xFEE761.opaqueColor()).append(
                        Component.literal("${container.durability}")
                            .withColor(0xffffff.opaqueColor()).append(
                                Component.literal("/${container.augment.uses}\n")
                                    .withColor(0x505050.opaqueColor())
                            ).append(
                                ProgressBar.progressComponent(
                                    container.durability / container.augment.uses.toFloat(), 50, 10
                                )
                            )
                    )
                if (container.status == AugmentStatus.REPAIRED) {
                    c.append(
                        Component.literal("\n\nThis item has previously been repaired.")
                            .withColor(0xA8B0B0.opaqueColor())
                    )
                }
                if (container.status == AugmentStatus.PAUSED) {
                    c.append(
                        Component.literal("\n\nPaused")
                            .withColor(WARNING_COLOR_ALT.opaqueColor())
                            .append(
                                Component.literal(" This item will not consume uses or apply it's effects until unpaused.")
                                    .withColor(WARNING_COLOR.opaqueColor())
                            )
                    )
                }
                c
            }
        }

        val c =
            name.append(supplyInfo).append(uses).append(trigger).append(grotto).append(useProgress)
        setTooltip(Tooltip.create(c))
    }

    // don't look too close, this code stinks
    override fun renderWidget(
        graphics: GuiGraphics, i: Int, j: Int, f: Float
    ) {

        val isSmall = when (container.status) {
            AugmentStatus.NEEDS_REPAIRING -> true
            AugmentStatus.BROKEN -> true
            AugmentStatus.PAUSED -> true
            else -> false
        }

        val padding = if (isSmall) 2 else 0
        val actualSize = if (isSmall) (textureWidth - 4) else textureWidth

        Model(
            container.augment.modelPath,
            actualSize,
            actualSize,
        ).render(
            graphics, x + padding, y + padding
        )

        drawAugmentBar(
            graphics,
            container.status == AugmentStatus.REPAIRED,
            container.durability / container.augment.uses.toFloat()
        )

        if (Config.Fishing.suppliesModuleShowAugmentDurability) renderRemainingUses(graphics)

        when (container.status) {
            AugmentStatus.NEEDS_REPAIRING -> {
                Texture(
                    REPAIR_AUGMENT, 12, 12
                ).blit(graphics, x, y)
            }

            AugmentStatus.BROKEN -> {
                Texture(
                    BROKEN_AUGMENT, 12, 12
                ).blit(graphics, x, y)
            }

            AugmentStatus.PAUSED -> {
                Texture(
                    PAUSED_AUGMENT, 12, 12
                ).blit(graphics, x, y)
            }

            else -> {}
        }
    }

    private fun renderRemainingUses(graphics: GuiGraphics) {
        if (container.augment == Augment.EMPTY_AUGMENT) return
        graphics.pose().pushMatrix()
        val pose = graphics.pose()

        val durability =
            if (container.status == AugmentStatus.BROKEN || container.status == AugmentStatus.NEEDS_REPAIRING) 0 else container.durability
        val factor = 0.65f
        val posX = when {
            durability >= 100 -> x
            durability in 10..99 -> x + 2
            else -> x + 4
        }
        val posY = y + 15
        val percent = durability / container.augment.uses.toFloat()
        val color = when {
            container.status == AugmentStatus.PAUSED -> WARNING_COLOR.opaqueColor()
            durability == 0 -> 0xA8B0B0.opaqueColor()
            percent <= 0.25f -> TridentFont.ERROR.baseColor
            else -> 0xFFFFFF.opaqueColor()
        }
        pose.scaleAround(factor, factor, posX.toFloat(), posY.toFloat(), pose)
        graphics.drawString(
            Minecraft.getInstance().font,
            Component.literal("$durability").withColor(color),
            posX,
            posY,
            0xFFFFFF.opaqueColor()
        )
        graphics.pose().popMatrix()
    }

    private fun drawAugmentBar(graphics: GuiGraphics, isRepaired: Boolean, value: Float) {
        if (value == 1F) return
        val barWidth = textureWidth - 2
        val filledWidth = (barWidth * value).toInt().coerceIn(0, barWidth)
        val color = if (isRepaired) REPAIRED_COLOR else NORMAL_COLOR
        graphics.fill(
            x + 1,
            y + textureWidth - 2,
            x + barWidth + 1,
            y + textureWidth - 1,
            color.lighten(-0.7f)
        )
        graphics.fill(
            x + 1, y + textureWidth - 2, x + filledWidth + 1, y + textureWidth - 1, color
        )
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit
}