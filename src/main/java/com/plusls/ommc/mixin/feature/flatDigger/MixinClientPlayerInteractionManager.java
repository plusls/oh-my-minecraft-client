package com.plusls.ommc.mixin.feature.flatDigger;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "startDestroyBlock", at = @At(value = "HEAD"), cancellable = true)
    private void flatDigger(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (shouldFlatDigger(pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueDestroyBlock", at = @At(value = "HEAD"), cancellable = true)
    private void flatDigger1(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (shouldFlatDigger(pos)) {
            cir.setReturnValue(false);
        }
    }

    private boolean shouldFlatDigger(BlockPos pos) {
        Level world = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if (Configs.FeatureToggle.FLAT_DIGGER.getBooleanValue() &&
                world != null && player != null) {
            return !player.isShiftKeyDown() && pos.getY() < player.getBlockY();
        }
        return false;
    }

}