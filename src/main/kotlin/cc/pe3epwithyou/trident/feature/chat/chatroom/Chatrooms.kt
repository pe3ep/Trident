package cc.pe3epwithyou.trident.feature.chat.chatroom

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.events.click.ClickEvents
import cc.pe3epwithyou.trident.events.container.withContainerCtx
import cc.pe3epwithyou.trident.feature.chat.ChatControllerManager
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.*
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import kotlinx.serialization.Serializable
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.inventory.Slot
import java.util.function.Consumer

object Chatrooms {
    fun getActiveChatroom(): Chatroom? =
        (ChatControllerManager.getController() as? ChatroomController)?.chatroom

    fun register() {
        fun unpinRemoved(id: String) {
            if ((ChatControllerManager.getController() as? ChatroomController)?.chatroom?.id?.equals(id, true) ?: false) {
                disableLock(false)
            }
            playerState().activeChatrooms.removeIf { chatroom -> chatroom.id.equals(id, true) }
        }

        ClientReceiveMessageEvents.ALLOW_GAME.register { component, _ ->
            Regex("""You have left the '(.+)' chat room.""").find(component.string)?.let { match ->
                match.groupValues.getOrNull(1)?.let {
                    unpinRemoved(it)
                }
            }

            true
        }

        ClickEvents.onClick {
            if (!Config.Global.chatroomChannelButtons) return@onClick
            requireTitle("CHAT ROOMS")
            val clickedSlot = clickedSlot() ?: return@onClick
            val clickedItem = clickedSlot.item
            if (!isInBoundary(clickedSlot.index)) return@onClick
            if (middle) {
                val id = clickedItem.hoverName?.string ?: return@onClick
                val model = clickedItem.components?.get(DataComponents.ITEM_MODEL) ?: return@onClick
                val color = ChatroomColor.entries.find {it.getItemModel() == model} ?: ChatroomColor.WHITE
                val chatroom = Chatroom(id, color)
                if (playerState().activeChatrooms.removeIf { chatroom.id == it.id }) {
                    Logger.sendMessage(
                        Component.literal("Unpinned chatroom ").withColor(chatroom.color.color)
                            .append(
                                Component.literal(chatroom.id).withStyle(
                                    ChatFormatting.WHITE
                                )
                            )
                    )
                    return@onClick
                }

                playerState().activeChatrooms.add(chatroom)
                Logger.sendMessage(
                    Component.literal("Pinned chatroom ").withColor(chatroom.color.color)
                        .append(
                            Component.literal(chatroom.id).withStyle(
                                ChatFormatting.WHITE
                            )
                        )
                )
            }

            // User left chatroom, unpin if needed
            if (shift && right) {
                val id = clickedItem.hoverName?.string ?: return@onClick
                unpinRemoved(id)
            }
        }
    }

    @JvmStatic
    fun modifyComponent(component: Component): Component {
        playerState().activeChatrooms.forEach { chatroom ->
            Regex("""\[${chatroom.id.uppercase()}] .+""").find(component.string)?.let {

                // This will correct the color of the chatroom to make sure it's always up to date
                val color = component.toFlatList().first().style.color?.value ?: return@let
                if (color != chatroom.color.color) {
                    chatroom.color =
                        ChatroomColor.entries.find { it.color == color } ?: chatroom.color
                }
                val mutable = component as MutableComponent
                // Add a click action to lock chatroom
                mutable.style = mutable.style
                    .withClickEvent(ClickEvent.RunCommand("chatroomlock ${chatroom.id}"))
                    .withHoverEvent(HoverEvent.ShowText(getTooltip()))

                return mutable
            }
        }

        return component
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
                Component.literal("Toggle Chatroom Lock")
                    .withColor(0xfbe460)
                    .defaultFont()
            )
    }

    fun enableLock(chatroom: Chatroom, sendMessage: Boolean = false) {
        playerState().activeChatrooms.apply {
            remove(chatroom)
            add(0, chatroom)
        }
        ChatControllerManager.setController(ChatroomController(chatroom))
        if (sendMessage) {
            Logger.sendMessage(
                Component.literal("Enabled Chatroom Lock for ").withColor(chatroom.color.color)
                    .append(
                        Component.literal(
                            chatroom.id
                        ).withStyle(ChatFormatting.WHITE)
                    )
            )
        }
    }

    fun disableLock(sendMessage: Boolean = false) {
        ChatControllerManager.clearController()
        if (sendMessage) {
            Logger.sendMessage(
                Component.literal("Disabled Chatroom Lock").withSwatch(TridentFont.TRIDENT_COLOR)
            )
        }
    }

    private val SPRITE = Texture(
        Resources.trident("textures/interface/pin_icon.png"),
        10,
        10
    )

    @JvmStatic
    fun renderPinIcon(graphics: GuiGraphicsExtractor, slot: Slot) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.chatroomChannelButtons) return
        val screen = minecraft().screen as? ContainerScreen ?: return
        if (isInBoundary(slot.index)) {
            withContainerCtx(screen) {
                requireTitle("CHAT ROOMS")
                val chatroomID = slot.item.hoverName.string.uppercase()

                playerState().activeChatrooms.forEach {
                    if (it.id == chatroomID) {
                        SPRITE.blit(graphics, slot.x + 8, slot.y - 2)
                    }
                }
            }

        }
    }

    private fun isInBoundary(index: Int): Boolean {
        return index in 19..25 ||
                index in 28..34 ||
                index in 37..43
    }

    @JvmStatic
    fun modifyTooltip(consumer: Consumer<Component>) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.chatroomChannelButtons) return
        val screen = minecraft().screen as? ContainerScreen ?: return

        withContainerCtx(screen) {
            requireTitle("CHAT ROOMS")
            val slot = hoveredSlot() ?: return@withContainerCtx
            val item = slot.item
            val chatroomID = item.hoverName.string.uppercase()

            val isPinned = playerState().activeChatrooms.firstOrNull { it.id == chatroomID } != null
            if (!isInBoundary(slot.index)) return@withContainerCtx
            val component =
                FontCollection.get("_fonts/icon/click_action_middle.png", 7, 7).withColor(0xffffff)
                    .mccFont("icon")
                    .append(
                        Component.literal(" > ").withStyle(ChatFormatting.DARK_GRAY).defaultFont()
                    )
                    .append(
                        Component.literal("Middle-Click to ").withColor(0xe9d282).defaultFont()
                    ).append(
                        Component.literal("${if (isPinned) "Unpin" else "Pin"} chatroom")
                            .withColor(0xfbe460).defaultFont()
                    )

            consumer.accept(component)
        }
    }

    @Serializable
    data class Chatroom(val id: String, var color: ChatroomColor)

    @Suppress("unused")
    enum class ChatroomColor(
        val color: Int
    ) {
        WHITE(0xFFFFFF),
        YELLOW(0xFFFF55),
        BLUE(0x5555FF),
        GREEN(0x55FF55),
        ORANGE(0xFF5F1F),
        PINK(0xFF55FF),
        TEAL(0x55FFFF);

        fun getItemModel() = Resources.mcc("island_interface/settings/chat_bubble_${this.name.lowercase()}")
        fun getChatIconTexture() = Resources.trident("textures/interface/chat_channels/channel_${this.name.lowercase()}.png")
    }
}