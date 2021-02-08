package com.chaos.ekinomy.data

import java.util.*

sealed class OperationType(val balance: Double, val playerName: String?, val playerUUID: UUID?) {
    class ADD(balance: Double, playerName: String?, playerUUID: UUID?): OperationType(balance, playerName, playerUUID)
    class SET(balance: Double, playerName: String?, playerUUID: UUID?): OperationType(balance, playerName, playerUUID)
}