package com.plusls.ommc.mixin.feature.preventIntentionalGameDesign;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MixinClientPlayerInteractionManager {
    @Inject(method = "useItemOn",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/InteractionResult;consumesAction()Z",
                    ordinal = 0),
            cancellable = true)
    private void preventIntentionalGameDesign(LocalPlayer player, ClientLevel world, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!Configs.preventIntentionalGameDesign) {
            return;
        }
        BlockPos blockPos = hitResult.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if ((blockState.getBlock() instanceof BedBlock && !world.dimensionType().bedWorks()) ||
                (blockState.getBlock() instanceof RespawnAnchorBlock && !world.dimensionType().respawnAnchorWorks())) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
