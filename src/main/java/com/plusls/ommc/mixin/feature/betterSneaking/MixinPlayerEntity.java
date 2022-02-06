package com.plusls.ommc.mixin.feature.betterSneaking;

import com.plusls.ommc.config.Configs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    final private static float MAX_STEP_HEIGHT = 1.25f;
    final private static float DEFAULT_STEP_HEIGHT = 114514;
    private float prevStepHeight = DEFAULT_STEP_HEIGHT;

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "adjustMovementForSneaking", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/Vec3d;x:D", opcode = Opcodes.GETFIELD, ordinal = 0))
    private void setStepHeight(Vec3d movement, MovementType type, CallbackInfoReturnable<Vec3d> cir) {
        if (!Configs.FeatureToggle.BETTER_SNEAKING.getBooleanValue() || !this.world.isClient()) {
            return;
        }
        prevStepHeight = this.stepHeight;
        this.stepHeight = MAX_STEP_HEIGHT;
    }

    @Inject(method = "adjustMovementForSneaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V", ordinal = 0))
    private void restoreStepHeight(Vec3d movement, MovementType type, CallbackInfoReturnable<Vec3d> cir) {
        if (!Configs.FeatureToggle.BETTER_SNEAKING.getBooleanValue() || !this.world.isClient() || Math.abs(prevStepHeight - DEFAULT_STEP_HEIGHT) <= 0.001) {
            return;
        }
        this.stepHeight = prevStepHeight;
        prevStepHeight = DEFAULT_STEP_HEIGHT;
    }

    @Redirect(method = "adjustMovementForSneaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isSpaceEmpty(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Z", ordinal = -1))
    private boolean myIsSpaceEmpty(World world, Entity entity, Box box) {
        boolean retOld = world.isSpaceEmpty(entity, box.offset(0, this.stepHeight - prevStepHeight, 0));
        boolean retNew = world.isSpaceEmpty(entity, box);
        if (Configs.FeatureToggle.BETTER_SNEAKING.getBooleanValue() && this.world.isClient() && (retOld && !retNew) &&
                world.getFluidState(this.getBlockPos().down()).getFluid() instanceof LavaFluid) {
            return true;
        }
        return retNew;
    }

    @Inject(method = "method_30263", at = @At(value = "HEAD"))
    private void setStepHeight(CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.FeatureToggle.BETTER_SNEAKING.getBooleanValue() || !this.world.isClient()) {
            return;
        }
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        prevStepHeight = playerEntity.stepHeight;
        playerEntity.stepHeight = MAX_STEP_HEIGHT;
    }

    @Inject(method = "method_30263", at = @At(value = "RETURN"))
    private void restoreStepHeight(CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.FeatureToggle.BETTER_SNEAKING.getBooleanValue() || !this.world.isClient() || Math.abs(prevStepHeight - DEFAULT_STEP_HEIGHT) <= 0.001) {
            return;
        }
        this.stepHeight = prevStepHeight;
        prevStepHeight = DEFAULT_STEP_HEIGHT;
    }
}
