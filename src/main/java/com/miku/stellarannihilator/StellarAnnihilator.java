package com.miku.stellarannihilator;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StellarAnnihilator implements ModInitializer {
	public static final String MOD_ID = "stellarannihilator";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("StellarAnnihilator initializing!");
		com.miku.stellarannihilator.network.StrikePacket.register();
		ModItems.register();
		ModLoot.register();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}