package com.chaos.ekinomy.util.networking

import com.chaos.ekinomy.data.OperationType
import com.chaos.ekinomy.handler.EkinomyManager
import net.minecraft.network.PacketBuffer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class ExecuteOperationPacket(private val operation: OperationType) {
    companion object : PacketBase<ExecuteOperationPacket> {
        override fun encode(packet: ExecuteOperationPacket, buffer: PacketBuffer) {
            PacketHelper.writeOperation(packet.operation, buffer)
        }

        override fun decode(buffer: PacketBuffer): ExecuteOperationPacket {
            val operationType = PacketHelper.readOperation(buffer)

            return ExecuteOperationPacket(operationType)
        }

        override fun handle(packet: ExecuteOperationPacket, context: Supplier<NetworkEvent.Context>) {
            if (PacketBase.handleInSide(context, Dist.DEDICATED_SERVER)) {
                context.get().enqueueWork {
                    EkinomyManager.operate(packet.operation)
                }
            }
        }
    }
}