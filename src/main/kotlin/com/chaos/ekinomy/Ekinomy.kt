package com.chaos.ekinomy

import com.chaos.ekinomy.block.ModBlocks
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent
import org.apache.logging.log4j.Level
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
        ModBlocks.REGISTRY.register(MOD_BUS)

        MOD_BUS.addListener(Ekinomy::onClientSetup)
        FORGE_BUS.addListener(Ekinomy::onServerAboutToStart)
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
    }

    private fun onServerAboutToStart(event: FMLServerAboutToStartEvent) {
    }
}