package com.plusls.ommc.mixin.feature.highlightPersistentMob;

import com.plusls.ommc.config.Configs;
import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

@Mixin(Entity.class)
public class MixinEntity {
    private static final List<String> itemBlackList = Arrays.asList("sword", "bow", "trident", "axe", "fishing_rod");

    @SuppressWarnings("unchecked")
    private static <T extends Entity> T getBestEntity(T entity) {
        // Only try to fetch the corresponding server world if the entity is in the actual client world.
        // Otherwise the entity may be for example in Litematica's schematic world.
        World world = entity.getEntityWorld();
        MinecraftClient client = MinecraftClient.getInstance();
        T ret = entity;
        if (world == client.world) {
            world = WorldUtils.getBestWorld(client);
            if (world != null && world != client.world) {
                Entity bestEntity = world.getEntityById(entity.getEntityId());
                if (entity.getClass().isInstance(bestEntity)) {
                    ret = (T) bestEntity;
                }
            }
        }
        return ret;
    }

    @Inject(method = "isGlowing", at = @At(value = "RETURN"), cancellable = true)
    private void checkWanderingTraderEntity(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (Configs.FeatureToggle.HIGHLIGHT_PERSISTENT_MOB.getBooleanValue() && !cir.getReturnValue()) {
            entity = getBestEntity(entity);
            if (entity instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity) entity;
                if (mobEntity.cannotDespawn() || mobEntity.isPersistent()) {
                    cir.setReturnValue(true);
                    return;
                }
                if (!Configs.Generic.HIGHLIGHT_PERSISTENT_MOB_CLIENT_MODE.getBooleanValue()) {
                    return;
                }
                String mainHandItemName = Registry.ITEM.getId(mobEntity.getMainHandStack().getItem()).toString();
                if (!mobEntity.getMainHandStack().isEmpty() && itemBlackList.stream().noneMatch(mainHandItemName::contains) ||
                        entity.getCustomName() != null) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
