package com.chaos.ekinomy.data

import java.util.*

sealed class OperationType(val balance: Long, val playerName: String?, val playerUUID: UUID?) {
    class ADD(balance: Long, playerName: String?, playerUUID: UUID?): OperationType(balance, playerName, playerUUID)
    class SET(balance: Long, playerName: String?, playerUUID: UUID?): OperationType(balance, playerName, playerUUID)
    class RESET(balance: Long, playerName: String?, playerUUID: UUID?): OperationType(balance, playerName, playerUUID)
}