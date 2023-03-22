package com.plusls.ommc.compat.modmenu;

import com.plusls.ommc.OhMyMinecraftClientReference;

public class WrapperModMenuApiImpl extends ModMenuApiImpl {
    @Override
    public String getModIdCompat() {
        return OhMyMinecraftClientReference.getModIdentifier();
    }
}
