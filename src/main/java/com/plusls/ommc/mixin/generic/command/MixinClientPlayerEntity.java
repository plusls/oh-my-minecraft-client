package com.plusls.ommc.mixin.generic.command;

import com.plusls.ommc.util.command.ClientCommandInternals;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Code from https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/mixin/command/client/ClientPlayerEntityMixin.java
@Mixin(ClientPlayerEntity.class)
abstract class MixinClientPlayerEntity {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo info) {
        if (ClientCommandInternals.executeCommand(message)) {
            info.cancel();
        }
    }
}
