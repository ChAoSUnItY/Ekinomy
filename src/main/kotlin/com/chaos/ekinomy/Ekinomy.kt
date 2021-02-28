package com.chaos.ekinomy

import com.chaos.ekinomy.command.CommandEkinomy
import com.chaos.ekinomy.data.OperationType
import com.chaos.ekinomy.data.PlayerCachedData
import com.chaos.ekinomy.handler.EkinomyManager
import com.chaos.ekinomy.handler.PacketManager
import com.chaos.ekinomy.util.config.Config
import com.chaos.ekinomy.util.nbt.EkinomyLevelData
import com.chaos.ekinomy.util.nbt.EkinomyLogLevelData
import com.chaos.ekinomy.web.EkinomyDashboard
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.world.IWorld
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(Ekinomy.MODID)
object Ekinomy {
    const val MODID = "ekinomy"
    const val MODNAME = "Ekinomy"

    val LOGGER: Logger = LogManager.getLogger(MODNAME)
    lateinit var dashboard: EkinomyDashboard

    init {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SERVER_SPEC)

        MOD_BUS.addListener(::setup)

        FORGE_BUS.addListener(::onLivingDeath)
        FORGE_BUS.addListener(::onPlayerJoin)
        FORGE_BUS.addListener(::onServerStart)
        FORGE_BUS.addListener(::onServerStop)
        FORGE_BUS.addListener(::onWorldLoad)
        FORGE_BUS.addListener(::onWorldSave)
        FORGE_BUS.addListener(::onWorldUnload)
        FORGE_BUS.addListener(::registerCommands)
    }

    private fun setup(event: FMLCommonSetupEvent) {
        PacketManager.init()
    }

    private fun onLivingDeath(event: LivingDeathEvent) {
        if (!event.entity.world.isRemote) {
            if (Config.SERVER.mobKillReward.get() == 0L ||
                Config.SERVER.playerKilledPenalty.get() == 0L
            ) return

            val killed = event.entity
            val killer = event.source.trueSource

            if (killer is ServerPlayerEntity)
                if (killed is ServerPlayerEntity) {
                    EkinomyManager.operate(
                        OperationType.PAY(
                            Config.SERVER.playerKilledPenalty.get(),
                            killed.uniqueID,
                            killer.uniqueID
                        )
                    )
                } else if (killed is IMob) {
                    EkinomyManager.operate(
                        OperationType.ADD(
                            Config.SERVER.mobKillReward.get(),
                            killer.uniqueID
                        )
                    )
                }
        }
    }

    private fun onPlayerJoin(event: EntityJoinWorldEvent) {
        val entity = event.entity

        if (entity is PlayerEntity && !entity.world.isRemote)
            EkinomyManager.getDataOrCreate(entity)
    }

    private fun onServerStart(event: FMLServerStartingEvent) {
        if (Config.SERVER.launchWeb.get()) {
            EkinomyDashboard.init()
        }
    }

    private fun onServerStop(event: FMLServerStoppingEvent) {
        if (Config.SERVER.launchWeb.get()) {
            EkinomyDashboard.stop()
        }
    }

    private fun onWorldLoad(event: WorldEvent.Load) {
        val world = event.world

        if (!world.isRemote && world is ServerWorld && world.dimensionKey == World.OVERWORLD) {
            val saver = EkinomyLevelData.getLevelData(event.world as ServerWorld)
            val logSaver = EkinomyLogLevelData.getLevelData(event.world as ServerWorld)

            EkinomyManager.init(saver.dataCollection, logSaver.logs)
        }
    }

    private fun onWorldSave(event: WorldEvent.Save) {
        markDataSaversDirty(event.world)
    }

    private fun onWorldUnload(event: WorldEvent.Unload) {
        markDataSaversDirty(event.world)
    }

    private fun markDataSaversDirty(world: IWorld) {
        if (!world.isRemote && world is ServerWorld && world.dimensionKey == World.OVERWORLD) {
            val saver = EkinomyLevelData.getLevelData(world)
            saver.dataCollection = EkinomyManager.getCachedDataCollection()
            saver.markDirty()

            val logSaver = EkinomyLogLevelData.getLevelData(world)
            logSaver.logs = EkinomyManager.getCachedDataCollection().flatMap(PlayerCachedData::logs)
                .plus(EkinomyManager.getPresavedLogCollection()).toMutableList()
            logSaver.markDirty()
        }
    }

    private fun registerCommands(event: RegisterCommandsEvent) {
        CommandEkinomy.register(event.dispatcher)
    }
}