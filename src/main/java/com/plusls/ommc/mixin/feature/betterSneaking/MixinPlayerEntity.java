package com.plusls.ommc.mixin.feature.betterSneaking;

import com.plusls.ommc.config.Configs;
import net.minecraft.world.entity.Entity;
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
import top.hendrixshen.magiclib.util.MiscUtil;

//#if MC > 11404
@Mixin(Player.class)
//#else
//$$ @Mixin(Entity.class)
//#endif
public abstract class MixinPlayerEntity {

    final private static float MAX_STEP_HEIGHT = 1.25f;
    final private static float DEFAULT_STEP_HEIGHT = 114514;
    private float prevStepHeight = DEFAULT_STEP_HEIGHT;

    @Inject(method = "maybeBackOffFromEdge", at = @At(value = "FIELD", target = "Lnet/minecraft/world/phys/Vec3;x:D", opcode = Opcodes.GETFIELD, ordinal = 0))
    private void setStepHeight(Vec3 movement, MoverType type, CallbackInfoReturnable<Vec3> cir) {
        Entity thisObj = MiscUtil.cast(this);
        if (!Configs.betterSneaking || !thisObj.getLevelCompat().isClientSide()) {
            return;
        }
        prevStepHeight = thisObj.maxUpStepCompat();
        thisObj.setMaxUpStepCompat(MAX_STEP_HEIGHT);
    }

    @Inject(method = "maybeBackOffFromEdge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V", ordinal = 0))
    private void restoreStepHeight(Vec3 movement, MoverType type, CallbackInfoReturnable<Vec3> cir) {
        Entity thisObj = MiscUtil.cast(this);
        if (!Configs.betterSneaking || !thisObj.getLevelCompat().isClientSide() || Math.abs(prevStepHeight - DEFAULT_STEP_HEIGHT) <= 0.001) {
            return;
        }
        thisObj.setMaxUpStepCompat(prevStepHeight);
        prevStepHeight = DEFAULT_STEP_HEIGHT;
    }

    @Redirect(method = "maybeBackOffFromEdge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z", ordinal = -1))
    private boolean myIsSpaceEmpty(Level world, Entity entity, AABB box) {
        Entity thisObj = MiscUtil.cast(this);
        boolean retOld = world.noCollision(entity, box.move(0, thisObj.maxUpStepCompat() - prevStepHeight, 0));
        boolean retNew = world.noCollision(entity, box);
        if (Configs.betterSneaking && thisObj.getLevelCompat().isClientSide() && (retOld && !retNew) &&
                world.getFluidState(thisObj.blockPosition().below()).getType() instanceof LavaFluid) {
            return true;
        }
        return retNew;
    }

    //#if MC > 11502
    @Inject(method = "isAboveGround", at = @At(value = "HEAD"))
    private void setStepHeight(CallbackInfoReturnable<Boolean> cir) {
        Entity thisObj = MiscUtil.cast(this);
        if (!Configs.betterSneaking || !thisObj.getLevelCompat().isClientSide()) {
            return;
        }
        Player playerEntity = MiscUtil.cast(this);
        prevStepHeight = playerEntity.maxUpStepCompat();
        playerEntity.setMaxUpStepCompat(MAX_STEP_HEIGHT);
    }

    @Inject(method = "isAboveGround", at = @At(value = "RETURN"))
    private void restoreStepHeight(CallbackInfoReturnable<Boolean> cir) {
        Entity thisObj = MiscUtil.cast(this);
        if (!Configs.betterSneaking || !thisObj.getLevelCompat().isClientSide() || Math.abs(prevStepHeight - DEFAULT_STEP_HEIGHT) <= 0.001) {
            return;
        }
        thisObj.setMaxUpStepCompat(prevStepHeight);
        prevStepHeight = DEFAULT_STEP_HEIGHT;
    }
    //#endif
}
