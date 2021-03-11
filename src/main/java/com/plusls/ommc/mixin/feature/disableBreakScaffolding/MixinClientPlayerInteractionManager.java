package com.plusls.ommc.mixin.feature.disableBreakScaffolding;

import com.plusls.ommc.config.Configs;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "attackBlock", at = @At(value = "HEAD"), cancellable = true)
    private void checkScaffolding(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        World world = MinecraftClient.getInstance().world;
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (Configs.FeatureToggle.DISABLE_BREAK_SCAFFOLDING.getBooleanValue() &&
                world != null && world.getBlockState(pos).isOf(Blocks.SCAFFOLDING) &&
                player != null) {
            String itemId = Registry.ITEM.getId(player.getMainHandStack().getItem()).toString();
            for (String whitelistId : Configs.Lists.BREAK_SCAFFOLDING_WHITELIST.getStrings()) {
                if (itemId.contains(whitelistId)) {
                    return;
                }
            }
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "HEAD"), cancellable = true)
    private void checkScaffolding1(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        World world = MinecraftClient.getInstance().world;
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (Configs.FeatureToggle.DISABLE_BREAK_SCAFFOLDING.getBooleanValue() &&
                world != null && world.getBlockState(pos).isOf(Blocks.SCAFFOLDING) &&
                player != null) {
            String itemId = Registry.ITEM.getId(player.getMainHandStack().getItem()).toString();
            for (String whitelistId : Configs.Lists.BREAK_SCAFFOLDING_WHITELIST.getStrings()) {
                if (itemId.contains(whitelistId)) {
                    return;
                }
            }
            cir.setReturnValue(false);
        }
    }

}
