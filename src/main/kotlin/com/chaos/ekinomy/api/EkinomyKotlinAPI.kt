package com.chaos.ekinomy.api

import com.chaos.ekinomy.data.LogBundle
import com.chaos.ekinomy.data.OperationType
import com.chaos.ekinomy.data.PlayerBalanceData
import com.chaos.ekinomy.data.PlayerCachedData
import com.chaos.ekinomy.util.interop.unwrap
import net.minecraft.entity.player.PlayerEntity
import java.util.*

object EkinomyKotlinAPI {
    fun initManagerWithCachedData(cachedDataList: List<PlayerCachedData>, logList: List<LogBundle> = mutableListOf()) =
        EkinomyJavaAPI.initManager(cachedDataList, logList)

    fun initManagerWithBalanceData(balanceDataList: List<PlayerBalanceData>, logList: List<LogBundle> = mutableListOf()) =
        EkinomyJavaAPI.initManager(balanceDataList.map(PlayerBalanceData::asCachedData), logList)

    fun reloadManagerWithCachedData(cachedDataList: List<PlayerCachedData>, logList: List<LogBundle> = mutableListOf()) =
        EkinomyJavaAPI.reloadManager(cachedDataList, logList)

    fun reloadManagerWithBalanceData(balanceDataList: List<PlayerBalanceData>, logList: List<LogBundle> = mutableListOf()) =
        EkinomyJavaAPI.reloadManager(balanceDataList.map(PlayerBalanceData::asCachedData), logList)

    fun getCachedDataList(): List<PlayerCachedData> =
        EkinomyJavaAPI.getCachedDataList()

    fun getPlayerCachedData(playerUUID: UUID): PlayerCachedData? =
        EkinomyJavaAPI.getPlayerCachedData(playerUUID).unwrap()

    fun getBalanceDataList(): List<PlayerBalanceData> =
        EkinomyJavaAPI.getBalanceDataList()

    fun getPlayerBalanceDataOrCreate(player: PlayerEntity): PlayerBalanceData =
        EkinomyJavaAPI.getPlayerBalanceDataOrCreate(player)

    fun getPlayerBalanceData(playerUUID: UUID): PlayerBalanceData? =
        EkinomyJavaAPI.getPlayerBalanceData(playerUUID).unwrap()

    fun getLogList(): List<LogBundle> =
        EkinomyJavaAPI.getLogList()

    fun playerHasData(player: PlayerEntity) =
        EkinomyJavaAPI.playerHasData(player)

    fun playerHasData(playerUUID: UUID) =
        EkinomyJavaAPI.playerHasData(playerUUID)

    fun replacePlayerData(playerUUID: UUID, newData: PlayerCachedData) =
        EkinomyJavaAPI.replacePlayerData(playerUUID, newData)

    fun operate(operation: OperationType) =
        EkinomyJavaAPI.operate(operation)
}