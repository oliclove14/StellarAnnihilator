package com.miku.stellarannihilator.network;

import com.miku.stellarannihilator.StellarAnnihilator;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.RegistryByteBuf;

public record StrikePacket(BlockPos target) implements CustomPayload {

    public static final CustomPayload.Id<StrikePacket> ID =
            new CustomPayload.Id<>(StellarAnnihilator.id("strike_packet"));

    public static final PacketCodec<RegistryByteBuf, StrikePacket> CODEC =
            PacketCodec.of(
                    (packet, buf) -> buf.writeBlockPos(packet.target()),
                    buf -> new StrikePacket(buf.readBlockPos())
            );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
    }
}