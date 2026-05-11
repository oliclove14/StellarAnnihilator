package com.miku.stellarannihilator.client.mixin;

import com.miku.stellarannihilator.client.SkyRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public class LightmapMixin {

    @Inject(at = @At("HEAD"), method = "update", cancellable = true)
    private void onUpdate(float delta, CallbackInfo info) {
        if (SkyRenderer.isDark()) {
            info.cancel();
        }
    }
}