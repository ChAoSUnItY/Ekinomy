package com.chaos.ekinomy.handler

import com.chaos.ekinomy.data.OperationType
import com.chaos.ekinomy.data.PlayerBalanceData
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
                }
            }
            opType.playerName != null -> {
                when (opType) {
                    is OperationType.ADD -> addBalanceByName(opType.balance, opType.playerName)
                    is OperationType.SET -> setBalanceByName(opType.balance, opType.playerName)
                }
            }
            else -> false
        }
    }

    fun addBalanceByName(balance: Double, playerName: String? = null): Boolean {
        return when {
            playerName.isNullOrEmpty() -> {
                reload(cache.map {
                    it + balance
                })
                true
            }
            playerName.isNotEmpty() -> {
                if (!has(playerName))
                    false
                else {
                    reload(cache.map {
                        if (it.playerName == playerName)
                            it + balance
                        else
                            it
                    })
                    true
                }
            }
            else -> false
        }
    }

    fun setBalanceByName(balance: Double, playerName: String? = null): Boolean {
        return when {
            playerName.isNullOrEmpty() -> false
            else -> {
                if (!has(playerName))
                    false
                else {
                    reload(cache.map {
                        if (it.playerName == playerName)
                            PlayerBalanceData(playerName, it.playerUUID, balance)
                        else
                            it
                    })
                    true
                }
            }
        }
    }

    fun addBalanceByUUID(balance: Double, playerUUID: UUID? = null): Boolean {
        return when (playerUUID) {
            null -> {
                reload(cache.map {
                    it + balance
                })
                true
            }
            else -> {
                if (!has(playerUUID))
                    false
                else {
                    reload(cache.map {
                        if (it.playerUUID == playerUUID)
                            it + balance
                        else
                            it
                    })
                    true
                }
            }
        }
    }

    fun setBalanceByUUID(balance: Double, playerUUID: UUID? = null): Boolean {
        return when (playerUUID) {
            null -> false
            else -> {
                if (!has(playerUUID))
                    false
                else {
                    reload(cache.map {
                        if (it.playerUUID == playerUUID)
                            PlayerBalanceData(it.playerName, playerUUID, balance)
                        else
                            it
                    })
                    true
                }
            }
        }
    }

    fun getDataByName(playerName: String): PlayerBalanceData? = cache.find { it.playerName == playerName }

    fun getDataByUUID(playerUUID: UUID): PlayerBalanceData? = cache.find { it.playerUUID == playerUUID }
}