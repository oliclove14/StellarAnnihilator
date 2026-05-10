package com.miku.stellarannihilator;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StellarAnnihilatorItem extends Item {

    public StellarAnnihilatorItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        BlockPos target = player.getBlockPos();
        player.sendMessage(Text.literal("§4☀ §cTarget marked! Orbital strike incoming..."), false);
        OrbitalStrikeManager.beginStrike((ServerWorld) world, player, target);

        return ActionResult.SUCCESS;
    }
}