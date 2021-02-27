package com.chaos.ekinomy.handler

import com.chaos.ekinomy.Ekinomy
import com.chaos.ekinomy.util.networking.PacketAddBalance
import com.chaos.ekinomy.util.networking.PacketOperationStatus
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.simple.SimpleChannel
import java.util.function.Supplier

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