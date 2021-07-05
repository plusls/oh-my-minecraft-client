package com.plusls.ommc.mixin.feature.disableBreakBlock;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "attackBlock", at = @At(value = "HEAD"), cancellable = true)
    private void disableBreakScaffolding(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (shouldDisableBreakBlock(pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "HEAD"), cancellable = true)
    private void disableBreakScaffolding1(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (shouldDisableBreakBlock(pos)) {
            cir.setReturnValue(false);
        }
    }

    private boolean shouldDisableBreakBlock(BlockPos pos) {
        World world = MinecraftClient.getInstance().world;
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (Configs.FeatureToggle.DISABLE_BREAK_BLOCK.getBooleanValue() &&
                world != null && player != null) {
            String blockId = Registry.BLOCK.getId(world.getBlockState(pos).getBlock()).toString();
            String blockName = world.getBlockState(pos).getBlock().getName().getString();
            return Configs.Lists.BREAK_BLOCK_BLACKLIST.getStrings().stream().anyMatch(s -> blockId.contains(s) || blockName.contains(s));
        }
        return false;
    }

}

