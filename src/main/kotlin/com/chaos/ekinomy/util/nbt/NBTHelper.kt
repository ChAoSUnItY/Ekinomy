package com.chaos.ekinomy.util.nbt

import com.chaos.ekinomy.data.PlayerBalanceData
import net.minecraft.nbt.CompoundNBT

object NBTHelper {
    fun toCompoundNBT(data: PlayerBalanceData): CompoundNBT {
        val nbt = CompoundNBT()
        nbt.putString(NBTFacts.COMPOUND_PLAYER_NAME, data.playerName)
        nbt.putUniqueId(NBTFacts.COMPOUND_PLAYER_UUID, data.playerUUID)
        nbt.putLong(NBTFacts.COMPOUND_PLAYER_BALANCE, data.balance)

        return nbt
    }

    fun fromCompoundNBT(nbt: CompoundNBT): PlayerBalanceData =
        PlayerBalanceData(
            nbt.getString(NBTFacts.COMPOUND_PLAYER_NAME),
            nbt.getUniqueId(NBTFacts.COMPOUND_PLAYER_UUID),
            nbt.getLong(NBTFacts.COMPOUND_PLAYER_BALANCE)
        )
}