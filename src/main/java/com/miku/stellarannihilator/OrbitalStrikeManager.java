package com.miku.stellarannihilator;

import com.miku.stellarannihilator.network.StrikePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
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
    private static final int STRIKE_DELAY = 300;

    public static void beginStrike(ServerWorld world, PlayerEntity player, BlockPos target) {
        StrikeData data = new StrikeData(target, world.getTime());
        activeStrikes.put(player.getUuid(), data);
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

            if (elapsed > 0 && elapsed < STRIKE_DELAY) {
                spawnRings(world, data.target, elapsed);
            }

            if (elapsed >= STRIKE_DELAY * 0.6 && elapsed < STRIKE_DELAY) {
                spawnBeam(world, data.target);
            }

            if (elapsed == 20) {
                broadcastToAll(world, "§4☀ §cOrbital strike incoming...");
            } else if (elapsed == 100) {
                broadcastToAll(world, "§4⚡ §c§lTAKE COVER!");
            } else if (elapsed == STRIKE_DELAY) {
                executeStrike(world, data.target);
                world.setWeather(6000, 0, false, false);
                return true;
            }

            return false;
        });
    }

    private static void spawnRings(ServerWorld world, BlockPos target, long elapsed) {
        double progress = (double) elapsed / STRIKE_DELAY;
        double maxRadius = 120.0;
        double radius = maxRadius * (1.0 - progress);
        int ringCount = 6;
        int points = 120;

        for (int ring = 0; ring < ringCount; ring++) {
            double ringRadius = radius - (ring * 5.0);
            if (ringRadius <= 0) continue;

            double ringY = target.getY() + 80 - (progress * 75);

            for (int j = 0; j < points; j++) {
                double angle = (2 * Math.PI * j) / points;
                double x = target.getX() + ringRadius * Math.cos(angle);
                double z = target.getZ() + ringRadius * Math.sin(angle);

                world.spawnParticles(ParticleTypes.FLAME, x, ringY, z, 1, 0, 0, 0, 0);

                if (j % 3 == 0) {
                    world.spawnParticles(ParticleTypes.END_ROD,
                            target.getX() + (ringRadius - 1) * Math.cos(angle),
                            ringY,
                            target.getZ() + (ringRadius - 1) * Math.sin(angle),
                            1, 0, 0, 0, 0);
                }
            }

            if (elapsed % 3 == 0) {
                double sparkAngle = Math.random() * 2 * Math.PI;
                world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                        target.getX() + ringRadius * Math.cos(sparkAngle),
                        ringY,
                        target.getZ() + ringRadius * Math.sin(sparkAngle),
                        3, 0.5, 0.5, 0.5, 0.1);
            }
        }

        if (progress > 0.4) {
            double pillarHeight = (progress - 0.4) / 0.6 * 60;
            for (int h = 0; h < (int) pillarHeight; h += 2) {
                world.spawnParticles(ParticleTypes.FLAME,
                        target.getX(), target.getY() + h, target.getZ(),
                        2, 0.3, 0, 0.3, 0);
            }
        }
    }

    private static void spawnBeam(ServerWorld world, BlockPos target) {
        int maxHeight = 320;

        for (int y = target.getY(); y < maxHeight; y += 1) {
            world.spawnParticles(ParticleTypes.END_ROD,
                    target.getX(), y, target.getZ(),
                    2, 0.08, 0, 0.08, 0);

            world.spawnParticles(ParticleTypes.FLAME,
                    target.getX(), y, target.getZ(),
                    2, 0.2, 0, 0.2, 0);

            if (y % 2 == 0) {
                for (int r = 0; r < 12; r++) {
                    double angle = (2 * Math.PI * r) / 12;
                    double radius = 2.0;
                    world.spawnParticles(ParticleTypes.FLAME,
                            target.getX() + radius * Math.cos(angle),
                            y,
                            target.getZ() + radius * Math.sin(angle),
                            1, 0, 0, 0, 0);
                }
            }

            if (y % 8 == 0) {
                world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                        target.getX(), y, target.getZ(),
                        3, 0.5, 0.1, 0.5, 0.2);
            }
        }

        for (int r = 0; r < 48; r++) {
            double angle = (2 * Math.PI * r) / 48;
            double radius = 4.0 + Math.random() * 3;
            world.spawnParticles(ParticleTypes.FLAME,
                    target.getX() + radius * Math.cos(angle),
                    target.getY() + 0.5,
                    target.getZ() + radius * Math.sin(angle),
                    1, 0, 0.1, 0, 0);
        }
    }

    private static void executeStrike(ServerWorld world, BlockPos target) {
        for (int i = 0; i < 300; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double r = Math.random() * 25;
            world.spawnParticles(ParticleTypes.FLAME,
                    target.getX() + r * Math.cos(angle),
                    target.getY() + Math.random() * 40,
                    target.getZ() + r * Math.sin(angle),
                    1, 0, 0.3, 0, 0);
            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                    target.getX() + r * Math.cos(angle),
                    target.getY() + Math.random() * 20,
                    target.getZ() + r * Math.sin(angle),
                    1, 0.5, 0.5, 0.5, 0.2);
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