package com.plusls.ommc.mixin.feature.highlightPersistentMob;

import com.plusls.ommc.config.Configs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

@Mixin(Entity.class)
public class MixinEntity {
    private static final List<String> itemBlackList = Arrays.asList("sword", "bow", "trident", "axe", "fishing_rod");

    @Inject(method = "isGlowing", at = @At(value = "RETURN"), cancellable = true)
    private void checkWanderingTraderEntity(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (Configs.FeatureToggle.HIGHLIGHT_PERSISTENT_MOB.getBooleanValue() && !cir.getReturnValue()) {
            if (entity instanceof EndermanEntity) {
                EndermanEntity endermanEntity = (EndermanEntity) entity;
                if (endermanEntity.getCarriedBlock() != null) {
                    cir.setReturnValue(true);
                }
            } else if (entity instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity) entity;
                String mainHandItemName = Registry.ITEM.getId(mobEntity.getMainHandStack().getItem()).toString();
                if (!mobEntity.getMainHandStack().isEmpty() && itemBlackList.stream().noneMatch(mainHandItemName::contains) ||
                        entity.getCustomName() != null) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
