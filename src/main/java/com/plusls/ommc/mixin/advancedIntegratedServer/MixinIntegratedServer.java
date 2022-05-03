package com.plusls.ommc.mixin.advancedIntegratedServer;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer {

    @ModifyArg(method = "initServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;setUsesAuthentication(Z)V", ordinal = 0), index = 0)
    private boolean modifySetOnlineModeArg(boolean onlineMode) {
        return Configs.onlineMode;
    }

    @ModifyArg(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;setPvpAllowed(Z)V", ordinal = 0), index = 0)
    private boolean modifySetPvpEnabledArg(boolean arg) {
        return Configs.pvp;
    }

    @ModifyArg(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;setFlightAllowed(Z)V", ordinal = 0), index = 0)
    private boolean modifySetFlightEnabledArg(boolean arg) {
        return Configs.flight;
    }

}
