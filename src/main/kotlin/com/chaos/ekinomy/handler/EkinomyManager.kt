package com.chaos.ekinomy.handler

import com.chaos.ekinomy.data.OperationType
import com.chaos.ekinomy.data.PlayerBalanceData
import com.chaos.ekinomy.data.PlayerCachedData
import com.chaos.ekinomy.util.config.Config
import net.minecraft.entity.player.PlayerEntity
import java.util.*

internal object EkinomyManager {
    private val cache: MutableList<PlayerCachedData> = mutableListOf()

    internal fun init(dataCollection: List<PlayerBalanceData>) {
        clear()

        val cachedData = dataCollection.map(PlayerBalanceData::asCachedData)

        cache.addAll(cachedData)
    }

    internal fun reload(dataCollection: List<PlayerBalanceData>) {
        init(dataCollection)
    }

    private fun clear() = cache.clear()

    internal fun getBalanceDataCollection(): MutableList<PlayerBalanceData> =
        cache.map(PlayerCachedData::asBalanceData).toMutableList()

    internal fun has(entity: PlayerEntity): Boolean = has(entity.uniqueID)

    internal fun has(playerUUID: UUID): Boolean = cache.find { it.playerUUID == playerUUID } != null

    internal fun getDataIndex(playerUUID: UUID): Int = cache.indexOfFirst { it.playerUUID == playerUUID }

    internal fun replaceData(playerUUID: UUID, cachedData: PlayerCachedData): Boolean {
        val index = getDataIndex(playerUUID)

        return if (index == -1)
            false
        else {
            cache[index] = cachedData
            true
        }
    }

    internal fun operate(opType: OperationType): Boolean {
        return when (opType) {
            is OperationType.ADD -> addBalance(opType.balance, opType.playerUUID)
            is OperationType.SUB -> subBalance(opType.balance, opType.playerUUID)
            is OperationType.PAY -> payBalance(opType.balance, opType.playerUUID, opType.targetPlayerUUID)
            is OperationType.SET -> setBalance(opType.balance, opType.playerUUID)
            is OperationType.RESET -> resetBalance(opType.playerUUID)
        }
    }

    internal fun addBalance(balance: Long, playerUUID: UUID? = null): Boolean {
        return when (playerUUID) {
            null -> false
            else -> {
                if (!has(playerUUID))
                    false
                else {
                    replaceData(playerUUID, getCachedData(playerUUID)!!.operate(OperationType.ADD(balance, playerUUID)))
                    true
                }
            }
        }
    }

    internal fun subBalance(balance: Long, playerUUID: UUID? = null): Boolean {
        return when (playerUUID) {
            null -> false
            else -> {
                if (!has(playerUUID))
                    false
                else {
                    replaceData(playerUUID, getCachedData(playerUUID)!!.operate(OperationType.SUB(balance, playerUUID)))
                    true
                }
            }
        }
    }

    internal fun payBalance(balance: Long, playerUUID: UUID?, targetPlayerUUID: UUID?): Boolean {
        return when (playerUUID) {
            null -> false
            else -> {
                when (targetPlayerUUID) {
                    null -> false
                    else -> {
                        if (!has(playerUUID) && !has(targetPlayerUUID))
                            false
                        else {
                            if (getDataByUUID(playerUUID)?.balance!! < balance)
                                false
                            else {
                                subBalance(balance, playerUUID)
                                addBalance(balance, targetPlayerUUID)
                                true
                            }
                        }
                    }
                }
            }
        }
    }

    internal fun setBalance(balance: Long, playerUUID: UUID? = null): Boolean {
        return when (playerUUID) {
            null -> false
            else -> {
                if (!has(playerUUID))
                    false
                else {
                    replaceData(playerUUID, getCachedData(playerUUID)!!.operate(OperationType.SET(balance, playerUUID)))
                    true
                }
            }
        }
    }

    internal fun resetBalance(playerUUID: UUID? = null): Boolean {
        return when (playerUUID) {
            null -> false
            else -> {
                if (!has(playerUUID))
                    false
                else {
                    replaceData(playerUUID, getCachedData(playerUUID)!!.operate(OperationType.RESET(playerUUID)))
                    true
                }
            }
        }
    }

    internal fun getCachedData(playerUUID: UUID): PlayerCachedData? =
        cache.find { it.playerUUID == playerUUID }

    internal fun getDataByUUID(playerUUID: UUID): PlayerBalanceData? =
        getCachedData(playerUUID)?.asBalanceData()

    internal fun getDataOrCreate(playerEntity: PlayerEntity): PlayerBalanceData =
        getDataByUUID(playerEntity.uniqueID) ?: createData(
            playerEntity.name.string,
            playerEntity.uniqueID
        )

    private fun createData(playerName: String, playerUUID: UUID): PlayerBalanceData {
        val newPlayerData = PlayerBalanceData(playerName, playerUUID, Config.SERVER.initialBalance.get())

        cache.add(newPlayerData.asCachedData())

        return newPlayerData
    }
}