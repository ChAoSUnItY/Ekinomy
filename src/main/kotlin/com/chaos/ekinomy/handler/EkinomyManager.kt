package com.chaos.ekinomy.handler

import com.chaos.ekinomy.data.LogBundle
import com.chaos.ekinomy.data.OperationType
import com.chaos.ekinomy.data.PlayerBalanceData
import com.chaos.ekinomy.data.PlayerCachedData
import com.chaos.ekinomy.util.config.Config
import net.minecraft.entity.player.PlayerEntity
import java.util.*

internal object EkinomyManager {
    private val cache: MutableList<PlayerCachedData> = mutableListOf()

    internal fun init(dataCollection: MutableList<PlayerCachedData>) {
        clear()

        cache.addAll(dataCollection)
    }

    internal fun reload(dataCollection: MutableList<PlayerCachedData>) =
        init(dataCollection)

    private fun clear() = cache.clear()

    internal fun getCachedDataCollection(): MutableList<PlayerCachedData> =
        cache.toMutableList()

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

    internal fun logToCachedData(operationType: OperationType, playerUUID: UUID) {
        val cachedData = getCachedData(playerUUID)
        val balanceData = cachedData?.asBalanceData()

        if (cachedData != null && balanceData != null) {
            cachedData.log(LogBundle(operationType, balanceData))
            replaceData(playerUUID, cachedData)
        }
    }

    internal fun operate(opType: OperationType): Boolean {
        return when (opType) {
            is OperationType.DATA -> false
            is OperationType.ADD -> addBalance(opType, opType.playerUUID)
            is OperationType.SUB -> subBalance(opType, opType.playerUUID)
            is OperationType.PAY -> payBalance(opType, opType.playerUUID, opType.targetPlayerUUID)
            is OperationType.SET -> setBalance(opType, opType.playerUUID)
            is OperationType.RESET -> resetBalance(opType)
        }
    }

    internal fun addBalance(opType: OperationType, playerUUID: UUID? = null, log: Boolean = true): Boolean {
        return when (playerUUID) {
            null -> false
            else -> {
                if (!has(playerUUID))
                    false
                else {
                    replaceData(playerUUID, getCachedData(playerUUID)!!.operate(opType))
                    logToCachedData(opType, playerUUID)

                    true
                }
            }
        }
    }

    internal fun subBalance(opType: OperationType, playerUUID: UUID? = null, log: Boolean = true): Boolean {
        return when (playerUUID) {
            null -> false
            else -> {
                if (!has(playerUUID))
                    false
                else {
                    replaceData(playerUUID, getCachedData(playerUUID)!!.operate(opType))
                    logToCachedData(opType, playerUUID)

                    true
                }
            }
        }
    }

    internal fun payBalance(
        opType: OperationType,
        playerUUID: UUID?,
        targetPlayerUUID: UUID?,
        log: Boolean = true
    ): Boolean {
        return when (playerUUID) {
            null -> false
            else -> {
                when (targetPlayerUUID) {
                    null -> false
                    else -> {
                        if (!has(playerUUID) && !has(targetPlayerUUID))
                            false
                        else {
                            if (getDataByUUID(playerUUID)?.balance!! < opType.balance)
                                false
                            else {
                                subBalance(opType, playerUUID, log = false)
                                addBalance(opType, targetPlayerUUID, log = false)
                                logToCachedData(opType, playerUUID)

                                true
                            }
                        }
                    }
                }
            }
        }
    }

    internal fun setBalance(opType: OperationType, playerUUID: UUID? = null, log: Boolean = true): Boolean {
        return when (playerUUID) {
            null -> false
            else -> {
                if (!has(playerUUID))
                    false
                else {
                    replaceData(playerUUID, getCachedData(playerUUID)!!.operate(opType))
                    logToCachedData(opType, playerUUID)

                    true
                }
            }
        }
    }

    internal fun resetBalance(opType: OperationType, playerUUID: UUID? = null, log: Boolean = true): Boolean {
        return when (playerUUID) {
            null -> false
            else -> {
                if (!has(playerUUID))
                    false
                else {
                    replaceData(playerUUID, getCachedData(playerUUID)!!.operate(opType))
                    logToCachedData(opType, playerUUID)

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