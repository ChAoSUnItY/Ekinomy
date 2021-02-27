package com.chaos.ekinomy.data

import net.minecraft.entity.player.PlayerEntity
import java.util.*
import kotlin.properties.Delegates

data class LogBundle(val operationType: OperationType, val data: PlayerBalanceData) {
    inner class Builder {
        lateinit var playerUUID: UUID
        lateinit var playerName: String
        var balance by Delegates.notNull<Long>()

        fun setPlayer(player: PlayerEntity): Builder {
            playerUUID = player.uniqueID
            playerName = player.displayName.string

            return this
        }

        fun setBalance(balance: Long): Builder {
            this.balance = balance

            return this
        }

        infix fun build(operationType: OperationType): LogBundle =
            LogBundle(operationType, PlayerBalanceData(playerName, playerUUID, balance))
    }
}