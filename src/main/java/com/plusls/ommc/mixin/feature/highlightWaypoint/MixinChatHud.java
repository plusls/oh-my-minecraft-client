package com.plusls.ommc.mixin.feature.highlightWaypoint;

import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointUtil;
//#if MC > 11802
import net.minecraft.client.GuiMessageTag;
//#endif
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatComponent.class, priority = 999)
public class MixinChatHud {

    @Inject(
            //#if MC > 11802
            method = "logChatMessage",
            //#else
            //$$ method = "addMessage(Lnet/minecraft/network/chat/Component;I)V",
            //#endif
            at = @At(
                    value = "HEAD"
            )
    )
    //#if MC > 11802
    public void modifyMessage(Component message, GuiMessageTag guiMessageTag, CallbackInfo ci) {
    //#else
    //$$ public void modifyMessage(Component message, int messageId, CallbackInfo ci) {
    //#endif
        if (Configs.parseWaypointFromChat) {
            HighlightWaypointUtil.parseWaypointText(message);
        }
    }
}
