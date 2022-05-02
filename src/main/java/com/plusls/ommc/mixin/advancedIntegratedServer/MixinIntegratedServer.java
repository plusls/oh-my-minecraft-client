package com.plusls.ommc.mixin.advancedIntegratedServer;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.plusls.ommc.config.Configs;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.net.Proxy;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer extends MinecraftServer {

    public MixinIntegratedServer(Thread serverThread, LevelStorageSource.LevelStorageAccess session, PackRepository dataPackManager, WorldStem saveLoader, Proxy proxy, DataFixer dataFixer, @Nullable MinecraftSessionService sessionService, @Nullable GameProfileRepository gameProfileRepo, @Nullable GameProfileCache userCache, ChunkProgressListenerFactory worldGenerationProgressListenerFactory) {
        super(serverThread, session, dataPackManager, saveLoader, proxy, dataFixer, sessionService, gameProfileRepo, userCache, worldGenerationProgressListenerFactory);
    }

    @ModifyArg(method = "initServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;setUsesAuthentication(Z)V", ordinal = 0), index = 0)
    private boolean modifySetOnlineModeArg(boolean onlineMode) {
        return Configs.AdvancedIntegratedServer.ONLINE_MODE.getBooleanValue();
    }

    @ModifyArg(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;setPvpAllowed(Z)V", ordinal = 0), index = 0)
    private boolean modifySetPvpEnabledArg(boolean arg) {
        return Configs.AdvancedIntegratedServer.PVP.getBooleanValue();
    }

    @ModifyArg(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;setFlightAllowed(Z)V", ordinal = 0), index = 0)
    private boolean modifySetFlightEnabledArg(boolean arg) {
        return Configs.AdvancedIntegratedServer.FLIGHT.getBooleanValue();
    }

}
