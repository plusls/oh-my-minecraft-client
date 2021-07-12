package com.plusls.ommc.mixin.feature.removeBreakCooldown;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    @Shadow
    private int blockBreakingCooldown;

    @Inject(method = "updateBlockBreakingProgress",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I",
                    opcode = Opcodes.PUTFIELD,
                    ordinal = 2, shift = At.Shift.AFTER))
    private void removeBreakingCooldown(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.FeatureToggle.REMOVE_BREAKING_COOLDOWN.getBooleanValue() && !Configs.FeatureToggle.FORCE_BREAKING_COOLDOWN.getBooleanValue()) {
            blockBreakingCooldown = 0;
        }
    }
}