package com.miku.stellarannihilator.client;

import com.miku.stellarannihilator.ModItems;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;

public class StaffRenderer {

    public static float currentRotation = 0f;
    public static float rotationSpeed = 1f;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (SkyRenderer.strikeActive) {
                float progress = (float)(System.currentTimeMillis() - SkyRenderer.strikeStartTime)
                        / SkyRenderer.STRIKE_DURATION_MS;
                rotationSpeed = 2f + (progress * 30f);
            } else {
                rotationSpeed = 0.5f;
            }
            currentRotation = (currentRotation + rotationSpeed) % 360f;
        });
    }
}