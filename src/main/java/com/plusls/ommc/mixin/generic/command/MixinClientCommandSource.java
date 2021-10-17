package com.plusls.ommc.mixin.generic.command;

import com.plusls.ommc.util.command.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

// Code from https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/mixin/command/client/ClientCommandSourceMixin.java
@Mixin(ClientCommandSource.class)
abstract class MixinClientCommandSource implements FabricClientCommandSource {
    @Shadow
    @Final
    private MinecraftClient client;

    @Override
    public void sendFeedback(Text message) {
        client.inGameHud.addChatMessage(MessageType.SYSTEM, message);
    }

    @Override
    public void sendError(Text message) {
        client.inGameHud.addChatMessage(MessageType.SYSTEM, new LiteralText("").append(message).formatted(Formatting.RED));
    }

    @Override
    public MinecraftClient getClient() {
        return client;
    }

    @Override
    public ClientPlayerEntity getPlayer() {
        return client.player;
    }

    @Override
    public ClientWorld getWorld() {
        return client.world;
    }
}
