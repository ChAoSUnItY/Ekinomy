package com.chaos.ekinomy.data

import com.chaos.ekinomy.util.config.Config
import java.util.*

sealed class OperationType(val type: OpType) {
    abstract val balance: Long
    abstract val playerUUID: UUID

    internal data class DATA(val targetType: OpType, override val balance: Long, override val playerUUID: UUID) :
        OperationType(OpType.DATA)

    data class ADD(override val balance: Long, override val playerUUID: UUID) :
        OperationType(OpType.ADD)

    data class SUB(override val balance: Long, override val playerUUID: UUID) :
        OperationType(OpType.SUB)

    data class PAY(override val balance: Long, override val playerUUID: UUID, val targetPlayerUUID: UUID) :
        OperationType(OpType.PAY)

    data class SET(override val balance: Long, override val playerUUID: UUID) :
        OperationType(OpType.SET)

    data class RESET(override val playerUUID: UUID, override val balance: Long = Config.SERVER.initialBalance.get()) :
        OperationType(OpType.RESET)

    enum class OpType {
        DATA, ADD, SUB, PAY, SET, RESET
    }
}