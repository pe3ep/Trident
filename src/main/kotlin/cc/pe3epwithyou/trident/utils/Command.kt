package cc.pe3epwithyou.trident.utils

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import java.util.concurrent.CompletableFuture

/**
 * Simple DSL to create commands
 *
 * Example usage:
 * ```kt
 * // Simple command without arguments
 * val simpleCommand = Command("simple") {
 *   executes {
 *     // ...
 *   }
 * }
 *
 * // Command with arguments
 * val commandWithArgs = Command("foo") {
 *   argument("bar") {
 *     executes {
 *       val arg = it.getArgument("bar", String::class.java)
 *       // ...
 *     }
 *   }
 * }
 * ```
 */
class Command(
    name: String, block: Builder.() -> Unit
) {
    private val root: LiteralArgumentBuilder<FabricClientCommandSource> =
        ClientCommandManager.literal(name)

    init {
        Builder(root).block()
    }

    fun build(): LiteralArgumentBuilder<FabricClientCommandSource> = root

    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        dispatcher.register(root)
    }

    class Builder(
        private val node: LiteralArgumentBuilder<FabricClientCommandSource>
    ) {

        /** Context variant: executes { ctx -> ... } */
        fun executes(block: (CommandContext<FabricClientCommandSource>) -> Unit) {
            node.executes { ctx ->
                block(ctx)
                0
            }
        }

        fun literal(
            name: String, block: Builder.() -> Unit
        ) {
            val literal = ClientCommandManager.literal(name)
            Builder(literal).block()
            node.then(literal)
        }

        fun <T> argument(
            name: String, type: ArgumentType<T>, block: ArgumentBuilder<T>.() -> Unit
        ) {
            val argNode = ClientCommandManager.argument(name, type)
            ArgumentBuilder(argNode).block()
            node.then(argNode)
        }

        /** Convenience: string argument */
        fun argument(
            name: String, block: ArgumentBuilder<String>.() -> Unit
        ) {
            argument(name, StringArgumentType.string(), block)
        }
    }

    class ArgumentBuilder<T>(
        private val node: RequiredArgumentBuilder<FabricClientCommandSource, T>
    ) {
        fun executes(block: (CommandContext<FabricClientCommandSource>) -> Unit) {
            node.executes { ctx ->
                block(ctx)
                0
            }
        }

        fun suggests(
            provider: (CommandContext<FabricClientCommandSource>, SuggestionsBuilder) -> CompletableFuture<Suggestions>
        ) {
            node.suggests(provider)
        }

        fun literal(
            name: String, block: Builder.() -> Unit
        ) {
            val literal = ClientCommandManager.literal(name)
            Builder(literal).block()
            node.then(literal)
        }

        fun <U> argument(
            name: String, type: ArgumentType<U>, block: ArgumentBuilder<U>.() -> Unit
        ) {
            val argNode = ClientCommandManager.argument(name, type)
            ArgumentBuilder(argNode).block()
            node.then(argNode)
        }

        fun argument(
            name: String, block: ArgumentBuilder<String>.() -> Unit
        ) {
            argument(name, StringArgumentType.string(), block)
        }
    }
}
