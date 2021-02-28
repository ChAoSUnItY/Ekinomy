package com.chaos.ekinomy.handler

import com.chaos.ekinomy.Ekinomy
import com.chaos.ekinomy.util.networking.ExecuteOperationPacket
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

    fun init() {
        var id = 0

        INSTANCE.registerMessage(
            id++,
            ExecuteOperationPacket::class.java,
            ExecuteOperationPacket.Companion::encode,
            ExecuteOperationPacket.Companion::decode,
            ExecuteOperationPacket.Companion::handle
        )
    }
}