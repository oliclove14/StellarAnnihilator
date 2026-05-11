package com.miku.stellarannihilator.client;

import net.minecraft.util.math.BlockPos;

public class SkyRenderer {

    public static boolean strikeActive = false;
    public static BlockPos strikeTarget = null;
    public static long strikeStartTime = 0;
    public static final long STRIKE_DURATION_MS = 15000;

    public static void register() {
    }

    public static void beginStrike(BlockPos target) {
        strikeActive = true;
        strikeTarget = target;
        strikeStartTime = System.currentTimeMillis();

        // Auto stop after duration
        new Thread(() -> {
            try {
                Thread.sleep(STRIKE_DURATION_MS);
                strikeActive = false;
            } catch (InterruptedException ignored) {}
        }).start();
    }

    public static boolean isDark() {
        if (!strikeActive) return false;
        long elapsed = System.currentTimeMillis() - strikeStartTime;
        float progress = Math.min((float) elapsed / STRIKE_DURATION_MS, 1.0f);
        long blinkInterval = Math.max(80, 800 - (long)(progress * 720));
        return (elapsed / blinkInterval) % 2 == 0;
    }
}