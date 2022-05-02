package com.plusls.ommc.mixin.feature.betterSneaking;

import com.plusls.ommc.config.Configs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    final private static float MAX_STEP_HEIGHT = 1.25f;
    final private static float DEFAULT_STEP_HEIGHT = 114514;
    private float prevStepHeight = DEFAULT_STEP_HEIGHT;

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "maybeBackOffFromEdge", at = @At(value = "FIELD", target = "Lnet/minecraft/world/phys/Vec3;x:D", opcode = Opcodes.GETFIELD, ordinal = 0))
    private void setStepHeight(Vec3 movement, MoverType type, CallbackInfoReturnable<Vec3> cir) {
        if (!Configs.FeatureToggle.BETTER_SNEAKING.getBooleanValue() || !this.level.isClientSide()) {
            return;
        }
        prevStepHeight = this.maxUpStep;
        this.maxUpStep = MAX_STEP_HEIGHT;
    }

    @Inject(method = "maybeBackOffFromEdge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V", ordinal = 0))
    private void restoreStepHeight(Vec3 movement, MoverType type, CallbackInfoReturnable<Vec3> cir) {
        if (!Configs.FeatureToggle.BETTER_SNEAKING.getBooleanValue() || !this.level.isClientSide() || Math.abs(prevStepHeight - DEFAULT_STEP_HEIGHT) <= 0.001) {
            return;
        }
        this.maxUpStep = prevStepHeight;
        prevStepHeight = DEFAULT_STEP_HEIGHT;
    }

    @Redirect(method = "maybeBackOffFromEdge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z", ordinal = -1))
    private boolean myIsSpaceEmpty(Level world, Entity entity, AABB box) {
        boolean retOld = world.noCollision(entity, box.move(0, this.maxUpStep - prevStepHeight, 0));
        boolean retNew = world.noCollision(entity, box);
        if (Configs.FeatureToggle.BETTER_SNEAKING.getBooleanValue() && this.level.isClientSide() && (retOld && !retNew) &&
                world.getFluidState(this.blockPosition().below()).getType() instanceof LavaFluid) {
            return true;
        }
        return retNew;
    }

    @Inject(method = "isAboveGround", at = @At(value = "HEAD"))
    private void setStepHeight(CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.FeatureToggle.BETTER_SNEAKING.getBooleanValue() || !this.level.isClientSide()) {
            return;
        }
        Player playerEntity = (Player) (Object) this;
        prevStepHeight = playerEntity.maxUpStep;
        playerEntity.maxUpStep = MAX_STEP_HEIGHT;
    }

    @Inject(method = "isAboveGround", at = @At(value = "RETURN"))
    private void restoreStepHeight(CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.FeatureToggle.BETTER_SNEAKING.getBooleanValue() || !this.level.isClientSide() || Math.abs(prevStepHeight - DEFAULT_STEP_HEIGHT) <= 0.001) {
            return;
        }
        this.maxUpStep = prevStepHeight;
        prevStepHeight = DEFAULT_STEP_HEIGHT;
    }
}
