package com.plusls.ommc.command;

import com.mojang.brigadier.context.CommandContext;
import com.plusls.ommc.OmmcConfig;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

public class SwitchHighlightLavaCommand {
    private static boolean modifyHighlightLava;

    public static void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("switchHighlightLava").executes(SwitchHighlightLavaCommand::execute));
    }


    public static void updateHighlightLava(MinecraftClient minecraftClient) {
        if (modifyHighlightLava) {
            if (OmmcConfig.highlightLava) {
                OmmcConfig.highlightLava = false;
                if (minecraftClient.player != null) {
                    minecraftClient.player.sendMessage(new LiteralText("HighlightLava off"), false);

                }
            } else {
                OmmcConfig.highlightLava = true;
                if (minecraftClient.player != null) {
                    minecraftClient.player.sendMessage(new LiteralText("HighlightLava on"), false);
                }
            }
            minecraftClient.worldRenderer.reload();
            modifyHighlightLava = false;
        }

    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        modifyHighlightLava = true;
        return 0;
    }
}