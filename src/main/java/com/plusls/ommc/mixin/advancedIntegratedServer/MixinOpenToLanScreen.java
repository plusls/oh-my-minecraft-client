package com.plusls.ommc.mixin.advancedIntegratedServer;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ShareToLanScreen.class)
public class MixinOpenToLanScreen {
    @ModifyVariable(method = "method_19851",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/HttpUtil;getAvailablePort()I", ordinal = 0, remap = true),
            ordinal = 0, remap = false)
    private int modifyPort(int port) {
        int ret = Configs.AdvancedIntegratedServer.PORT.getIntegerValue();
        if (ret == 0) {
            ret = port;
        }
        return ret;
    }
}
