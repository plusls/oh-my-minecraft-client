package com.plusls.ommc.mixin.advancedIntegratedServer;

import net.minecraft.client.gui.screens.ShareToLanScreen;
import org.spongepowered.asm.mixin.Mixin;
//#if MC < 11903
//$$ import com.plusls.ommc.config.Configs;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.ModifyVariable;
//#endif

@Mixin(ShareToLanScreen.class)
public class MixinOpenToLanScreen {
    //#if MC < 11903
    //$$ @ModifyVariable(method = "method_19851",
    //$$         at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/HttpUtil;getAvailablePort()I", ordinal = 0),
    //$$         ordinal = 0, remap = false)
    //$$ private int modifyPort(int port) {
    //$$     int ret = Configs.port;
    //$$     if (ret == 0) {
    //$$         ret = port;
    //$$     }
    //$$     return ret;
    //$$ }
    //#endif
}
