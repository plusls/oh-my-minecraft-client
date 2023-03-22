package com.plusls.ommc.mixin.api.command;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.api.annotation.Dependency;

//#if MC <= 11605
//$$ import net.minecraft.client.main.GameConfig;
//$$ import com.plusls.ommc.api.command.ClientCommandInternals;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Dependencies(and = @Dependency(value = "minecraft", versionPredicate = "<=1.16.5"))
@Mixin(Minecraft.class)
public class MixinMinecraft {
    //#if MC <= 11605
    //$$ @Inject(method = "<init>", at = @At("RETURN"))
    //$$ private void onConstruct(GameConfig args, CallbackInfo info) {
    //$$     ClientCommandInternals.finalizeInit();
    //$$ }
    //#endif
}
