package com.plusls.ommc.event;

import com.plusls.ommc.ModInfo;
import com.plusls.ommc.config.Configs;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;

public class InputHandler implements IKeybindProvider {
    private static final InputHandler INSTANCE = new InputHandler();

    private InputHandler() {}

    public static InputHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void addKeysToMap(IKeybindManager manager) {
        for (ConfigHotkey configHotkey : Configs.Generic.HOTKEYS) manager.addKeybindToMap(configHotkey.getKeybind());
        for (IHotkey configHotkey : Configs.FeatureToggle.OPTIONS) manager.addKeybindToMap(configHotkey.getKeybind());
    }

    @Override
    public void addHotkeys(IKeybindManager manager) {
    }
}