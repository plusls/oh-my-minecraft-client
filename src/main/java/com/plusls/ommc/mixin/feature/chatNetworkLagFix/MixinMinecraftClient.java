package com.plusls.ommc.mixin.feature.chatNetworkLagFix;

import com.plusls.ommc.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Redirect(
            method = "shouldBlockMessages",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/SocialInteractionsManager;" +
                            "isPlayerMuted(Ljava/util/UUID;)Z"
            )
    )
    public boolean skipCheck(SocialInteractionsManager socialInteractionsManager, UUID uuid) {
        if (Configs.FeatureToggle.CHAT_NETWORK_LAG_FIX.getBooleanValue()) {
            return false;
        }
        return socialInteractionsManager.isPlayerBlocked(uuid);
    }
}
