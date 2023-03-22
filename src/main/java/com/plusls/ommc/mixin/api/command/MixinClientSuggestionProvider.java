package com.plusls.ommc.mixin.api.command;

import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;

//#if MC <= 11605
//$$ import com.plusls.ommc.api.command.FabricClientCommandSource;
//$$ import net.minecraft.ChatFormatting;
//$$ import net.minecraft.client.Minecraft;
//$$ import net.minecraft.client.multiplayer.ClientLevel;
//$$ import net.minecraft.client.player.LocalPlayer;
//$$ import net.minecraft.network.chat.Component;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Shadow;
//#endif

// Code from https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/mixin/command/client/ClientCommandSourceMixin.java
@Dependencies(and = @Dependency(value = "minecraft", versionPredicate = "<=1.16.5"))
@Mixin(ClientSuggestionProvider.class)
//#if MC <= 11605
//$$ public abstract class MixinClientSuggestionProvider implements FabricClientCommandSource {
//$$     @Shadow
//$$     @Final
//$$     private Minecraft minecraft;
//$$ 
//$$     @Override
//$$     public void sendFeedback(Component message) {
//$$         LocalPlayer localPlayer = minecraft.player;
//$$         if (localPlayer != null) {
//$$             localPlayer.displayClientMessage(message, false);
//$$         }
//$$     }
//$$ 
//$$     @Override
//$$     public void sendError(Component message) {
//$$         LocalPlayer localPlayer = minecraft.player;
//$$         if (localPlayer != null) {
//$$             localPlayer.displayClientMessage(message.copy().withStyle(ChatFormatting.RED), false);
//$$         }
//$$     }
//$$ 
//$$     @Override
//$$     public Minecraft getClient() {
//$$         return minecraft;
//$$     }
//$$ 
//$$     @Override
//$$     public LocalPlayer getPlayer() {
//$$         return minecraft.player;
//$$     }
//$$ 
//$$     @Override
//$$     public ClientLevel getWorld() {
//$$         return minecraft.level;
//$$     }
//$$ }
//#else
public abstract class MixinClientSuggestionProvider {
}
//#endif