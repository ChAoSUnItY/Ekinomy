package com.chaos.ekinomy.data

import java.util.*

abstract class PlayerData {
    abstract val playerName: String
    abstract val playerUUID: UUID
    abstract var balance: Long
}