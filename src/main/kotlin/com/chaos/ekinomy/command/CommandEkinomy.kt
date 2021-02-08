package com.chaos.ekinomy.command

import com.chaos.ekinomy.data.PlayerBalanceData
import com.chaos.ekinomy.handler.EkinomyManager
import com.mojang.authlib.GameProfile
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.command.arguments.GameProfileArgument
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting
import java.util.*

object CommandEkinomy {
    fun register(dispatcher: CommandDispatcher<CommandSource>): LiteralCommandNode<CommandSource> =
        dispatcher.register(
            Commands.literal("ekinomy").then(
                Commands.literal("balance").then(
                    Commands.argument("target", GameProfileArgument.gameProfile()).requires { it.hasPermissionLevel(2) }
                        .executes {
                            sendPlayerBalance(
                                it,
                                true,
                                GameProfileArgument.getGameProfiles(it, "target")
                            )
                        }
                ).executes(::sendPlayerBalance)
            ).then(
                Commands.literal("add").requires { it.hasPermissionLevel(2) }.then(
                    Commands.argument("target", GameProfileArgument.gameProfile()).requires { it.hasPermissionLevel(2) }
                        .then(
                            Commands.argument("value", LongArgumentType.longArg()).requires { it.hasPermissionLevel(2) }

                        )
                ).then(
                    Commands.argument("value", LongArgumentType.longArg()).requires { it.hasPermissionLevel(2) }
                        .executes { executeAddBalanceOperation(it, LongArgumentType.getLong(it, "value")) }
                )
            ).executes(::sendHelpMessage)
        )

    private fun sendHelpMessage(context: CommandContext<CommandSource>): Int {
        val message = """
            ================== Ekinomy Help ====================
            /ekinomy balance: shows your current balance
            ==================================================
        """.trimIndent()

        context.source.sendFeedback(StringTextComponent(message), false)

        return 1
    }

    private fun sendPlayerBalance(
        context: CommandContext<CommandSource>,
        hasTarget: Boolean = false,
        gameProfiles: Collection<GameProfile>? = null
    ): Int {
        return if (hasTarget && gameProfiles != null) {
            if (gameProfiles.none())
                sendPlayerBalance(context)
            else {
                for (player in gameProfiles)
                    sendPlayerBalance(context, getPlayerByUUID(context, player.id))
                1
            }
        } else sendPlayerBalance(context, null)

    }

    private fun sendPlayerBalance(context: CommandContext<CommandSource>, player: PlayerEntity? = null): Int {
        val data: PlayerBalanceData? =
            EkinomyManager.getDataByUUID(if (player != null) player.uniqueID else context.source.asPlayer().uniqueID)

        if (data != null)
            context.source.sendFeedback(
                StringTextComponent("${if (player == null) "Your" else player.displayName.string} balance : ").append(
                    StringTextComponent("${data.balance}").mergeStyle(
                        when {
                            data.balance < 0L -> TextFormatting.RED
                            data.balance == 0L -> TextFormatting.WHITE
                            else -> TextFormatting.GREEN
                        }
                    )
                ),
                false
            )
        else
            context.source.sendFeedback(
                StringTextComponent(
                    "${if (player != null) "Player ${player.displayName.string}'s" else "Your"} profile does not exist"
                ).mergeStyle(TextFormatting.RED),

                false
            )
        return 1
    }

    private fun executeAddBalanceOperation(
        context: CommandContext<CommandSource>,
        balance: Long,
        player: PlayerEntity? = null
    ): Int {
        if (EkinomyManager.addBalanceByUUID(balance, context.source.asPlayer().uniqueID))
            context.source.sendFeedback(
                StringTextComponent("Added $balance to ${if (player == null) "your" else player.displayName.string} balance").mergeStyle(
                    TextFormatting.GREEN
                ),
                false
            )
        else
            context.source.sendFeedback(
                StringTextComponent("Could not add balance to ${if (player == null) "your" else player.displayName.string} balance").mergeStyle(
                    TextFormatting.RED
                ),
                false
            )
        return 1
    }

    private fun getPlayerByUUID(context: CommandContext<CommandSource>, uuid: UUID): PlayerEntity? =
        context.source.server.playerList.getPlayerByUUID(uuid)
}