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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "attackBlock", at = @At(value = "HEAD"), cancellable = true)
    private void disableBreakScaffolding(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (shouldDisableBreakScaffolding(pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "HEAD"), cancellable = true)
    private void disableBreakScaffolding1(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (shouldDisableBreakScaffolding(pos)) {
            cir.setReturnValue(false);
        }
    }

    private boolean shouldDisableBreakScaffolding(BlockPos pos) {
        World world = MinecraftClient.getInstance().world;
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (Configs.FeatureToggle.DISABLE_BREAK_SCAFFOLDING.getBooleanValue() &&
                world != null && world.getBlockState(pos).getBlock() == Blocks.SCAFFOLDING &&
                player != null) {
            String itemId = Registry.ITEM.getId(player.getMainHandStack().getItem()).toString();
            String itemName = player.getMainHandStack().getItem().getName().getString();
            return Configs.Lists.BREAK_SCAFFOLDING_WHITELIST.getStrings().stream().noneMatch(s -> itemId.contains(s) || itemName.contains(s));
        }
        return false;
    }

}
