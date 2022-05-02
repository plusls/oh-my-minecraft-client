package com.plusls.ommc.mixin.feature.highlightWaypoint;

import com.plusls.ommc.feature.highlithtWaypoint.HighlightWaypointUtil;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundRespawnPacket.class)
public class MixinPlayerRespawnS2CPacket {
    @Inject(method = "handle*", at = @At(value = "HEAD"))
    void postApply(ClientGamePacketListener clientPlayPacketListener, CallbackInfo ci) {
        HighlightWaypointUtil.postRespawn((ClientboundRespawnPacket) (Object) this);
    }
}
