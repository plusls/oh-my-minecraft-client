package com.plusls.ommc.mixin.feature.betterSneaking;

import com.plusls.ommc.config.Configs;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    final private static float MAX_STEP_HEIGHT = 1.25f;
    final private static float DEFAULT_STEP_HEIGHT = 114514;
    private float prevStepHeight = DEFAULT_STEP_HEIGHT;

    @Inject(method = "adjustMovementForSneaking", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/Vec3d;x:D", opcode = Opcodes.GETFIELD, ordinal = 0))
    private void setStepHeight(Vec3d movement, MovementType type, CallbackInfoReturnable<Vec3d> cir) {
        if (!Configs.FeatureToggle.BETTER_SNEAKING.getBooleanValue()) {
            return;
        }
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        prevStepHeight = playerEntity.stepHeight;
        playerEntity.stepHeight = MAX_STEP_HEIGHT;
    }

    @Inject(method = "adjustMovementForSneaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V", ordinal = 0))
    private void restoreStepHeight(Vec3d movement, MovementType type, CallbackInfoReturnable<Vec3d> cir) {
        if (!Configs.FeatureToggle.BETTER_SNEAKING.getBooleanValue() || Math.abs(prevStepHeight - DEFAULT_STEP_HEIGHT) <= 0.001) {
            return;
        }
        ((PlayerEntity) (Object) this).stepHeight = prevStepHeight;
        prevStepHeight = DEFAULT_STEP_HEIGHT;
    }
}
