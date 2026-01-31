package cc.pe3epwithyou.trident.feature.dmlock

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.mixin.GuiAccessor
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.offset
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.popped
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withTridentFont
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.player.Player

object ReplyLock {
    var currentLock: String? = null

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
        fun renderIcon(graphics: GuiGraphics, cameraPlayer: Player) {
            if (!MCCIState.isOnIsland()) return
            if (!Config.Global.replyLock) return
            if (currentLock == null) return
            val gui = Minecraft.getInstance().gui
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

            val font = Minecraft.getInstance().font

            graphics.fill(
                x,
                y,
                x + 9,
                y + 9,
                0x000000 opacity 128
            )

            graphics.drawString(font, lock, x + 1, y, 0xffffff.opaqueColor())
        }
    }


    fun register() {
        ClientSendMessageEvents.ALLOW_CHAT.register allowChat@{ message ->
            if (!MCCIState.isOnIsland()) return@allowChat true
            if (!Config.Global.replyLock) return@allowChat true
            val user = currentLock
            if (user != null) {
                val connection = Minecraft.getInstance().connection ?: return@allowChat true
                connection.sendCommand("msg $user $message")
                return@allowChat false
            }

            return@allowChat true
        }

        ClientSendMessageEvents.MODIFY_COMMAND.register modifyCmd@{ command ->
            if (!MCCIState.isOnIsland()) return@modifyCmd command
            if (!Config.Global.replyLock) return@modifyCmd command

            var modified = command
            if (modified.startsWith("chat ") || modified == "l" || modified == "local") {
                currentLock = null
                Minecraft.getInstance()
                    .setScreen(Minecraft.getInstance().screen) // re-init screen (hacky, but works)
            }

            if (modified.startsWith("r ") || modified.startsWith("reply ")) {
                if (currentLock == null) return@modifyCmd command
                modified = modified.removePrefix("reply ")
                modified = modified.removePrefix("r ")
                modified = "msg $currentLock $modified"
            }

            return@modifyCmd modified
        }
    }

    fun enableLock(user: String, showMessage: Boolean = true) {
        if (!Config.Global.replyLock) return
        val self = Minecraft.getInstance().gameProfile.name
        if (user.equals(self, ignoreCase = true)) {
            val component =
                Component.literal("You can't lock replies with yourself!").withColor(0xfc7dfc)
            Logger.sendMessage(component)
            return
        }

        currentLock = user
        refreshChatScreen()
        if (!showMessage) return
        val component = Component.literal("Enabled Reply Lock for ").withColor(0xfc7dfc)
            .append(Component.literal(user).withColor(0xffffff))
        Logger.sendMessage(component)
    }

    fun disableLock(showMessage: Boolean = true) {
        if (!Config.Global.replyLock) return
        currentLock = null
        refreshChatScreen()

        if (!showMessage) return
        val component = Component.literal("Disabled Reply Lock.").withColor(0xfc7dfc)
        Logger.sendMessage(component)
    }

    /**
     * re-inits chat screen (hacky, but works)
     */
    private fun refreshChatScreen() {
        val client = Minecraft.getInstance()
        val screen = client.screen
        if (screen is ChatScreen) {
            client.setScreen(screen)
        }
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
            val isLocked = currentLock.equals(user?.string, ignoreCase = true)

            val modified = Component.literal("[").withStyle(removed.style)
            if (currentLock != null && isLocked) {
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
            items.forEach { mutable = mutable.append(it) }

            mutable.style = mutable.style
                .withClickEvent(ClickEvent.RunCommand("trident setReplyLock ${user?.string} ${!isLocked}"))
                .withHoverEvent(HoverEvent.ShowText(getTooltip(isLocked)))
        }

        return mutable
    }

    private fun getTooltip(isLocked: Boolean): Component {
        return if (isLocked) {
            Component.literal("Locked. Click to unlock").withStyle(ChatFormatting.RED)
        } else {
            Component.literal("Click here to lock this conversation")
                .withStyle(ChatFormatting.GREEN)
        }
    }

    private fun cleanupOther(components: List<Component>): Component? {
        return components.firstOrNull { it.string.length >= 3 }
    }
}