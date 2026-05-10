package com.miku.stellarannihilator.client;

import com.miku.stellarannihilator.network.StrikePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class StellarAnnihilatorClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(StrikePacket.ID, (payload, context) -> {
			context.client().execute(() -> {
				SkyRenderer.beginStrike(payload.target());
			});
		});

		SkyRenderer.register();
	}
}