package com.miku.stellarannihilator;

import com.miku.stellarannihilator.network.StrikePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
    private static final int STRIKE_DELAY = 300; // 15 seconds

    public static void beginStrike(ServerWorld world, PlayerEntity player, BlockPos target) {
        StrikeData data = new StrikeData(target, world.getTime());
        activeStrikes.put(player.getUuid(), data);
        broadcastToAll(world, "§8☀ The sky darkens...");
        world.setWeather(0, 12000, true, false);
        for (var p : world.getPlayers()) {
            ServerPlayNetworking.send(p, new StrikePacket(target));
        }
    }

    public static void tick(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) return;
        long currentTime = world.getTime();

        activeStrikes.entrySet().removeIf(entry -> {
            StrikeData data = entry.getValue();
            long elapsed = currentTime - data.startTime;

            // Spawn rings every tick
            if (elapsed > 0 && elapsed < STRIKE_DELAY) {
                spawnRings(world, data.target, elapsed);
            }

            // Beam in final 40 ticks (2 seconds)
            if (elapsed >= STRIKE_DELAY - 40 && elapsed < STRIKE_DELAY) {
                spawnBeam(world, data.target);
            }

            if (elapsed == 20) {
                broadcastToAll(world, "§e☀ Energy gathering from the sun...");
            } else if (elapsed == 100) {
                broadcastToAll(world, "§4⚡ §cThe rings converge... take cover!");
            } else if (elapsed == STRIKE_DELAY - 40) {
                broadcastToAll(world, "§4§lFIRING!");
            } else if (elapsed == STRIKE_DELAY) {
                broadcastToAll(world, "§4§l💥 STELLAR ANNIHILATOR STRIKE!");
                executeStrike(world, data.target);
                world.setWeather(6000, 0, false, false);
                return true;
            }
            return false;
        });
    }

    private static void spawnRings(ServerWorld world, BlockPos target, long elapsed) {
        double progress = (double) elapsed / STRIKE_DELAY;

        // Start with huge radius and shrink to 0
        double maxRadius = 80.0;
        double radius = maxRadius * (1.0 - progress);

        // 5 rings at different heights
        int ringCount = 5;
        int points = 72; // more points = smoother ring

        for (int ring = 0; ring < ringCount; ring++) {
            double ringRadius = radius - (ring * 4.0);
            if (ringRadius <= 0) continue;

            // Rings float high up and descend toward target
            double ringY = target.getY() + 60 - (progress * 55);

            for (int j = 0; j < points; j++) {
                double angle = (2 * Math.PI * j) / points;
                double x = target.getX() + ringRadius * Math.cos(angle);
                double z = target.getZ() + ringRadius * Math.sin(angle);

                world.spawnParticles(ParticleTypes.FLAME,
                        x, ringY, z, 1, 0, 0, 0, 0);

                // Every 4th point add lava for thicker look
                if (j % 4 == 0) {
                    world.spawnParticles(ParticleTypes.LAVA,
                            x, ringY, z, 1, 0, 0, 0, 0);
                }
            }
        }
    }

    private static void spawnBeam(ServerWorld world, BlockPos target) {
        // Shoot particles from high up down to target
        for (int i = 0; i < 20; i++) {
            double height = 50 + Math.random() * 50;
            double spreadX = (Math.random() - 0.5) * 2;
            double spreadZ = (Math.random() - 0.5) * 2;

            world.spawnParticles(ParticleTypes.END_ROD,
                    target.getX() + spreadX,
                    target.getY() + height,
                    target.getZ() + spreadZ,
                    1, 0, -2, 0, 0.5);

            world.spawnParticles(ParticleTypes.FLAME,
                    target.getX() + spreadX,
                    target.getY() + height,
                    target.getZ() + spreadZ,
                    1, 0, -2, 0, 0.5);
        }
    }

    private static void executeStrike(ServerWorld world, BlockPos target) {
        // Final burst
        for (int i = 0; i < 200; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double r = Math.random() * 20;
            world.spawnParticles(ParticleTypes.FLAME,
                    target.getX() + r * Math.cos(angle),
                    target.getY() + Math.random() * 30,
                    target.getZ() + r * Math.sin(angle),
                    1, 0, 0.5, 0, 0.2);
        }

        world.createExplosion(null,
                target.getX(), target.getY(), target.getZ(),
                40.0f, true, World.ExplosionSourceType.TNT);

        for (int i = 0; i < 12; i++) {
            double offsetX = (Math.random() - 0.5) * 30;
            double offsetZ = (Math.random() - 0.5) * 30;
            world.createExplosion(null,
                    target.getX() + offsetX,
                    target.getY(),
                    target.getZ() + offsetZ,
                    20.0f, true, World.ExplosionSourceType.TNT);
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