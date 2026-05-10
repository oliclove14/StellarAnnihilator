package com.miku.stellarannihilator;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class ModItems {

    public static Item SOLAR_FRAGMENT;
    public static Item VOID_CORE;
    public static Item STELLAR_ANNIHILATOR_ITEM;

    public static void register() {
        SOLAR_FRAGMENT = Registry.register(Registries.ITEM,
                StellarAnnihilator.id("solar_fragment"),
                new Item(new Item.Settings().registryKey(
                        RegistryKey.of(RegistryKeys.ITEM, StellarAnnihilator.id("solar_fragment")))));

        VOID_CORE = Registry.register(Registries.ITEM,
                StellarAnnihilator.id("void_core"),
                new Item(new Item.Settings().registryKey(
                        RegistryKey.of(RegistryKeys.ITEM, StellarAnnihilator.id("void_core")))));

        STELLAR_ANNIHILATOR_ITEM = Registry.register(Registries.ITEM,
                StellarAnnihilator.id("stellar_annihilator"),
                new StellarAnnihilatorItem(new Item.Settings().maxCount(1).registryKey(
                        RegistryKey.of(RegistryKeys.ITEM, StellarAnnihilator.id("stellar_annihilator")))));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(SOLAR_FRAGMENT);
            entries.add(VOID_CORE);
            entries.add(STELLAR_ANNIHILATOR_ITEM);
        });

        StellarAnnihilator.LOGGER.info("Items registered!");
    }
}