package com.plusls.ommc.mixin.feature.disablePistonPushEntity;

import com.plusls.ommc.config.Configs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.phys.AABB;

@Mixin(PistonMovingBlockEntity.class)
public class MixinPistonBlockEntity {
    @Redirect(method = "moveCollidedEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;", ordinal = 0))
    private static List<Entity> removeNoPlayerEntity(Level world, Entity except, AABB box) {
        if (world.isClientSide() && Configs.FeatureToggle.DISABLE_PISTON_PUSH_ENTITY.getBooleanValue()) {
            LocalPlayer playerEntity = Minecraft.getInstance().player;
            if (playerEntity != null && !playerEntity.isSpectator() &&
                    playerEntity.getBoundingBox().intersects(box)
            ) {
                return List.of(playerEntity);
            }
            return List.of();
        }
        return world.getEntities(except, box);
    }
}
