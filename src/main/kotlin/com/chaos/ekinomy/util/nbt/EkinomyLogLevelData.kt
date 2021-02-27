package com.chaos.ekinomy.util.nbt

import com.chaos.ekinomy.Ekinomy
import com.chaos.ekinomy.data.LogBundle
import com.chaos.ekinomy.data.PlayerCachedData
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.world.server.ServerWorld
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.util.Constants
import java.util.function.Supplier

class EkinomyLogLevelData(name: String? = null) : WorldSavedData(name ?: "${Ekinomy.MODID}-logs"),
    Supplier<EkinomyLogLevelData> {
    var logs: MutableList<LogBundle> = mutableListOf()

    companion object {
        fun getLevelData(world: ServerWorld): EkinomyLogLevelData {
            val storage = world.savedData
            val sup = EkinomyLogLevelData()

            return storage.getOrCreate(sup, "${Ekinomy.MODID}-logs")
        }
    }

    override fun read(nbt: CompoundNBT) {
        val list = nbt.getList(NBTFacts.EKINOMY_LOG_LEVEL_DATA_LOG_LIST, Constants.NBT.TAG_COMPOUND)

        logs.addAll(list.map { it as CompoundNBT }.map(NBTHelper::logFromCompoundNBT))
    }

    override fun write(compound: CompoundNBT): CompoundNBT {
        val list = ListNBT()

        list.addAll(logs.map(NBTHelper::logToCompoundNBT))
        compound.put(NBTFacts.EKINOMY_LOG_LEVEL_DATA_LOG_LIST, list)

        return compound
    }

    override fun get(): EkinomyLogLevelData = this
}