package com.chaos.ekinomy.command

import com.chaos.ekinomy.data.OperationType
import com.chaos.ekinomy.data.PlayerBalanceData
import com.chaos.ekinomy.handler.EkinomyManager
import com.chaos.ekinomy.util.config.Config
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
                    Commands.argument("target", GameProfileArgument.gameProfile())
                        .then(
                            Commands.argument("value", LongArgumentType.longArg())
                                .executes {
                                    val profiles = GameProfileArgument.getGameProfiles(it, "target")

                                    for (gameProfile in profiles)
                                        executeAddBalanceOperation(
                                            it,
                                            LongArgumentType.getLong(it, "value"),
                                            it.source.world.getPlayerByUuid(gameProfile.id)
                                        )

                                    1
                                }
                        )
                )
            ).then(
                Commands.literal("sub").requires { it.hasPermissionLevel(2) }.then(
                    Commands.argument("target", GameProfileArgument.gameProfile())
                        .then(
                            Commands.argument("value", LongArgumentType.longArg())
                                .executes {
                                    val profiles = GameProfileArgument.getGameProfiles(it, "target")

                                    for (gameProfile in profiles)
                                        executeSubBalanceOperation(
                                            it,
                                            LongArgumentType.getLong(it, "value"),
                                            it.source.world.getPlayerByUuid(gameProfile.id)
                                        )

                                    1
                                }
                        )
                )
            ).then(
                Commands.literal("pay").then(
                    Commands.argument("from", GameProfileArgument.gameProfile()).requires { it.hasPermissionLevel(2) }
                        .then(
                            Commands.argument("to", GameProfileArgument.gameProfile())
                                .then(
                                    Commands.argument("value", LongArgumentType.longArg())
                                        .executes {
                                            val fromPlayers = GameProfileArgument.getGameProfiles(it, "from")
                                            val toPlayers = GameProfileArgument.getGameProfiles(it, "to")

                                            executePayBalanceOperation(
                                                it,
                                                LongArgumentType.getLong(it, "value"),
                                                fromPlayers.toList(),
                                                toPlayers.toList()
                                            )
                                        }
                                )
                        )
                ).then(
                    Commands.argument("target", GameProfileArgument.gameProfile())
                        .then(
                            Commands.argument("value", LongArgumentType.longArg())
                                .executes {
                                    val profiles = GameProfileArgument.getGameProfiles(it, "target")

                                    executePayBalanceOperation(
                                        it,
                                        LongArgumentType.getLong(it, "value"),
                                        listOf(it.source.asPlayer().gameProfile),
                                        profiles.toList()
                                    )

                                    1
                                }
                        )
                )
            ).then(
                Commands.literal("set").requires { it.hasPermissionLevel(2) }
                    .then(
                        Commands.argument("target", GameProfileArgument.gameProfile())
                            .then(
                                Commands.argument("value", LongArgumentType.longArg())
                                    .executes {
                                        val profiles = GameProfileArgument.getGameProfiles(it, "target")

                                        executeSetBalanceOperation(
                                            it,
                                            LongArgumentType.getLong(it, "value"),
                                            profiles.toList()
                                        )
                                    }
                            )
                    )
            ).then(
                Commands.literal("reset").requires { it.hasPermissionLevel(2) }
                    .then(
                        Commands.argument("target", GameProfileArgument.gameProfile())
                            .executes {
                                val profiles = GameProfileArgument.getGameProfiles(it, "target")

                                executeResetBalanceOperation(
                                    it,
                                    profiles.toList()
                                )
                            }
                    )
            ).executes(::sendHelpMessage)
        )

    private fun sendHelpMessage(context: CommandContext<CommandSource>): Int {
        val message = """
            ================== Ekinomy Help ==============================
            /ekinomy balance: shows your current balance
            /ekinomy add <target> <value>: adds balance to target player
            =============================================================
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
        val playerEntity = player ?: context.source.asPlayer()
        val isSelf = player?.uniqueID == context.source.asPlayer().uniqueID

        if (EkinomyManager.operate(
                OperationType.ADD(
                    balance,
                    player?.uniqueID ?: context.source.asPlayer().uniqueID
                )
            )
        )
            playerEntity.sendStatusMessage(
                StringTextComponent("Added $balance to ${if (isSelf) "your" else playerEntity.displayName.string} balance").mergeStyle(
                    TextFormatting.GREEN
                ),
                false
            )
        else
            playerEntity.sendStatusMessage(
                StringTextComponent("Could not add balance to ${if (isSelf) "your" else playerEntity.displayName.string} balance").mergeStyle(
                    TextFormatting.RED
                ),
                false
            )

        return 1
    }

    private fun executeSubBalanceOperation(
        context: CommandContext<CommandSource>,
        balance: Long,
        player: PlayerEntity? = null
    ): Int {
        val playerEntity = player ?: context.source.asPlayer()
        val isSelf = player?.uniqueID == context.source.asPlayer().uniqueID

        if (EkinomyManager.operate(
                OperationType.SUB(
                    balance,
                    player?.uniqueID ?: context.source.asPlayer().uniqueID
                )
            )
        )
            playerEntity.sendStatusMessage(
                StringTextComponent("Subtracted $balance from ${if (isSelf) "your" else playerEntity.displayName.string} balance").mergeStyle(
                    TextFormatting.GREEN
                ),
                false
            )
        else
            playerEntity.sendStatusMessage(
                StringTextComponent("Could not subtract balance from ${if (isSelf) "your" else playerEntity.displayName.string} balance").mergeStyle(
                    TextFormatting.RED
                ),
                false
            )

        return 1
    }

    private fun executePayBalanceOperation(
        context: CommandContext<CommandSource>,
        balance: Long,
        fromPlayers: List<GameProfile>,
        toPlayers: List<GameProfile>
    ): Int {
        val world = context.source.world
        val sourcePlayerEntity = context.source.asPlayer()
        val fromPlayerEntities = fromPlayers.map { world.getPlayerByUuid(it.id) }
        val toPlayerEntities = toPlayers.map { world.getPlayerByUuid(it.id) }

        if (fromPlayerEntities.size == 1) {
            if (toPlayerEntities.size == 1) {
                executePayOperation(
                    balance,
                    sourcePlayerEntity,
                    fromPlayerEntities.first(),
                    toPlayerEntities.first()
                )
            } else {
                for (toPlayer in toPlayerEntities) {
                    executePayOperation(
                        balance,
                        sourcePlayerEntity,
                        fromPlayerEntities.first(),
                        toPlayer
                    )
                }
            }
        } else {
            if (toPlayerEntities.size == 1) {
                for (fromPlayer in fromPlayerEntities) {
                    executePayOperation(
                        balance,
                        sourcePlayerEntity,
                        fromPlayer,
                        toPlayerEntities.first()
                    )
                }
            } else {
                for (fromPlayer in fromPlayerEntities) {
                    for (toPlayer in toPlayerEntities) {
                        executePayOperation(
                            balance,
                            sourcePlayerEntity,
                            fromPlayer,
                            toPlayer
                        )
                    }
                }
            }
        }

        return 1
    }

    private fun executePayOperation(
        balance: Long,
        sourcePlayer: PlayerEntity,
        fromPlayer: PlayerEntity?,
        toPlayer: PlayerEntity?
    ) {
        if (fromPlayer == null || toPlayer == null) {
            sourcePlayer.sendStatusMessage(
                StringTextComponent("Could not execute pay action. Players might not exists."),
                false
            )
            return
        }

        if (EkinomyManager.operate(
                OperationType.PAY(
                    balance,
                    fromPlayer.uniqueID,
                    toPlayer.uniqueID
                )
            )
        )
            sourcePlayer.sendStatusMessage(
                StringTextComponent("Paid $balance from ${fromPlayer.displayName.string} to ${toPlayer.displayName.string}").mergeStyle(
                    TextFormatting.GREEN
                ),
                false
            )
        else
            sourcePlayer.sendStatusMessage(
                StringTextComponent("Could not pay balance from ${fromPlayer.displayName.string} to ${toPlayer.displayName.string}").mergeStyle(
                    TextFormatting.RED
                ),
                false
            )
    }

    private fun executeSetBalanceOperation(
        context: CommandContext<CommandSource>,
        balance: Long,
        players: List<GameProfile>
    ): Int {
        val sourcePlayer = context.source.asPlayer()

        for (player in players.map { context.source.world.getPlayerByUuid(it.id) }) {
            if (player != null)
                if (EkinomyManager.operate(
                        OperationType.SET(
                            balance,
                            player.uniqueID
                        )
                    )
                )
                    sourcePlayer.sendStatusMessage(
                        StringTextComponent("Set ${player.displayName.string}'s balance to $balance").mergeStyle(
                            TextFormatting.GREEN
                        ),
                        false
                    )
                else
                    sourcePlayer.sendStatusMessage(
                        StringTextComponent("Could not set ${player.displayName.string}'s balance to $balance").mergeStyle(
                            TextFormatting.RED
                        ),
                        false
                    )
        }

        return 1
    }

    private fun executeResetBalanceOperation(
        context: CommandContext<CommandSource>,
        players: List<GameProfile>
    ): Int {
        val sourcePlayer = context.source.asPlayer()

        for (player in players.map { context.source.world.getEntityByUuid(it.id) }) {
            if (player != null)
                if (EkinomyManager.operate(
                        OperationType.RESET(
                            player.uniqueID
                        )
                    )
                )
                    sourcePlayer.sendStatusMessage(
                        StringTextComponent("Reset ${player.displayName.string}'s balance to ${Config.SERVER.initialBalance.get()}").mergeStyle(
                            TextFormatting.GREEN
                        ),
                        false
                    )
                else
                    sourcePlayer.sendStatusMessage(
                        StringTextComponent("Could not reset ${player.displayName.string}'s balance to ${Config.SERVER.initialBalance.get()}").mergeStyle(
                            TextFormatting.RED
                        ),
                        false
                    )
        }

        return 1
    }

    private fun getPlayerByUUID(context: CommandContext<CommandSource>, uuid: UUID): PlayerEntity? =
        context.source.server.playerList.getPlayerByUUID(uuid)
}