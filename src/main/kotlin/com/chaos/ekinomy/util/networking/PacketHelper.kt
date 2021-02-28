package com.chaos.ekinomy.util.networking

import com.chaos.ekinomy.data.OperationType
import net.minecraft.network.PacketBuffer
import java.util.*

object PacketHelper {
    fun writeOperation(operationType: OperationType, buffer: PacketBuffer) {
        when (operationType) {
            is OperationType.DATA -> writeOperationData(operationType, buffer)
            is OperationType.ADD -> writeOperationAdd(operationType, buffer)
            is OperationType.SUB -> writeOperationSub(operationType, buffer)
            is OperationType.PAY -> writeOperationPay(operationType, buffer)
            is OperationType.SET -> writeOperationSet(operationType, buffer)
            is OperationType.RESET -> writeOperationReset(operationType, buffer)
        }
    }

    private fun writeOperationTargetType(operationType: OperationType, buffer: PacketBuffer) {
        buffer.writeEnumValue(operationType.type)
    }

    private fun writeOperationBaseData(operationType: OperationType, buffer: PacketBuffer) {
        buffer.writeLong(operationType.balance)
        buffer.writeUniqueId(operationType.playerUUID)
    }

    private fun writeOperationData(operationType: OperationType.DATA, buffer: PacketBuffer) {
        writeOperationTargetType(operationType, buffer)
        buffer.writeEnumValue(operationType.targetType)
        writeOperationBaseData(operationType, buffer)
    }

    private fun writeOperationAdd(operationType: OperationType.ADD, buffer: PacketBuffer) {
        writeOperationTargetType(operationType, buffer)
        writeOperationBaseData(operationType, buffer)
    }

    private fun writeOperationSub(operationType: OperationType.SUB, buffer: PacketBuffer) {
        writeOperationTargetType(operationType, buffer)
        writeOperationBaseData(operationType, buffer)
    }

    private fun writeOperationPay(operationType: OperationType.PAY, buffer: PacketBuffer) {
        writeOperationTargetType(operationType, buffer)
        writeOperationBaseData(operationType, buffer)
        buffer.writeUniqueId(operationType.targetPlayerUUID)
    }

    private fun writeOperationSet(operationType: OperationType.SET, buffer: PacketBuffer) {
        writeOperationTargetType(operationType, buffer)
        writeOperationBaseData(operationType, buffer)
    }

    private fun writeOperationReset(operationType: OperationType.RESET, buffer: PacketBuffer) {
        writeOperationTargetType(operationType, buffer)
        writeOperationBaseData(operationType, buffer)
    }

    fun readOperation(buffer: PacketBuffer): OperationType {
        return when (buffer.readEnumValue(OperationType.OpType::class.java)) {
            OperationType.OpType.DATA -> readOperationData(buffer)
            OperationType.OpType.ADD -> readOperationAdd(buffer)
            OperationType.OpType.SUB -> readOperationSub(buffer)
            OperationType.OpType.PAY -> readOperationPay(buffer)
            OperationType.OpType.SET -> readOperationSet(buffer)
            OperationType.OpType.RESET -> readOperationReset(buffer)
            else -> throw IllegalArgumentException("Cannot read operation type because it's a null value.")
        }
    }

    private fun readOperationBaseData(buffer: PacketBuffer): Pair<Long, UUID> {
        val balance = buffer.readLong()
        val playerUUId = buffer.readUniqueId()

        return Pair(balance, playerUUId)
    }

    private fun readOperationData(buffer: PacketBuffer): OperationType.DATA {
        val targetType = buffer.readEnumValue(OperationType.OpType::class.java)
        val (balance, playerUUID) = readOperationBaseData(buffer)

        return OperationType.DATA(targetType, balance, playerUUID)
    }

    private fun readOperationAdd(buffer: PacketBuffer): OperationType.ADD {
        val (balance, playerUUID) = readOperationBaseData(buffer)

        return OperationType.ADD(balance, playerUUID)
    }

    private fun readOperationSub(buffer: PacketBuffer): OperationType.SUB {
        val (balance, playerUUID) = readOperationBaseData(buffer)

        return OperationType.SUB(balance, playerUUID)
    }

    private fun readOperationPay(buffer: PacketBuffer): OperationType.PAY {
        val (balance, playerUUID) = readOperationBaseData(buffer)
        val targetPlayerUUID = buffer.readUniqueId()

        return OperationType.PAY(balance, playerUUID, targetPlayerUUID)
    }

    private fun readOperationSet(buffer: PacketBuffer): OperationType.SET {
        val (balance, playerUUID) = readOperationBaseData(buffer)

        return OperationType.SET(balance, playerUUID)
    }

    private fun readOperationReset(buffer: PacketBuffer): OperationType.RESET {
        val (balance, playerUUID) = readOperationBaseData(buffer)

        return OperationType.RESET(playerUUID, balance)
    }
}