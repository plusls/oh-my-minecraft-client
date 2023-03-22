package com.plusls.ommc.mixin.feature.disableBlocklistCheck;

import com.plusls.ommc.config.Configs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

//#if MC > 11502
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
//#else
//$$ import top.hendrixshen.magiclib.compat.preprocess.api.DummyClass;
//#endif

//#if MC > 11502
@Mixin(PlayerSocialManager.class)
//#else
//$$ @Mixin(DummyClass.class)
//#endif
public class MixinSocialInteractionsManager {
    //#if MC > 11502
    @Inject(method = "isBlocked", at = @At("HEAD"), cancellable = true)
    public void disableBlocklistCheck(UUID uuid, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.disableBlocklistCheck) {
            cir.setReturnValue(false);
        }
    }
    //#endif
}
