package com.plusls.ommc.mixin.feature.dontClearChatHistory;

import com.plusls.ommc.config.Configs;
//#if MC <= 11802
//$$ import net.minecraft.client.GuiMessage;
//#endif
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC > 11502
import net.minecraft.util.FormattedCharSequence;
//#else
//$$ import net.minecraft.network.chat.Component;
//#endif

import java.util.List;

// From https://www.curseforge.com/minecraft/mc-mods/dont-clear-chat-history
@Mixin(ChatComponent.class)
public class MixinChatHud {
    @Inject(method = "clearMessages", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V", ordinal = 2), cancellable = true)
    private void dontClearChatHistory(boolean clearHistory, CallbackInfo ci) {
        if (Configs.dontClearChatHistory) {
            ci.cancel();
        }
    }

    @Redirect(
            //#if MC > 11802
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V",
            //#else
            //$$ method = "addMessage(Lnet/minecraft/network/chat/Component;IIZ)V",
            //#endif
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;size()I",
                    ordinal = 0
            )
    )
    //#if MC > 11802
    private int modifySize(List<FormattedCharSequence> list) {
    //#elseif MC > 11502
    //$$ private int modifySize(List<GuiMessage<FormattedCharSequence>> list) {
    //#else
    //$$ private int modifySize(List<Component> list) {
    //#endif
        if (Configs.dontClearChatHistory) {
            return 1;
        }
        return list.size();
    }
}
