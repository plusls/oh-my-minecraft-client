package com.plusls.ommc.mixin.feature.highlightWaypoint;

import com.plusls.ommc.OmmcMixinPlugin;
import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointUtil;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ChatHudListener.class)
public class MixinChatHudListener {
    @Inject(method = "onChatMessage", at = @At(value = "HEAD"), cancellable = true)
    public void postOnChatMessage(MessageType messageType, Text message, UUID sender, CallbackInfo ci) {
        if (!OmmcMixinPlugin.isVoxelmapLoaded && HighlightWaypointUtil.parseWaypoints(message)) {
            ci.cancel();
        }
    }
}
