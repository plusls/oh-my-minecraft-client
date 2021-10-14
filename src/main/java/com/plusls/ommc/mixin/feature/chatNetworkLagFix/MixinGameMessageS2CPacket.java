package com.plusls.ommc.mixin.feature.chatNetworkLagFix;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.Util;
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
            Util.getIoWorkerExecutor().execute(() -> {
                MinecraftClient client = MinecraftClient.getInstance();
                if (!client.getSocialInteractionsManager().isPlayerBlocked(packet.getSender()) &&
                        !client.getSocialInteractionsManager()
                                .isPlayerBlocked(client.inGameHud.extractSender(packet.getMessage()))) {
                    client.executeTask(() ->
                            client.inGameHud.addChatMessage(packet.getLocation(), packet.getMessage(), packet.getSender())
                    );
                }
            });
        } else {
            clientPlayPacketListener.onGameMessage(packet);
        }
    }
}
