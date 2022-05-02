package com.plusls.ommc.mixin.feature.highlightWanderingTrader;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.WanderingTrader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {
    @Inject(method = "shouldEntityAppearGlowing", at = @At(value = "RETURN"), cancellable = true)
    private void checkWanderingTraderEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.FeatureToggle.HIGHLIGHT_WANDERING_TRADER.getBooleanValue() && !cir.getReturnValue()) {
            if (entity instanceof WanderingTrader) {
                cir.setReturnValue(true);
            }
        }
    }
}
