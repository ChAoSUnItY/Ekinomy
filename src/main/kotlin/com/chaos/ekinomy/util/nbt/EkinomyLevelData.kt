package com.chaos.ekinomy.util.nbt

import com.chaos.ekinomy.Ekinomy
import com.chaos.ekinomy.data.PlayerCachedData
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.world.server.ServerWorld
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.util.Constants
import java.util.function.Supplier

class EkinomyLevelData(name: String? = null) : WorldSavedData(name ?: Ekinomy.MODID), Supplier<EkinomyLevelData> {
    var dataCollection: MutableList<PlayerCachedData> = mutableListOf()

    companion object {
        fun getLevelData(world: ServerWorld): EkinomyLevelData {
            val storage = world.savedData
            val sup = EkinomyLevelData()

            return storage.getOrCreate(sup, Ekinomy.MODID)
        }
    }

    override fun read(nbt: CompoundNBT) {
        val list = nbt.getList(NBTFacts.EKINOMY_LEVEL_DATA_LIST, Constants.NBT.TAG_COMPOUND)
        list.forEach {
            dataCollection.add(NBTHelper.dataFromCompoundNBT(it as CompoundNBT) as PlayerCachedData)
        }
    }

    override fun write(compound: CompoundNBT): CompoundNBT {
        val list = ListNBT()
        dataCollection.forEach { list.add(NBTHelper.dataToCompoundNBT(it)) }
        compound.put(NBTFacts.EKINOMY_LEVEL_DATA_LIST, list)

        return compound
    }

    override fun get(): EkinomyLevelData = this
}