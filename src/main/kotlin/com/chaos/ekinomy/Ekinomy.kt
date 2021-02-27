package com.chaos.ekinomy

import com.chaos.ekinomy.block.ModBlocks
import com.chaos.ekinomy.command.CommandEkinomy
import com.chaos.ekinomy.handler.EkinomyManager
import com.chaos.ekinomy.handler.PacketManager
import com.chaos.ekinomy.util.config.Config
import com.chaos.ekinomy.util.nbt.EkinomyLevelData
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
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

        MOD_BUS.addListener(::setup)

        FORGE_BUS.addListener(::onPlayerJoin)
        FORGE_BUS.addListener(::onWorldLoad)
        FORGE_BUS.addListener(::onWorldSave)
        FORGE_BUS.addListener(::onWorldUnload)
        FORGE_BUS.addListener(::registerCommands)
    }

    private fun setup(event: FMLCommonSetupEvent) {
        PacketManager.init()
    }

    private fun onPlayerJoin(event: EntityJoinWorldEvent) {
        val entity = event.entity

        if (entity is PlayerEntity && !entity.world.isRemote)
            EkinomyManager.getDataOrCreate(entity)
    }

    private fun onWorldLoad(event: WorldEvent.Load) {
        val world = event.world

        if (!world.isRemote && world is ServerWorld && world.dimensionKey == World.OVERWORLD) {
            val saver = EkinomyLevelData.getLevelData(event.world as ServerWorld)

            EkinomyManager.init(saver.dataCollection)
        }
    }

    private fun onWorldSave(event: WorldEvent.Save) {
        val world = event.world

        if (!world.isRemote && world is ServerWorld && world.dimensionKey == World.OVERWORLD) {
            val saver = EkinomyLevelData.getLevelData(event.world as ServerWorld)
            saver.dataCollection = EkinomyManager.getBalanceDataCollection()
            saver.markDirty()
        }
    }

    private fun onWorldUnload(event: WorldEvent.Unload) {
        val world = event.world

        if (!world.isRemote && world is ServerWorld && world.dimensionKey == World.OVERWORLD) {
            val saver = EkinomyLevelData.getLevelData(event.world as ServerWorld)
            saver.dataCollection = EkinomyManager.getBalanceDataCollection()
            saver.markDirty()
        }
    }

    private fun registerCommands(event: RegisterCommandsEvent) {
        CommandEkinomy.register(event.dispatcher)
    }
}