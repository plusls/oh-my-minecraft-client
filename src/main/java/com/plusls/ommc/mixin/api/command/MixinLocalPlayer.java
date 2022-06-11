package com.plusls.ommc.mixin.api.command;

import com.plusls.ommc.api.command.ClientCommandInternals;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {
    @Inject(
            //#if MC > 11802
            method = "command(Ljava/lang/String;Lnet/minecraft/network/chat/Component;)V",
            //#else
            //$$ method = "chat",
            //#endif
            at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String string,
                                   //#if MC > 11802
                                   Component component,
                                   //#endif
                                   CallbackInfo ci) {
        //#if MC > 11802
        string = "/" + string;
        //#endif
        if (ClientCommandInternals.executeCommand(string)) {
            ci.cancel();
        }
    }
}
