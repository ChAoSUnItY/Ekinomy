package com.chaos.ekinomy.api;

import com.chaos.ekinomy.data.LogBundle;
import com.chaos.ekinomy.data.OperationType;
import com.chaos.ekinomy.data.PlayerBalanceData;
import com.chaos.ekinomy.data.PlayerCachedData;
import com.chaos.ekinomy.handler.EkinomyManager;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class EkinomyJavaAPI {
    public static void initManagerWithCachedData(List<PlayerCachedData> cachedDataList) {
        initManager(cachedDataList, Lists.newArrayList());
    }

    public static void initManagerWithBalanceData(List<PlayerBalanceData> balanceDataList) {
        initManagerWithCachedData(balanceDataList.stream().map(PlayerBalanceData::asCachedData).collect(Collectors.toList()));
    }

    public static void initManager(List<PlayerCachedData> cachedDataList, List<LogBundle> logList) {
        EkinomyManager.INSTANCE.init$Ekinomy(cachedDataList, logList);
    }

    public static void reloadManagerWithCachedData(List<PlayerCachedData> cachedDataList) {
        reloadManager(cachedDataList, Lists.newArrayList());
    }

    public static void reloadManagerWithBalanceData(List<PlayerBalanceData> balanceDataList) {
        reloadManagerWithCachedData(balanceDataList.stream().map(PlayerBalanceData::asCachedData).collect(Collectors.toList()));
    }

    public static void reloadManager(List<PlayerCachedData> cachedDataList, List<LogBundle> logList) {
        EkinomyManager.INSTANCE.reload$Ekinomy(cachedDataList, logList);
    }

    public static List<PlayerCachedData> getCachedDataList() {
        return EkinomyManager.INSTANCE.getCachedDataCollection$Ekinomy();
    }

    public static Optional<PlayerCachedData> getPlayerCachedData(UUID playerUUID) {
        return Optional.ofNullable(EkinomyManager.INSTANCE.getCachedData$Ekinomy(playerUUID));
    }

    public static List<PlayerBalanceData> getBalanceDataList() {
        return EkinomyManager.INSTANCE.getBalanceDataCollection$Ekinomy();
    }

    public static PlayerBalanceData getPlayerBalanceDataOrCreate(PlayerEntity player) {
        return EkinomyManager.INSTANCE.getDataOrCreate$Ekinomy(player);
    }

    public static Optional<PlayerBalanceData> getPlayerBalanceData(UUID playerUUID) {
        return Optional.ofNullable(EkinomyManager.INSTANCE.getDataByUUID$Ekinomy(playerUUID));
    }

    public static List<LogBundle> getLogList() {
        return EkinomyManager.INSTANCE.getAllLogCollection$Ekinomy();
    }

    public static boolean playerHasData(PlayerEntity player) {
        return EkinomyManager.INSTANCE.has$Ekinomy(player);
    }

    public static boolean playerHasData(UUID playerUUID) {
        return EkinomyManager.INSTANCE.has$Ekinomy(playerUUID);
    }

    public static boolean replacePlayerData(UUID playerUUID, PlayerCachedData newData) {
        return EkinomyManager.INSTANCE.replaceData$Ekinomy(playerUUID, newData);
    }

    public static boolean operate(OperationType operation) {
        return EkinomyManager.INSTANCE.operate$Ekinomy(operation);
    }
}
