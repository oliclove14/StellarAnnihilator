package com.miku.stellarannihilator.mixin;

import com.miku.stellarannihilator.OrbitalStrikeManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class ExampleMixin {
	@Inject(at = @At("HEAD"), method = "tick")
	private void onTick(CallbackInfo info) {
		OrbitalStrikeManager.tick((MinecraftServer)(Object)this);
	}
}