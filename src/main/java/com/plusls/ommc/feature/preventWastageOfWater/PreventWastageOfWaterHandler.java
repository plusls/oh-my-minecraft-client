package com.plusls.ommc.feature.preventWastageOfWater;

import com.plusls.ommc.config.Configs;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class PreventWastageOfWaterHandler implements UseBlockCallback, UseItemCallback {
    public static void init() {
        PreventWastageOfWaterHandler handler = new PreventWastageOfWaterHandler();
        UseBlockCallback.EVENT.register(handler);
        UseItemCallback.EVENT.register(handler);
    }

    private static boolean checkCommon(PlayerEntity player, World world, Hand hand) {
        return Configs.FeatureToggle.PREVENT_WASTAGE_OF_WATER.getBooleanValue()
                && world.isClient
                && player.getStackInHand(hand).getItem() == Items.WATER_BUCKET
                && world.getDimension().isUltrawarm();
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        return checkCommon(player, world, hand)
                && !world.getBlockState(hitResult.getBlockPos()).onUse(world, player, hand, hitResult).isAccepted()
                ? ActionResult.FAIL : ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        return checkCommon(player, world, hand) ? TypedActionResult.fail(ItemStack.EMPTY) : TypedActionResult.pass(ItemStack.EMPTY);
    }
}
