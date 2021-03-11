package com.plusls.ommc;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class OmmcMixinPlugin implements IMixinConfigPlugin {
    public final static String SODIUM_MOD_ID = "sodium";
    private static final String MIXIN_SODIUM = ".sodium.";
    public final static String CANVAS_MOD_ID = "canvas";
    private static final String MIXIN_CANVAS = ".canvas.";
    public static boolean isSodiumLoaded;
    public static boolean isCanvasLoaded;

    @Override
    public void onLoad(String mixinPackage) {
        if (FabricLoader.getInstance().isModLoaded(SODIUM_MOD_ID)) {
            isSodiumLoaded = true;
        }
        if (FabricLoader.getInstance().isModLoaded(CANVAS_MOD_ID)) {
            isCanvasLoaded = true;
        }
    }


    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!isSodiumLoaded && mixinClassName.contains(MIXIN_SODIUM)) {
            return false;
        } else if (!isCanvasLoaded && mixinClassName.contains(MIXIN_CANVAS)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
