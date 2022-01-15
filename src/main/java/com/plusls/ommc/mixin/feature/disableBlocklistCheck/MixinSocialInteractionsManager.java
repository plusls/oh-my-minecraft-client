package com.plusls.ommc.mixin.feature.disableBlocklistCheck;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.network.SocialInteractionsManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(SocialInteractionsManager.class)
public class MixinSocialInteractionsManager {
    @Inject(method = "isPlayerBlocked", at = @At("HEAD"), cancellable = true)
    public void disableBlocklistCheck(UUID uuid, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.FeatureToggle.DISABLE_BLOCKLIST_CHECK.getBooleanValue()) {
            cir.setReturnValue(false);
        }
    }
}
