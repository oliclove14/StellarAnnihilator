package com.miku.stellarannihilator.client;

import net.minecraft.util.math.BlockPos;

public class SkyRenderer {

    public static boolean strikeActive = false;
    public static BlockPos strikeTarget = null;
    public static long strikeStartTime = 0;
    public static final long STRIKE_DURATION_MS = 15000;

    public static void register() {
        // Sky darkening via mixin - coming soon
    }

    public static void beginStrike(BlockPos target) {
        strikeActive = true;
        strikeTarget = target;
        strikeStartTime = System.currentTimeMillis();
    }
}