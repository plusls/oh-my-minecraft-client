package com.plusls.ommc.gui;

import com.plusls.ommc.OhMyMinecraftClientReference;
import com.plusls.ommc.config.Configs;
import top.hendrixshen.magiclib.config.ConfigManager;
import top.hendrixshen.magiclib.gui.ConfigGui;

public class GuiConfigs extends ConfigGui {

    private static GuiConfigs INSTANCE;

    private GuiConfigs(String identifier, String defaultTab, ConfigManager configManager) {
        super(identifier, defaultTab, configManager, () -> OhMyMinecraftClientReference.translate("gui.title.configs", OhMyMinecraftClientReference.getModVersion()));
    }


    public static GuiConfigs getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GuiConfigs(OhMyMinecraftClientReference.getModIdentifier(), Configs.ConfigCategory.GENERIC, ConfigManager.get(OhMyMinecraftClientReference.getModIdentifier()));
        }
        return INSTANCE;
    }
}
