package com.plusls.ommc.mixin.feature.highlightWaypoint;

import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointUtil;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatHud.class, priority = 999)
public class MixinChatHud {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At(value = "HEAD"))
    public void modifyMessage(Text message, int messageId, CallbackInfo ci) {
        if (Configs.Generic.PARSE_WAYPOINT_FROM_CHAT.getBooleanValue()) {
            HighlightWaypointUtil.parseWaypointText(message);
        }
    }
}
