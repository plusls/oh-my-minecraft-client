package com.plusls.ommc.mixin.api.command;


import com.plusls.ommc.api.command.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.hendrixshen.magiclib.compat.minecraft.network.chat.ComponentCompatApi;

//#if MC > 11802
import net.minecraft.core.Registry;
import java.util.Objects;
//#else
//$$ import top.hendrixshen.magiclib.compat.minecraft.UtilCompatApi;
//#endif

// Code from https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/mixin/command/client/ClientCommandSourceMixin.java
@Mixin(ClientSuggestionProvider.class)
public abstract class MixinClientSuggestionProvider implements FabricClientCommandSource {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Override
    public void sendFeedback(Component message) {
        LocalPlayer localPlayer = minecraft.player;
        if (localPlayer != null) {
            localPlayer.displayClientMessage(message, false);
        }
    }

    @Override
    public void sendError(Component message) {
        LocalPlayer localPlayer = minecraft.player;
        if (localPlayer != null) {
            localPlayer.displayClientMessage(message.copy().withStyle(ChatFormatting.RED), false);
        }
    }

    @Override
    public Minecraft getClient() {
        return minecraft;
    }

    @Override
    public LocalPlayer getPlayer() {
        return minecraft.player;
    }

    @Override
    public ClientLevel getWorld() {
        return minecraft.level;
    }

    @Override
    public Entity getEntity() {
        return FabricClientCommandSource.super.getEntity();
    }

    @Override
    public Vec3 getPosition() {
        return FabricClientCommandSource.super.getPosition();
    }

    @Override
    public Vec2 getRotation() {
        return FabricClientCommandSource.super.getRotation();
    }

    @Override
    public Object getMeta(String key) {
        return FabricClientCommandSource.super.getMeta(key);
    }
}