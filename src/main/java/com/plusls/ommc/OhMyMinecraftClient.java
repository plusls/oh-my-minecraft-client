package com.plusls.ommc;

import com.plusls.ommc.config.Configs;
import com.plusls.ommc.event.InputHandler;
import com.plusls.ommc.feature.chatNetworkLagFix.ChatMessageHandler;
import com.plusls.ommc.feature.highlightLavaSource.LavaSourceResourceLoader;
import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointResourceLoader;
import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointUtil;
import com.plusls.ommc.feature.realSneaking.RealSneakingEventHandler;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import net.fabricmc.api.ClientModInitializer;

public class OhMyMinecraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LavaSourceResourceLoader.init();
        HighlightWaypointResourceLoader.init();
        ConfigManager.getInstance().registerConfigHandler(ModInfo.MOD_ID, new Configs());
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
        RealSneakingEventHandler.init();
        HighlightWaypointUtil.init();
        ChatMessageHandler.getInstance().init();
    }
}

