package com.chaos.ekinomy.data

import java.util.*

data class PlayerBalanceData(
    override val playerName: String,
    override val playerUUID: UUID,
    override var balance: Long
) : PlayerData() {
    operator fun plus(balance: Long) = PlayerBalanceData(playerName, playerUUID, this.balance + balance)

    operator fun plus(balanceData: PlayerBalanceData) = plus(balanceData.balance)

    operator fun plusAssign(balance: Long) {
        this.balance += balance
    }

    operator fun plusAssign(balanceData: PlayerBalanceData) = plusAssign(balanceData.balance)

    fun asCachedData() = PlayerCachedData(
        playerName,
        playerUUID,
        balance
    )
}
