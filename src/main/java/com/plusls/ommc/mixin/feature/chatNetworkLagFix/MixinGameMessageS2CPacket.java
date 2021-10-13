package com.plusls.ommc.mixin.feature.chatNetworkLagFix;

import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.chatNetworkLagFix.ChatMessageHandler;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameMessageS2CPacket.class)
public class MixinGameMessageS2CPacket {
    @Redirect(
            method = "apply",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/listener/ClientPlayPacketListener;" +
                            "onGameMessage(Lnet/minecraft/network/packet/s2c/play/GameMessageS2CPacket;)V")
    )
    public void onGameMessage(ClientPlayPacketListener clientPlayPacketListener, GameMessageS2CPacket packet) {
        if (Configs.FeatureToggle.CHAT_NETWORK_LAG_FIX.getBooleanValue()) {
            ChatMessageHandler.getInstance().onGameMessage(packet);
        } else {
            clientPlayPacketListener.onGameMessage(packet);
        }
    }
}
