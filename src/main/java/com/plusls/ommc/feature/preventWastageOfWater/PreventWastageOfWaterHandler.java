package com.plusls.ommc.feature.preventWastageOfWater;

import com.plusls.ommc.config.Configs;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class PreventWastageOfWaterHandler implements UseItemCallback {
    public static void init() {
        PreventWastageOfWaterHandler handler = new PreventWastageOfWaterHandler();
        UseItemCallback.EVENT.register(handler);
    }

    @Override
    public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        return (Configs.FeatureToggle.PREVENT_WASTAGE_OF_WATER.getBooleanValue()
                && world.isClient
                && player.getStackInHand(hand).getItem() == Items.WATER_BUCKET
                && world.getDimension().isUltrawarm())
                ? TypedActionResult.fail(ItemStack.EMPTY)
                : TypedActionResult.pass(ItemStack.EMPTY);
    }
}
