package com.plusls.ommc.mixin.generic.command;

import com.plusls.ommc.util.command.ClientCommandInternals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Code from https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/mixin/command/client/MinecraftClientMixin.java
@Mixin(MinecraftClient.class)
abstract class MixinMinecraftClient {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(RunArgs args, CallbackInfo info) {
        ClientCommandInternals.finalizeInit();
    }
}
