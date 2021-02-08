package com.chaos.ekinomy.data

import java.util.*

data class PlayerBalanceData(val playerName: String, val playerUUID: UUID, var balance: Long) {
    operator fun plus(balance: Long) = PlayerBalanceData(playerName, playerUUID, this.balance + balance)

    operator fun plus(balanceData: PlayerBalanceData) = plus(balanceData.balance)

    operator fun plusAssign(balance: Long) {
        this.balance += balance
    }

    operator fun plusAssign(balanceData: PlayerBalanceData) = plusAssign(balanceData.balance)
}
