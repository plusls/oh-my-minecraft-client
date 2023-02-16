package com.plusls.ommc.compat.modmenu;

import com.plusls.ommc.OhMyMinecraftClientReference;
import com.plusls.ommc.gui.GuiConfigs;
import top.hendrixshen.magiclib.compat.modmenu.ModMenuCompatApi;

public class ModMenuApiImpl implements ModMenuCompatApi {
    @Override
    public ConfigScreenFactoryCompat<?> getConfigScreenFactoryCompat() {
        return (screen) -> {
            GuiConfigs gui = GuiConfigs.getInstance();
            gui.setParentGui(screen);
            return gui;
        };
    }

    @Override
    public String getModIdCompat() {
        return OhMyMinecraftClientReference.getCurrentModIdentifier();
    }
}
