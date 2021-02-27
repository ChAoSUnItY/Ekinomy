package com.chaos.ekinomy.util.networking

import net.minecraft.network.PacketBuffer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

interface PacketBase<T> {
    fun encode(packet: T, buffer: PacketBuffer)

    fun decode(buffer: PacketBuffer): T

    fun handle(packet: T, context: Supplier<NetworkEvent.Context>)

    companion object {
        fun handleInSide(context: Supplier<NetworkEvent.Context>, side: Dist): Boolean {
            var pass = false
            when (side) {
                Dist.CLIENT -> pass = if (context.get().direction.receptionSide.isServer) {
                    context.get().packetHandled = true
                    false
                } else {
                    true
                }
                Dist.DEDICATED_SERVER -> pass = if (context.get().direction.receptionSide.isClient) {
                    context.get().packetHandled = true
                    false
                } else {
                    true
                }
                else -> {
                }
            }
            return pass
        }
    }
}