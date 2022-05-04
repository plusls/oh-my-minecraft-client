package com.plusls.ommc.mixin.feature.highlightEntity;

import com.plusls.ommc.config.Configs;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public Level level;

    @Inject(method = "isCurrentlyGlowing", at = @At(value = "RETURN"), cancellable = true)
    private void checkWanderingTraderEntity(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() || !this.level.isClientSide) {
            return;
        }
        String entityId = Registry.ENTITY_TYPE.getKey(this.getType()).toString();
        String entityName = this.getType().getDescription().getString();
        if (Configs.highlightEntityListType == UsageRestriction.ListType.WHITELIST) {
            cir.setReturnValue(Configs.highlightEntityWhiteList.stream().anyMatch(s -> entityId.contains(s) || entityName.contains(s)));
        } else if (Configs.highlightEntityListType == UsageRestriction.ListType.BLACKLIST) {
            cir.setReturnValue(Configs.highlightEntityBlackList.stream().noneMatch(s -> entityId.contains(s) || entityName.contains(s)));
        }
    }
}
