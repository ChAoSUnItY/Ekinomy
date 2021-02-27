package com.chaos.ekinomy.data

import com.chaos.ekinomy.util.config.Config
import java.util.*

data class PlayerCachedData(
    override val playerName: String,
    override val playerUUID: UUID,
    override var balance: Long,
    val logs: MutableList<LogBundle> = mutableListOf()
) : PlayerData() {
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

    fun log(operationType: OperationType): PlayerCachedData =
        log(LogBundle(operationType, asBalanceData()))

    fun log(LogBundle: LogBundle): PlayerCachedData {
        if (Config.SERVER.storeLog.get())
            logs.add(LogBundle)

        return this
    }

    fun asBalanceData() = PlayerBalanceData(playerName, playerUUID, balance)
}