package com.miku.stellarannihilator;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

public class ModLoot {

    private static final Identifier VILLAGE_WEAPONSMITH =
            Identifier.of("minecraft", "chests/village/village_weaponsmith");
    private static final Identifier VILLAGE_PLAINS_HOUSE =
            Identifier.of("minecraft", "chests/village/village_plains_house");

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

            if (key.getValue().equals(VILLAGE_WEAPONSMITH)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(ModItems.SOLAR_FRAGMENT)
                                .apply(SetCountLootFunction.builder(
                                        ConstantLootNumberProvider.create(1))))
                        .conditionally(RandomChanceLootCondition.builder(0.08f))
                        .build());
            }

            if (key.getValue().equals(VILLAGE_PLAINS_HOUSE)) {
                tableBuilder.pool(LootPool.builder()
                        .with(ItemEntry.builder(ModItems.VOID_CORE)
                                .apply(SetCountLootFunction.builder(
                                        ConstantLootNumberProvider.create(1))))
                        .conditionally(RandomChanceLootCondition.builder(0.08f))
                        .build());
            }
        });
    }
}