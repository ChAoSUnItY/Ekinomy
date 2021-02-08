package com.chaos.ekinomy

import com.chaos.ekinomy.block.ModBlocks
import com.chaos.ekinomy.data.PlayerBalanceData
import com.chaos.ekinomy.handler.EkinomyManager
import com.chaos.ekinomy.util.config.Config
import com.chaos.ekinomy.util.nbt.EkinomyLevelData
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(Ekinomy.MODID)
object Ekinomy {
    const val MODID = "ekinomy"
    const val MODNAME = "Ekinomy"

    val LOGGER: Logger = LogManager.getLogger(MODNAME)

    init {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SERVER_SPEC)

        ModBlocks.REGISTRY.register(MOD_BUS)

        FORGE_BUS.addListener(::onPlayerJoin)
        FORGE_BUS.addListener(::onWorldLoad)
        FORGE_BUS.addListener(::onWorldSave)
        FORGE_BUS.addListener(::onWorldUnload)
        FORGE_BUS.addListener(::onServerAboutToStart)
    }

    private fun onPlayerJoin(event: EntityJoinWorldEvent) {
        val entity = event.entity

        if (entity is PlayerEntity)
            if (!EkinomyManager.has(entity))
                EkinomyManager.addData(
                    PlayerBalanceData(
                        entity.name.string,
                        entity.uniqueID,
                        Config.SERVER.initialBalance.get()
                    )
                )
    }

    private fun onWorldLoad(event: WorldEvent.Load) {
        if (!event.world.isRemote && event.world is ServerWorld) {
            val saver = EkinomyLevelData.getLevelData(event.world as ServerWorld)

            EkinomyManager.init(saver.dataCollection)
        }
    }

    private fun onWorldSave(event: WorldEvent.Save) {
        if (!event.world.isRemote && event.world is ServerWorld) {
            val saver = EkinomyLevelData.getLevelData(event.world as ServerWorld)
            saver.dataCollection = EkinomyManager.getBalanceDataCollection()
            saver.markDirty()
        }
    }

    private fun onWorldUnload(event: WorldEvent.Unload) {
        if (!event.world.isRemote && event.world is ServerWorld) {
            val saver = EkinomyLevelData.getLevelData(event.world as ServerWorld)
            saver.dataCollection = EkinomyManager.getBalanceDataCollection()
            saver.markDirty()
        }
    }

    private fun onServerAboutToStart(event: FMLServerAboutToStartEvent) {
    }
}