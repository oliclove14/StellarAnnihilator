package com.miku.stellarannihilator;

import com.miku.stellarannihilator.network.StrikePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrbitalStrikeManager {

    private static final Map<UUID, StrikeData> activeStrikes = new HashMap<>();
    private static final int STRIKE_DELAY = 300; // 15 Seconds

    public static void beginStrike(ServerWorld world, PlayerEntity player, BlockPos target) {
        StrikeData data = new StrikeData(target, world.getTime());
        activeStrikes.put(player.getUuid(), data);

        // Visual: Initial ping sound
        world.playSound(null, target, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.AMBIENT, 5.0f, 2.0f);

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

            // Phase 1: 0-10 Seconds (Targeting Rings)
            if (elapsed > 0 && elapsed < 200) {
                spawnRings(world, data.target, elapsed);
            }

            // Phase 2: 10-15 Seconds (Energy Build-up / Charging Beam)
            if (elapsed >= 200 && elapsed < STRIKE_DELAY) {
                spawnChargingBeam(world, data.target, elapsed);
                if (elapsed % 20 == 0) {
                    world.playSound(null, data.target, SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.AMBIENT, 2.0f, 0.5f + ((float)elapsed/300));
                }
            }

            // Chat Announcements
            if (elapsed == 20) {
                broadcastToAll(world, "§b[SYSTEM] §fOrbital coordinates locked...");
            } else if (elapsed == 200) {
                broadcastToAll(world, "§3[SYSTEM] §b§lORBITAL BEAM CHARGING...");
            } else if (elapsed == 280) {
                world.playSound(null, data.target, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.AMBIENT, 10.0f, 0.5f);
            }

            // Final Execution: 15 Seconds
            else if (elapsed >= STRIKE_DELAY) {
                executeStrike(world, data.target);
                return true;
            }

            return false;
        });
    }

    private static void spawnRings(ServerWorld world, BlockPos target, long elapsed) {
        double progress = (double) elapsed / 200.0;
        double radius = 10.0 * (1.0 - progress);
        double x = target.getX() + 0.5;
        double z = target.getZ() + 0.5;
        double y = target.getY() + 0.2;

        // Draw a cyan circular "target" on the ground
        for (int i = 0; i < 30; i++) {
            double angle = (2 * Math.PI * i) / 30;
            double px = x + radius * Math.cos(angle);
            double pz = z + radius * Math.sin(angle);

            // Sci-fi Blue/Cyan Particles
            world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, px, y, pz, 1, 0, 0.1, 0, 0.02);
            if (elapsed % 10 == 0) {
                world.spawnParticles(ParticleTypes.GLOW, px, y + 0.5, pz, 1, 0, 0.5, 0, 0.05);
            }
        }
    }

    private static void spawnChargingBeam(ServerWorld world, BlockPos target, long elapsed) {
        double x = target.getX() + 0.5;
        double z = target.getZ() + 0.5;

        // Thickening the beam as it charges
        float intensity = (float) (elapsed - 200) / 100f;
        int height = 320;

        for (int y = target.getY(); y < height; y += 4) {
            // Core Cyan Line
            world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, (int)(5 * intensity), 0.1, 1.0, 0.1, 0.01);

            // Electric arcs around the beam
            if (world.random.nextFloat() < 0.2f) {
                double offsetX = (world.random.nextDouble() - 0.5) * 2.0;
                double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;
                world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, x + offsetX, y, z + offsetZ, 1, 0, 0, 0, 0.1);
            }
        }
    }

    private static void executeStrike(ServerWorld world, BlockPos target) {
        double x = target.getX() + 0.5;
        double y = target.getY();
        double z = target.getZ() + 0.5;

        // 1. THE MASSIVE BEAM (Dense Cylinder)
        for (int i = 0; i < 320; i += 2) {
            // Inner Core (Pure White/Cyan)
            world.spawnParticles(ParticleTypes.SONIC_BOOM, x, y + 1, z, 1, 0, 0, 0, 0);

            // Outer Pillar (Large Cyan volume)
            world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y + i, z, 20, 1.2, 0.5, 1.2, 0.05);

            // Energy Sparks
            world.spawnParticles(ParticleTypes.GLOW, x, y + i, z, 5, 2.0, 1.0, 2.0, 0.1);
        }

        // 2. IMPACT VISUALS
        world.spawnParticles(ParticleTypes.SONIC_BOOM, x, y + 1, z, 5, 1.5, 1.5, 1.5, 0.1);
        world.playSound(null, target, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 20.0f, 0.5f);

        // 3. ACTUAL DAMAGE / EXPLOSION
        // Primary Explosion
        world.createExplosion(null, x, y, z, 45.0f, true, World.ExplosionSourceType.TNT);

        // Cluster Annihilation (Simulating "Stellar" impact)
        for (int i = 0; i < 15; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 40;
            double offsetZ = (world.random.nextDouble() - 0.5) * 40;
            world.createExplosion(null, x + offsetX, y, z + offsetZ, 15.0f, true, World.ExplosionSourceType.TNT);

            // Add blue fire to the craters
            BlockPos firePos = new BlockPos((int)(x + offsetX), (int)y, (int)(z + offsetZ));
            // You can add logic here to set blocks to soul fire if desired
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