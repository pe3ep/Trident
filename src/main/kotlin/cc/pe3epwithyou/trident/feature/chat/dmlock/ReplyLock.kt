package cc.pe3epwithyou.trident.feature.chat.dmlock

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.chat.ChatControllerManager
import cc.pe3epwithyou.trident.mixin.accessors.GuiAccessor
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.offset
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.popped
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withTridentFont
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.player.Player

object ReplyLock {
    fun getReplyLockUser(): String? =
        (ChatControllerManager.getController() as? ReplyLockController)?.user

    object Icon {
        val xpBonusCharCache = mutableSetOf<String>()

        fun containsChar(actionbar: String): Boolean {
            if (xpBonusCharCache.isEmpty()) return false
            for (c in actionbar) {
                if (c.toString() in xpBonusCharCache) return true
            }
            return false
        }

        @JvmStatic
        fun renderIcon(graphics: GuiGraphicsExtractor, cameraPlayer: Player) {
            if (!MCCIState.isOnIsland()) return
            if (!Config.Global.replyLock) return
            if (getReplyLockUser() == null) return
            val gui = minecraft().gui
            val actionBar = (gui as GuiAccessor).overlayMessageString ?: return

            val middle: Int = graphics.guiWidth() / 2
            val hotbarHalf = 91
            val hand = cameraPlayer.mainArm.opposite
            var offset = 0

            if (containsChar(actionBar.string)) {
                offset += 40
            }

            if (hand == HumanoidArm.RIGHT && !cameraPlayer.offhandItem.isEmpty) {
                offset += 29
            }

            val x = middle + hotbarHalf + 1 + offset
            val y = graphics.guiHeight() - 12

            val lock = Component.literal("\uE016").withTridentFont().popped()
                .withoutShadow()

            val font = minecraft().font

            graphics.fill(
                x,
                y,
                x + 9,
                y + 9,
                0x000000 opacity 128
            )

            graphics.text(font, lock, x + 1, y, 0xffffff.opaqueColor())
        }
    }

    fun enableLock(user: String, showMessage: Boolean = true) {
        if (!Config.Global.replyLock) return
        val self = minecraft().gameProfile.name
        if (user.equals(self, ignoreCase = true)) {
            val component =
                Component.literal("You can't lock replies with yourself!").withColor(0xfc7dfc)
            Logger.sendMessage(component)
            return
        }

        ChatControllerManager.setController(ReplyLockController(user))
        if (!showMessage) return
        val component = Component.literal("Enabled Reply Lock for ").withColor(0xfc7dfc)
            .append(Component.literal(user).withColor(0xffffff))
        Logger.sendMessage(component)
    }

    fun disableLock(showMessage: Boolean = true) {
        if (!Config.Global.replyLock) return
        if (ChatControllerManager.getController() is ReplyLockController) {
            ChatControllerManager.clearController()
        }

        if (!showMessage) return
        val component = Component.literal("Disabled Reply Lock.").withColor(0xfc7dfc)
        Logger.sendMessage(component)
    }

    fun modifyComponent(component: Component): Component {
        if (!MCCIState.isOnIsland()) return component
        if (!Config.Global.replyLock) return component

        var mutable = component.copy()
        Regex("""^\[(PM From|PM To)] (.+):""").find(mutable.string)?.let {
            val items = mutable.toFlatList()
            val removed = items.removeFirst()
            val user = cleanupOther(items)

            val type = it.groupValues[1]
            val isLocked = getReplyLockUser().equals(user?.string, ignoreCase = true)

            val modified = Component.literal("[").withStyle(removed.style)
            if (getReplyLockUser() != null && isLocked) {
                modified.append(
                    Component.literal("\uE016").withTridentFont().withStyle(
                        ChatFormatting.WHITE
                    ).popped().offset(y = -1f)
                )
                modified.append(Component.literal(" "))
            }
            modified.append(Component.literal("$type] "))

            items.add(0, modified)
            mutable = Component.empty()
            items.forEach { item -> mutable = mutable.append(item) }

            mutable.style = mutable.style
                .withClickEvent(ClickEvent.RunCommand("replylock ${user?.string}"))
                .withHoverEvent(HoverEvent.ShowText(getTooltip()))
        }

        return mutable
    }

    private fun getTooltip(): Component {
        return FontCollection.get("_fonts/icon/click_action_left.png", 7, 7).withColor(0xffffff)
            .append(Component.literal(" > ").withStyle(ChatFormatting.DARK_GRAY).defaultFont())
            .append(
                Component.literal("Click to ")
                    .withColor(0xe9d282)
                    .defaultFont()
            )
            .append(
                Component.literal("Toggle Reply Lock")
                    .withColor(0xfbe460)
                    .defaultFont()
            )
    }

    private fun cleanupOther(components: List<Component>): Component? {
        return components.firstOrNull { it.string.length >= 3 }
    }
}