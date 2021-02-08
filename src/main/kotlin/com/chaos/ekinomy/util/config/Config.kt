package com.chaos.ekinomy.util.config

import com.chaos.ekinomy.Ekinomy
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = Ekinomy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
object Config {
    val SERVER: EkinomyConfig
    val SERVER_SPEC: ForgeConfigSpec

    init {
        val specPair = ForgeConfigSpec.Builder().configure(::EkinomyConfig)
        SERVER_SPEC = specPair.right
        SERVER = specPair.left
    }
}