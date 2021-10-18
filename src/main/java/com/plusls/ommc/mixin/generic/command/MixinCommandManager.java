package com.plusls.ommc.mixin.generic.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Code from https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/mixin/command/CommandManagerMixin.java
@Mixin(CommandManager.class)
public abstract class MixinCommandManager {
    @Shadow
    @Final
    private CommandDispatcher<ServerCommandSource> dispatcher;

    /**
     * Wait an inject in a constructor?
     * This is a new addition to Fabric's fork of mixin.
     * If you are not using fabric's fork of mixin this will fail.
     *
     * @reason Add commands before ambiguities are calculated.
     */
    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V"), method = "<init>")
    private void fabric_addCommands(boolean isDedicatedServer, CallbackInfo ci) {
        CommandRegistrationCallback.EVENT.invoker().register(this.dispatcher, isDedicatedServer);
    }
}
