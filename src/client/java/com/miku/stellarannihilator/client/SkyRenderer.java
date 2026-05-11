package com.miku.stellarannihilator.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class SkyRenderer {

    public static boolean strikeActive = false;
    public static BlockPos strikeTarget = null;
    public static long strikeStartTime = 0;

    public static final long STRIKE_DURATION_MS = 15000;

    private static final Random random = new Random();

    public static void register() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (!strikeActive) return;
            if (client.world == null) return;
            if (strikeTarget == null) return;

            long elapsed = System.currentTimeMillis() - strikeStartTime;

            if (elapsed >= STRIKE_DURATION_MS) {
                strikeActive = false;
                return;
            }

            spawnOrbitalBeam(client.world);
        });
    }

    private static void spawnOrbitalBeam(ClientWorld world) {

        double x = strikeTarget.getX() + 0.5;
        double y = strikeTarget.getY();
        double z = strikeTarget.getZ() + 0.5;

        int beamHeight = 350;

        for (int i = 0; i < beamHeight; i += 2) {

            double beamY = y + i;

            // MAIN CORE
            MinecraftClient.getInstance().world.addParticleClient(
                    ParticleTypes.END_ROD,
                    x,
                    beamY,
                    z,
                    0.0,
                    -0.03,
                    0.0
            );

            // CYAN ENERGY GLOW
            for (int j = 0; j < 3; j++) {

                MinecraftClient.getInstance().world.addParticleClient(
                        ParticleTypes.GLOW,
                        x + (random.nextDouble() - 0.5) * 1.5,
                        beamY,
                        z + (random.nextDouble() - 0.5) * 1.5,
                        0.0,
                        0.02,
                        0.0
                );
            }

            // ELECTRIC SPARKS
            if (random.nextInt(3) == 0) {

                MinecraftClient.getInstance().world.addParticleClient(
                        ParticleTypes.ELECTRIC_SPARK,
                        x + (random.nextDouble() - 0.5),
                        beamY,
                        z + (random.nextDouble() - 0.5),
                        0.0,
                        0.15,
                        0.0
                );
            }
        }

        // GROUND ENERGY FOG
        for (int i = 0; i < 10; i++) {

            MinecraftClient.getInstance().world.addParticleClient(
                    ParticleTypes.SMOKE,
                    x + (random.nextDouble() - 0.5) * 3,
                    y,
                    z + (random.nextDouble() - 0.5) * 3,
                    0.0,
                    0.03,
                    0.0
            );
        }
    }

    public static void beginStrike(BlockPos target) {

        strikeActive = true;
        strikeTarget = target;
        strikeStartTime = System.currentTimeMillis();
    }

    public static boolean isDark() {

        if (!strikeActive) return false;

        long elapsed = System.currentTimeMillis() - strikeStartTime;

        float progress = Math.min(
                (float) elapsed / STRIKE_DURATION_MS,
                1.0f
        );

        long blinkInterval = Math.max(
                80,
                800 - (long) (progress * 720)
        );

        return (elapsed / blinkInterval) % 2 == 0;
    }
}