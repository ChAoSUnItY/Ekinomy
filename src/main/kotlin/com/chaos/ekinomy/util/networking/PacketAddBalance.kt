package com.chaos.ekinomy.util.networking

import com.chaos.ekinomy.data.OperationType
import com.chaos.ekinomy.handler.EkinomyManager
import com.chaos.ekinomy.handler.PacketManager
import net.minecraft.network.PacketBuffer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.PacketDistributor
import java.util.*
import java.util.function.Supplier

data class PacketAddBalance(private val balance: Long, private val playerUUID: UUID) {
    companion object : PacketBase<PacketAddBalance> {
        override fun encode(packet: PacketAddBalance, buffer: PacketBuffer) =
            PacketHelper.toPacket(packet.balance, packet.playerUUID, buffer)

        override fun decode(buffer: PacketBuffer): PacketAddBalance {
            val (balance, playerUUID) = PacketHelper.addTypeFromPacket(buffer)

            return PacketAddBalance(balance, playerUUID)
        }

        override fun handle(packet: PacketAddBalance, context: Supplier<NetworkEvent.Context>) {
            if (PacketBase.handleInSide(context, Dist.DEDICATED_SERVER)) {
                context.get().enqueueWork {
                    PacketManager.INSTANCE.send(
                        PacketDistributor.PLAYER.with { context.get().sender },
                        PacketOperationStatus(
                            EkinomyManager.operate(packet.asOperation()),
                            packet.balance,
                            packet.playerUUID
                        )
                    )
                }
            }
        }
    }

    fun asOperation(): OperationType.ADD = OperationType.ADD(balance, playerUUID)
}