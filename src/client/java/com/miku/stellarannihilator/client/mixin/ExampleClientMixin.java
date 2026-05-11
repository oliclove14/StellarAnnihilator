package com.miku.stellarannihilator.client.mixin;

import com.miku.stellarannihilator.client.SkyRenderer;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class ExampleClientMixin {

	@Inject(at = @At("HEAD"), method = "renderSky", cancellable = true)
	private void onRenderSky(CallbackInfo info) {
		if (SkyRenderer.isDark()) {
			info.cancel();
		}
	}
}