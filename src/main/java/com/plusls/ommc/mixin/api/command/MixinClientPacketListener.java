package com.plusls.ommc.mixin.api.command;

import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;

//#if MC <= 11605
//$$ import com.mojang.brigadier.CommandDispatcher;
//$$ import com.plusls.ommc.api.command.ClientCommandInternals;
//$$ import com.plusls.ommc.api.command.FabricClientCommandSource;
//$$ import net.minecraft.client.multiplayer.ClientSuggestionProvider;
//$$ import net.minecraft.commands.SharedSuggestionProvider;
//$$ import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Dependencies(and = @Dependency(value = "minecraft", versionPredicate = "<=1.16.5"))
@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {
	//#if MC <= 11605
    //$$ @Shadow
    //$$ private CommandDispatcher<SharedSuggestionProvider> commands;
    //$$
    //$$ @Shadow
    //$$ @Final
    //$$ private ClientSuggestionProvider suggestionsProvider;
    //$$
    //$$ @SuppressWarnings({"unchecked", "rawtypes"})
    //$$ @Inject(method = "handleCommands", at = @At("RETURN"))
    //$$ private void onOnCommandTree(ClientboundCommandsPacket packet, CallbackInfo info) {
    //$$     // Add the commands to the vanilla dispatcher for completion.
    //$$     // It's done here because both the server and the client commands have
    //$$     // to be in the same dispatcher and completion results.
    //$$     ClientCommandInternals.addCommands((CommandDispatcher) commands, (FabricClientCommandSource) suggestionsProvider);
    //$$ }
	//#endif
}
