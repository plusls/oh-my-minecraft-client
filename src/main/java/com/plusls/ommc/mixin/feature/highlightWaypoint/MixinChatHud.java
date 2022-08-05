package com.plusls.ommc.mixin.feature.highlightWaypoint;

import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointUtil;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC > 11802
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.client.GuiMessageTag;
//#endif
@Mixin(value = ChatComponent.class, priority = 999)
public class MixinChatHud {

    //#if MC > 11802
    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V", at = @At(value = "HEAD"))
    public void modifyMessage(Component message, MessageSignature messageSignature, int i, GuiMessageTag guiMessageTag, boolean bl, CallbackInfo ci){
    //#else
    //$$ @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;I)V", at = @At(value = "HEAD"))
    //$$ public void modifyMessage(Component message, int messageId, CallbackInfo ci) {
    //#endif
        if (Configs.parseWaypointFromChat) {
            HighlightWaypointUtil.parseWaypointText(message);
        }
    }
}
