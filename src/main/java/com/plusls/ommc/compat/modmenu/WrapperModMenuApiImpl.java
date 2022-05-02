package com.plusls.ommc.compat.modmenu;

import com.plusls.ommc.ModInfo;

public class WrapperModMenuApiImpl extends ModMenuApiImpl {

    @Override
    public String getModIdCompat() {
        return ModInfo.MOD_ID;
    }

}