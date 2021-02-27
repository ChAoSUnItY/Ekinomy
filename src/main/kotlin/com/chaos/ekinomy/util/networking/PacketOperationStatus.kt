package com.chaos.ekinomy.util.networking

import net.minecraft.network.PacketBuffer
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

data class PacketOperationStatus(val status: Boolean, val balance: Long, val playerUUID: UUID?) {
    companion object : PacketBase<PacketOperationStatus> {
        override fun encode(packet: PacketOperationStatus, buffer: PacketBuffer) {
            buffer.writeBoolean(packet.status)
            buffer.writeLong(packet.balance)
            buffer.writeUniqueId(packet.playerUUID)
        }

        override fun decode(buffer: PacketBuffer): PacketOperationStatus =
            PacketOperationStatus(buffer.readBoolean(), buffer.readLong(), buffer.readUniqueId())

        override fun handle(packet: PacketOperationStatus, context: Supplier<NetworkEvent.Context>) {
            if (PacketBase.handleInSide(context, Dist.CLIENT)) {
                context.get().enqueueWork {
                    val isSelf = context.get().sender?.uniqueID == packet.playerUUID
                    val player = context.get().sender?.serverWorld?.getPlayerByUuid(packet.playerUUID) ?: context.get().sender

                    if (packet.status)
                        player?.sendStatusMessage(
                            StringTextComponent("Added ${packet.balance} to ${if (isSelf) "your" else player.displayName.string} balance").mergeStyle(
                                TextFormatting.GREEN
                            ),
                            false
                        )
                    else
                        player?.sendStatusMessage(
                            StringTextComponent("Could not add balance to ${if (isSelf) "your" else player.displayName.string} balance").mergeStyle(
                                TextFormatting.RED
                            ),
                            false
                        )
                }
            }
        }
    }
}
