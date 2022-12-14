package com.plusls.ommc.mixin.api.command;

//#if MC <= 11802
//$$ import com.plusls.ommc.api.command.ClientCommandInternals;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {
    //#if MC <= 11802
    //$$ @Inject(method = "chat", at = @At("HEAD"), cancellable = true)
    //$$ private void onSendChatMessage(String message, CallbackInfo ci) {
    //$$     if (ClientCommandInternals.executeCommand(message)) {
    //$$         ci.cancel();
    //$$     }
    //$$ }
    //#endif
}
