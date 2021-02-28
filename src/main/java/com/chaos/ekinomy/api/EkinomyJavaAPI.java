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

/**
 * All Ekinomy Api methods if not detailed then should be operated on server side.
 *
 * @author ChAoS-UnItY
 */
public class EkinomyJavaAPI {
    /**
     * Initialize manager with cached data list.
     *
     * @param cachedDataList cached data list to be initialized by manager
     */
    public static void initManagerWithCachedData(List<PlayerCachedData> cachedDataList) {
        initManager(cachedDataList, Lists.newArrayList());
    }

    /**
     * Initialize manager with balance data list.
     *
     * @param balanceDataList balance data list to be initialized by manager
     */
    public static void initManagerWithBalanceData(List<PlayerBalanceData> balanceDataList) {
        initManagerWithCachedData(balanceDataList.stream().map(PlayerBalanceData::asCachedData).collect(Collectors.toList()));
    }

    /**
     * Initialize manager.
     *
     * @param cachedDataList cached data list to be initialized by manager
     * @param logList        previous games' log list, it's fine to leave it with empty list.
     */
    public static void initManager(List<PlayerCachedData> cachedDataList, List<LogBundle> logList) {
        EkinomyManager.INSTANCE.init$Ekinomy(cachedDataList, logList);
    }

    /**
     * Reload manager with cached data list.
     *
     * @param cachedDataList cached data list to be reloaded by manager
     */
    public static void reloadManagerWithCachedData(List<PlayerCachedData> cachedDataList) {
        reloadManager(cachedDataList, Lists.newArrayList());
    }

    /**
     * Reload manager with balance data list.
     *
     * @param balanceDataList balance data list to be reloaded by manager
     */
    public static void reloadManagerWithBalanceData(List<PlayerBalanceData> balanceDataList) {
        reloadManagerWithCachedData(balanceDataList.stream().map(PlayerBalanceData::asCachedData).collect(Collectors.toList()));
    }

    /**
     * Reload manager.
     *
     * @param cachedDataList cached data list to be reloaded by manager
     * @param logList        previous games' log list, it's fine to leave it with empty list.
     */
    public static void reloadManager(List<PlayerCachedData> cachedDataList, List<LogBundle> logList) {
        EkinomyManager.INSTANCE.reload$Ekinomy(cachedDataList, logList);
    }

    /**
     * get cached data list from current game.
     *
     * @return current cached data list held by manager
     */
    public static List<PlayerCachedData> getCachedDataList() {
        return EkinomyManager.INSTANCE.getCachedDataCollection$Ekinomy();
    }

    /**
     * get specific player's cached data from current game.
     *
     * @param playerUUID UUID references to a specific alive player (Fake player is not recommended)
     * @return if player's data already memorized, return player's cached data, else return a null
     */
    public static Optional<PlayerCachedData> getPlayerCachedData(UUID playerUUID) {
        return Optional.ofNullable(EkinomyManager.INSTANCE.getCachedData$Ekinomy(playerUUID));
    }

    /**
     * get balance data list from current game.
     *
     * @return current balance data list held by manager
     */
    public static List<PlayerBalanceData> getBalanceDataList() {
        return EkinomyManager.INSTANCE.getBalanceDataCollection$Ekinomy();
    }

    /**
     * get specific player's balance data, if player's data is not memorized by manager, then create a new data instance for the player.
     *
     * @param player player entity
     * @return already memorized balance data or a new balance data
     */
    public static PlayerBalanceData getPlayerBalanceDataOrCreate(PlayerEntity player) {
        return EkinomyManager.INSTANCE.getDataOrCreate$Ekinomy(player);
    }

    /**
     * get specific player's balance data from current game.
     *
     * @param playerUUID UUID references to a specific alive player (Fake player is not recommended)
     * @return if player's data already memorized, return player's balance data, else return a null
     */
    public static Optional<PlayerBalanceData> getPlayerBalanceData(UUID playerUUID) {
        return Optional.ofNullable(EkinomyManager.INSTANCE.getDataByUUID$Ekinomy(playerUUID));
    }

    /**
     * get economy log list from current game.
     *
     * @return current log list held by manager
     */
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

    /**
     * Execute an economy operation (or action whatever), this method is server only, if you need to operate this on
     * client side, consider send a {@link com.chaos.ekinomy.util.networking.ExecuteOperationPacket} to server.
     *
     * @param operation operation to be performed by manager.
     * @return if operation is successfully executed by manager without any error.
     */
    public static boolean operate(OperationType operation) {
        return EkinomyManager.INSTANCE.operate$Ekinomy(operation);
    }
}
