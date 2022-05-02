package com.plusls.ommc.mixin.feature.disableBreakBlock;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "startDestroyBlock", at = @At(value = "HEAD"), cancellable = true)
    private void disableBreakBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (shouldDisableBreakBlock(pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueDestroyBlock", at = @At(value = "HEAD"), cancellable = true)
    private void disableBreakBlock1(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (shouldDisableBreakBlock(pos)) {
            cir.setReturnValue(false);
        }
    }

    private boolean shouldDisableBreakBlock(BlockPos pos) {
        Level world = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if (Configs.FeatureToggle.DISABLE_BREAK_BLOCK.getBooleanValue() &&
                world != null && player != null) {
            String blockId = Registry.BLOCK.getKey(world.getBlockState(pos).getBlock()).toString();
            String blockName = world.getBlockState(pos).getBlock().getName().getString();
            return Configs.Lists.BREAK_BLOCK_BLACKLIST.getStrings().stream().anyMatch(s -> blockId.contains(s) || blockName.contains(s));
        }
        return false;
    }

}

