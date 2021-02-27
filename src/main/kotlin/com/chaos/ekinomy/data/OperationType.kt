package com.chaos.ekinomy.data

import com.chaos.ekinomy.util.config.Config
import java.util.*

sealed class OperationType {
    abstract val balance: Long
    abstract val playerUUID: UUID

    data class ADD(override val balance: Long, override val playerUUID: UUID) :
        OperationType()

    data class SUB(override val balance: Long, override val playerUUID: UUID) :
        OperationType()

    data class PAY(override val balance: Long, override val playerUUID: UUID, val targetPlayerUUID: UUID) :
        OperationType()

    data class SET(override val balance: Long, override val playerUUID: UUID) :
        OperationType()

    data class RESET(override val playerUUID: UUID, override val balance: Long = Config.SERVER.initialBalance.get()) :
        OperationType()
}