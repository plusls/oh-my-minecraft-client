package com.plusls.ommc.mixin.advancedIntegratedServer;

import com.plusls.ommc.config.Configs;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer {

    @ModifyArg(method = "setupServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;setOnlineMode(Z)V", ordinal = 0), index = 0)
    private boolean modifySetOnlineModeArg(boolean onlineMode) {
        return Configs.AdvancedIntegratedServer.ONLINE_MODE.getBooleanValue();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;setPvpEnabled(Z)V", ordinal = 0), index = 0)
    private boolean modifySetPvpEnabledArg(boolean arg) {
        return Configs.AdvancedIntegratedServer.PVP.getBooleanValue();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;setFlightEnabled(Z)V", ordinal = 0), index = 0)
    private boolean modifySetFlightEnabledArg(boolean arg) {
        return Configs.AdvancedIntegratedServer.FLIGHT.getBooleanValue();
    }

}
