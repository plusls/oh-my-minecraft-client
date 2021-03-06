package com.plusls.ommc.mixin.feature.emptyHandBreakScaffolding;

import com.plusls.ommc.config.Configs;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    @Inject(method = "attackBlock", at = @At(value = "HEAD"), cancellable = true)
    private void fuck(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        World world = MinecraftClient.getInstance().world;
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (Configs.FeatureToggle.EMPTY_HAND_BREAK_SCAFFOLDING.getBooleanValue() &&
                world != null && world.getBlockState(pos).isOf(Blocks.SCAFFOLDING) &&
                player != null && player.getMainHandStack().getItem() != Items.AIR) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "HEAD"), cancellable = true)
    private void fuck1(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        World world = MinecraftClient.getInstance().world;
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (Configs.FeatureToggle.EMPTY_HAND_BREAK_SCAFFOLDING.getBooleanValue() &&
                world != null && world.getBlockState(pos).isOf(Blocks.SCAFFOLDING) &&
                player != null &&
                player.getMainHandStack().getItem() != Items.AIR) {
            cir.setReturnValue(false);
        }
    }

}
