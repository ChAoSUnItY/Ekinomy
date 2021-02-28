package com.chaos.ekinomy.util.networking

import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

/**
 * Not Implemented Yet
 */
class RequestCachedDataListPacket {
    companion object : PacketBase<RequestCachedDataListPacket> {
        override fun encode(packet: RequestCachedDataListPacket, buffer: PacketBuffer) {
            TODO("Not yet implemented")
        }

        override fun decode(buffer: PacketBuffer): RequestCachedDataListPacket {
            TODO("Not yet implemented")
        }

        override fun handle(packet: RequestCachedDataListPacket, context: Supplier<NetworkEvent.Context>) {
            TODO("Not yet implemented")
        }

    }
}