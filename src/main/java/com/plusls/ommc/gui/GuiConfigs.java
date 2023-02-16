package com.plusls.ommc.gui;

import com.plusls.ommc.OhMyMinecraftClientReference;
import com.plusls.ommc.config.Configs;
import top.hendrixshen.magiclib.config.ConfigManager;
import top.hendrixshen.magiclib.gui.ConfigGui;

public class GuiConfigs extends ConfigGui {

    private static GuiConfigs INSTANCE;

    private GuiConfigs(String identifier, String defaultTab, ConfigManager configManager) {
        super(identifier, defaultTab, configManager, () -> OhMyMinecraftClientReference.translate("gui.title.configs", OhMyMinecraftClientReference.MOD_VERSION));
    }


    public static GuiConfigs getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GuiConfigs(OhMyMinecraftClientReference.MOD_ID, Configs.ConfigCategory.GENERIC, ConfigManager.get(OhMyMinecraftClientReference.MOD_ID));
        }
        return INSTANCE;
    }
}
