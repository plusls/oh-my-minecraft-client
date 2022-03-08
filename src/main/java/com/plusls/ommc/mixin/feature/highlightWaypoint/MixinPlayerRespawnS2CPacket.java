package com.plusls.ommc.mixin.feature.highlightWaypoint;

import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointUtil;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRespawnS2CPacket.class)
public class MixinPlayerRespawnS2CPacket {
    @Inject(method = "apply*", at = @At(value = "HEAD"))
    void postApply(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo ci) {
        HighlightWaypointUtil.postRespawn((PlayerRespawnS2CPacket) (Object) this);
    }
}
