package com.plusls.ommc.mixin.api.command;


import com.plusls.ommc.api.command.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.hendrixshen.magiclib.compat.minecraft.UtilCompatApi;
import top.hendrixshen.magiclib.compat.minecraft.network.chat.ComponentCompatApi;

//#if MC > 11802
//$$ import net.minecraft.core.Registry;
//$$ import java.util.Objects;
//#endif

// Code from https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/mixin/command/client/ClientCommandSourceMixin.java
@Mixin(ClientSuggestionProvider.class)
public abstract class MixinClientSuggestionProvider implements FabricClientCommandSource {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Override
    public void sendFeedback(Component message) {
        //#if MC > 11802
        //$$ minecraft.gui.handleSystemChat(Objects.requireNonNull(Objects.requireNonNull(minecraft.level).registryAccess().registryOrThrow(Registry.CHAT_TYPE_REGISTRY).get(ChatType.SYSTEM)), message);
        //#elseif MC > 11502
        minecraft.gui.handleChat(ChatType.SYSTEM, message, UtilCompatApi.NIL_UUID);
        //#else
        //$$ minecraft.gui.handleChat(ChatType.SYSTEM, message);
        //#endif
    }

    @Override
    public void sendError(Component message) {
        Component m = ComponentCompatApi.literal("").append(message).withStyle(ChatFormatting.RED);
        //#if MC > 11802
        //$$ minecraft.gui.handleSystemChat(Objects.requireNonNull(Objects.requireNonNull(minecraft.level).registryAccess().registryOrThrow(Registry.CHAT_TYPE_REGISTRY).get(ChatType.SYSTEM)), m);
        //#elseif MC > 11502
        minecraft.gui.handleChat(ChatType.SYSTEM, m, UtilCompatApi.NIL_UUID);
        //#else
        //$$ minecraft.gui.handleChat(ChatType.SYSTEM, m);
        //#endif
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
}