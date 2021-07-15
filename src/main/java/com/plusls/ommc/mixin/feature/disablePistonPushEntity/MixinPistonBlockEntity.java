package com.plusls.ommc.mixin.feature.disablePistonPushEntity;

import com.plusls.ommc.config.Configs;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(PistonBlockEntity.class)
public class MixinPistonBlockEntity {
    @Redirect(method = "pushEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;", ordinal = 0))
    private static List<Entity> removeNoPlayerEntity(World world, Entity except, Box box) {
        if (world.isClient() && Configs.FeatureToggle.DISABLE_PISTON_PUSH_ENTITY.getBooleanValue()) {
            ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
            if (playerEntity != null && !playerEntity.isSpectator() &&
                    playerEntity.getBoundingBox().intersects(box)
            ) {
                return List.of(playerEntity);
            }
            return List.of();
        }
        return world.getOtherEntities(except, box);
    }
}
