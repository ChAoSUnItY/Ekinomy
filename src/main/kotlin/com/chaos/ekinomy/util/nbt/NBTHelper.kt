package com.chaos.ekinomy.util.nbt

import com.chaos.ekinomy.data.*
import net.minecraft.nbt.CompoundNBT

object NBTHelper {
    fun dataToCompoundNBT(data: PlayerData): CompoundNBT {
        val nbt = CompoundNBT()
        nbt.putString(NBTFacts.COMPOUND_PLAYER_NAME, data.playerName)
        nbt.putUniqueId(NBTFacts.COMPOUND_PLAYER_UUID, data.playerUUID)
        nbt.putLong(NBTFacts.COMPOUND_PLAYER_BALANCE, data.balance)

        return nbt
    }

    fun dataFromCompoundNBT(nbt: CompoundNBT): PlayerData =
        PlayerCachedData(
            nbt.getString(NBTFacts.COMPOUND_PLAYER_NAME),
            nbt.getUniqueId(NBTFacts.COMPOUND_PLAYER_UUID),
            nbt.getLong(NBTFacts.COMPOUND_PLAYER_BALANCE)
        )

    fun logToCompoundNBT(logBundle: LogBundle): CompoundNBT {
        val nbt = CompoundNBT()
        nbt.putString(NBTFacts.COMPOUND_LOG_OP_TYPE, logBundle.operationType.type.name)
        nbt.putLong(NBTFacts.COMPOUND_LOG_BALANCE, logBundle.operationType.balance)
        nbt.putUniqueId(NBTFacts.COMPOUND_LOG_PLAYER_UUID, logBundle.operationType.playerUUID)
        nbt.put(NBTFacts.COMPOUND_LOG_DATA, dataToCompoundNBT(logBundle.data))

        return nbt
    }

    fun logFromCompoundNBT(nbt: CompoundNBT): LogBundle =
        LogBundle(
            OperationType.DATA(
                OperationType.OpType.valueOf(nbt.getString(NBTFacts.COMPOUND_LOG_OP_TYPE)),
                nbt.getLong(NBTFacts.COMPOUND_LOG_BALANCE),
                nbt.getUniqueId(NBTFacts.COMPOUND_LOG_PLAYER_UUID)
            ),
            dataFromCompoundNBT(nbt.getCompound(NBTFacts.COMPOUND_LOG_DATA)) as PlayerBalanceData
        )
}