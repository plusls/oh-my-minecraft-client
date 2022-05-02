package com.plusls.ommc.mixin.feature.highlightPersistentMob;

import com.plusls.ommc.config.Configs;
import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {
    private static final List<String> itemBlackList = Arrays.asList("sword", "bow", "trident", "axe", "fishing_rod");

    @SuppressWarnings("unchecked")
    private static <T extends Entity> T getBestEntity(T entity) {
        // Only try to fetch the corresponding server world if the entity is in the actual client world.
        // Otherwise the entity may be for example in Litematica's schematic world.
        Level world = entity.getCommandSenderWorld();
        Minecraft client = Minecraft.getInstance();
        T ret = entity;
        if (world == client.level) {
            world = WorldUtils.getBestWorld(client);
            if (world != null && world != client.level) {
                Entity bestEntity = world.getEntity(entity.getId());
                if (entity.getClass().isInstance(bestEntity)) {
                    ret = (T) bestEntity;
                }
            }
        }
        return ret;
    }

    @Inject(method = "shouldEntityAppearGlowing", at = @At(value = "RETURN"), cancellable = true)
    private void checkWanderingTraderEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.highlightPersistentMob && !cir.getReturnValue()) {
            entity = getBestEntity(entity);
            if (entity instanceof Mob) {
                Mob mobEntity = (Mob) entity;
                if (mobEntity.requiresCustomPersistence() || mobEntity.isPersistenceRequired()) {
                    cir.setReturnValue(true);
                    return;
                }
                if (!Configs.highlightPersistentMobClientMode) {
                    return;
                }
                String mainHandItemName = Registry.ITEM.getKey(mobEntity.getMainHandItem().getItem()).toString();
                if (!mobEntity.getMainHandItem().isEmpty() && itemBlackList.stream().noneMatch(mainHandItemName::contains) ||
                        entity.getCustomName() != null) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
