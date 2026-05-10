package com.miku.stellarannihilator;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrbitalStrikeManager {

    private static final Map<UUID, StrikeData> activeStrikes = new HashMap<>();

    public static void beginStrike(ServerWorld world, PlayerEntity player, BlockPos target) {
        StrikeData data = new StrikeData(target, world.getTime());
        activeStrikes.put(player.getUuid(), data);
        broadcastToAll(world, "§8The sky darkens...");
        world.setWeather(0, 6000, true, true);
    }

    public static void tick(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) return;

        long currentTime = world.getTime();

        activeStrikes.entrySet().removeIf(entry -> {
            StrikeData data = entry.getValue();
            long elapsed = currentTime - data.startTime;

            if (elapsed == 40) {
                broadcastToAll(world, "§e☀ The sun intensifies... something is charging.");
            } else if (elapsed == 80) {
                broadcastToAll(world, "§4⚡ §cRed rays pierce the sky! Energy converging...");
                spawnRedRays(world, data.target);
            } else if (elapsed == 140) {
                broadcastToAll(world, "§4§l💥 STELLAR ANNIHILATOR STRIKE!");
                executeStrike(world, data.target);
                world.setWeather(6000, 0, false, false);
                return true;
            }

            return false;
        });
    }

    private static void executeStrike(ServerWorld world, BlockPos target) {
        world.createExplosion(null,
                target.getX(), target.getY(), target.getZ(),
                30.0f, true, World.ExplosionSourceType.TNT);

        for (int i = 0; i < 8; i++) {
            double offsetX = (Math.random() - 0.5) * 20;
            double offsetZ = (Math.random() - 0.5) * 20;
            world.createExplosion(null,
                    target.getX() + offsetX,
                    target.getY(),
                    target.getZ() + offsetZ,
                    15.0f, true, World.ExplosionSourceType.TNT);
        }
    }

    private static void spawnRedRays(ServerWorld world, BlockPos target) {
        for (int i = 0; i < 50; i++) {
            double x = target.getX() + (Math.random() - 0.5) * 10;
            double z = target.getZ() + (Math.random() - 0.5) * 10;
            world.spawnParticles(ParticleTypes.FLAME,
                    x, target.getY() + 60, z,
                    5, 0, -1, 0, 0.5);
        }
    }

    private static void broadcastToAll(ServerWorld world, String message) {
        for (PlayerEntity player : world.getPlayers()) {
            player.sendMessage(Text.literal(message), false);
        }
    }

    static class StrikeData {
        BlockPos target;
        long startTime;

        StrikeData(BlockPos target, long startTime) {
            this.target = target;
            this.startTime = startTime;
        }
    }
}