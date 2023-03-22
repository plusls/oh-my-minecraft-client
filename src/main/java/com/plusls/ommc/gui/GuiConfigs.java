package com.plusls.ommc.gui;

import com.plusls.ommc.OhMyMinecraftClientReference;
import com.plusls.ommc.config.Configs;
import lombok.Getter;
import top.hendrixshen.magiclib.malilib.impl.ConfigManager;
import top.hendrixshen.magiclib.malilib.impl.gui.ConfigGui;

public class GuiConfigs extends ConfigGui {
    @Getter(lazy = true)
    private static final GuiConfigs instance = new GuiConfigs(OhMyMinecraftClientReference.getModIdentifier(), Configs.ConfigCategory.GENERIC, OhMyMinecraftClientReference.configHandler.configManager);

    private GuiConfigs(String identifier, String defaultTab, ConfigManager configManager) {
        super(identifier, defaultTab, configManager, () -> OhMyMinecraftClientReference.translate("gui.title.configs", OhMyMinecraftClientReference.getModVersion()));
    }
}
