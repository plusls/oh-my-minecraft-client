package com.plusls.ommc.mixin.advancedIntegratedServer;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(OpenToLanScreen.class)
public class MixinOpenToLanScreen {
    @SuppressWarnings("UnresolvedMixinReference")
    @ModifyVariable(method = "method_19851", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/util/NetworkUtils;findLocalPort()I", ordinal = 0), ordinal = 0)
    private int modifyPort(int port) {
        int ret = Configs.AdvancedIntegratedServer.PORT.getIntegerValue();
        if (ret < 1024) {
            ret = port;
        }
        return ret;
    }
}
