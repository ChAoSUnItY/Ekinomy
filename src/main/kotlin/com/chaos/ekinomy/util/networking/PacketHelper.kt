package com.chaos.ekinomy.util.networking

import com.chaos.ekinomy.data.OperationType
import net.minecraft.network.PacketBuffer
import java.util.*

object PacketHelper {
    fun toPacket(balance: Long, playerUUID: UUID, buffer: PacketBuffer) {
        buffer.writeLong(balance)
        buffer.writeUniqueId(playerUUID)
    }

    fun addTypeFromPacket(buffer: PacketBuffer): OperationType.ADD {
        val balance = buffer.readLong()
        val playerUUID = buffer.readUniqueId()

        return OperationType.ADD(balance, playerUUID)
    }
}