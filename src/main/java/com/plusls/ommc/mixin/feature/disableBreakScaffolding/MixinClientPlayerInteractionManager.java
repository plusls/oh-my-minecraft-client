package com.plusls.ommc.mixin.feature.disableBreakScaffolding;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "startDestroyBlock", at = @At(value = "HEAD"), cancellable = true)
    private void disableBreakScaffolding(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (shouldDisableBreakScaffolding(pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueDestroyBlock", at = @At(value = "HEAD"), cancellable = true)
    private void disableBreakScaffolding1(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (shouldDisableBreakScaffolding(pos)) {
            cir.setReturnValue(false);
        }
    }

    private boolean shouldDisableBreakScaffolding(BlockPos pos) {
        Level world = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if (Configs.disableBreakScaffolding &&
                world != null && world.getBlockState(pos).is(Blocks.SCAFFOLDING) &&
                player != null) {
            String itemId = Registry.ITEM.getKey(player.getMainHandItem().getItem()).toString();
            String itemName = player.getMainHandItem().getItem().getDescription().getString();
            return Configs.breakScaffoldingWhiteList.stream().noneMatch(s -> itemId.contains(s) || itemName.contains(s));
        }
        return false;
    }

}
