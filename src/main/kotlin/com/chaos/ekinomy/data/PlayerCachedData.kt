package com.chaos.ekinomy.data

import com.chaos.ekinomy.util.config.Config
import java.util.*

data class PlayerCachedData(
    val playerName: String,
    val playerUUID: UUID,
    var balance: Long,
    val logs: List<LogBundle> = listOf()
) {
    fun operate(operationType: OperationType): PlayerCachedData {
        when (operationType) {
            is OperationType.ADD ->
                balance += operationType.balance
            is OperationType.PAY ->
                if (balance >= operationType.balance)
                    balance -= operationType.balance
            is OperationType.RESET ->
                balance = Config.SERVER.initialBalance.get()
            is OperationType.SET ->
                balance = operationType.balance
            is OperationType.SUB ->
                balance -= operationType.balance
        }

        return this
    }

    fun asBalanceData() = PlayerBalanceData(playerName, playerUUID, balance)
}