package com.plusls.ommc.mixin.feature.highlightWaypoint;

import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointUtil;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatHud.class)
public class MixinChatHud {

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At(value = "HEAD"), ordinal = 0)
    public Text modifyMessage(Text message) {
        LiteralText result = new LiteralText("");
        if (HighlightWaypointUtil.parseWaypoints(message, result)) {
            return result;
        }
        return message;
    }
}
