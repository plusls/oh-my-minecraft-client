package com.plusls.ommc;

import com.plusls.ommc.config.Configs;
import com.plusls.ommc.event.InputHandler;
import com.plusls.ommc.feature.highlightLavaSource.LavaSourceTexture;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import net.fabricmc.api.ClientModInitializer;

public class OhMyMinecraftClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        LavaSourceTexture.initLavaSourceFlowSprite();
        ConfigManager.getInstance().registerConfigHandler(ModInfo.MOD_ID, new Configs());
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
    }


}

