package com.chaos.ekinomy.handler

import com.chaos.ekinomy.Ekinomy
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.simple.SimpleChannel

object PacketManager {
    private const val PROTOCAL_VERSION = "7"

    val INSTANCE: SimpleChannel = NetworkRegistry.newSimpleChannel(
        ResourceLocation(Ekinomy.MODID, "main"),
        { PROTOCAL_VERSION },
        { PROTOCAL_VERSION == it },
        { PROTOCAL_VERSION == it }
    )

    @JvmStatic
    fun init() {
        var id = 0

        Ekinomy.LOGGER.info("packet init here")


    }
}