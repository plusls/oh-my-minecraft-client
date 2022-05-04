package com.plusls.ommc.mixin.api.command;

import com.plusls.ommc.api.command.ClientCommandInternals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(GameConfig args, CallbackInfo info) {
        ClientCommandInternals.finalizeInit();
    }
}
