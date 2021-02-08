package com.chaos.ekinomy.handler

import com.chaos.ekinomy.data.OperationType
import com.chaos.ekinomy.data.PlayerBalanceData
import com.chaos.ekinomy.util.config.Config
import net.minecraft.entity.player.PlayerEntity
import java.util.*

internal object EkinomyManager {
    private val cache: MutableList<PlayerBalanceData> = mutableListOf()

    fun init(dataCollection: List<PlayerBalanceData>) = this.cache.addAll(cache)

    fun reload(dataCollection: List<PlayerBalanceData>) {
        clear()
        init(dataCollection)
    }

    fun clear() = cache.clear()

    fun getBalanceDataCollection(): MutableList<PlayerBalanceData> = cache.toMutableList()

    fun has(entity: PlayerEntity): Boolean = has(entity.name.string).and(has(entity.uniqueID))

    fun has(data: PlayerBalanceData): Boolean = has(data.playerName).and(has(data.playerUUID))

    fun has(playerName: String): Boolean = has { it.playerName == playerName }

    fun has(playerUUID: UUID): Boolean = has { it.playerUUID == playerUUID }

    private inline fun has(crossinline predicate: (PlayerBalanceData) -> Boolean): Boolean =
        cache.any { predicate.invoke(it) }

    fun addData(data: PlayerBalanceData): Boolean =
        if (has(data))
            false
        else
            cache.add(data)

    fun operate(opType: OperationType): Boolean {
        return when {
            opType.playerUUID != null -> {
                when (opType) {
                    is OperationType.ADD -> addBalanceByUUID(opType.balance, opType.playerUUID)
                    is OperationType.SET -> setBalanceByUUID(opType.balance, opType.playerUUID)
                    is OperationType.RESET -> resetBalanceByUUID(opType.playerUUID)
                }
            }
            opType.playerName != null -> {
                when (opType) {
                    is OperationType.ADD -> addBalanceByName(opType.balance, opType.playerName)
                    is OperationType.SET -> setBalanceByName(opType.balance, opType.playerName)
                    is OperationType.RESET -> resetBalanceByName(opType.playerName)
                }
            }
            else -> false
        }
    }

    fun addBalanceByName(balance: Long, playerName: String? = null): Boolean {
        return when {
            playerName.isNullOrEmpty() -> false
            playerName.isNotEmpty() -> {
                if (!has(playerName))
                    false
                else {
                    reload(cache.map {
                        if (it.playerName == playerName)
                            it + balance
                        else it
                    })
                    true
                }
            }
            else -> false
        }
    }

    fun setBalanceByName(balance: Long, playerName: String? = null): Boolean {
        return when {
            playerName.isNullOrEmpty() -> false
            else -> {
                if (!has(playerName))
                    false
                else {
                    reload(cache.map {
                        if (it.playerName == playerName)
                            PlayerBalanceData(playerName, it.playerUUID, balance)
                        else it
                    })
                    true
                }
            }
        }
    }

    fun resetBalanceByName(playerName: String? = null): Boolean {
        return when {
            playerName.isNullOrEmpty() -> {
                reload(cache.map {
                    PlayerBalanceData(it.playerName, it.playerUUID, Config.SERVER.initialBalance.get())
                })
                true
            }
            else -> {
                if (!has(playerName))
                    false
                else {
                    reload(cache.map {
                        if (it.playerName == playerName)
                            PlayerBalanceData(playerName, it.playerUUID, Config.SERVER.initialBalance.get())
                        else it
                    })
                    true
                }
            }
        }
    }

    fun addBalanceByUUID(balance: Long, playerUUID: UUID? = null): Boolean {
        return when (playerUUID) {
            null -> false
            else -> {
                if (!has(playerUUID))
                    false
                else {
                    reload(cache.map {
                        if (it.playerUUID == playerUUID)
                            it + balance
                        else it
                    })
                    true
                }
            }
        }
    }

    fun setBalanceByUUID(balance: Long, playerUUID: UUID? = null): Boolean {
        return when (playerUUID) {
            null -> false
            else -> {
                if (!has(playerUUID))
                    false
                else {
                    reload(cache.map {
                        if (it.playerUUID == playerUUID)
                            PlayerBalanceData(it.playerName, playerUUID, balance)
                        else it
                    })
                    true
                }
            }
        }
    }

    fun resetBalanceByUUID(playerUUID: UUID? = null): Boolean {
        return when (playerUUID) {
            null -> false
            else -> {
                if (!has(playerUUID))
                    false
                else {
                    reload(cache.map {
                        if (it.playerUUID == playerUUID)
                            PlayerBalanceData(it.playerName, playerUUID, Config.SERVER.initialBalance.get())
                        else it
                    })
                    true
                }
            }
        }
    }

    fun getDataByName(playerName: String): PlayerBalanceData? = cache.find { it.playerName == playerName }

    fun getDataByUUID(playerUUID: UUID): PlayerBalanceData? = cache.find { it.playerUUID == playerUUID }
}