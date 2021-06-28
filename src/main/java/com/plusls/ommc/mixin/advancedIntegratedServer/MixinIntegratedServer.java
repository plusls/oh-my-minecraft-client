package com.plusls.ommc.mixin.advancedIntegratedServer;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.plusls.ommc.config.Configs;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.net.Proxy;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer extends MinecraftServer {
    public MixinIntegratedServer(Thread serverThread, DynamicRegistryManager.Impl registryManager, LevelStorage.Session session, SaveProperties saveProperties, ResourcePackManager dataPackManager, Proxy proxy, DataFixer dataFixer, ServerResourceManager serverResourceManager, @Nullable MinecraftSessionService sessionService, @Nullable GameProfileRepository gameProfileRepo, @Nullable UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super(serverThread, registryManager, session, saveProperties, dataPackManager, proxy, dataFixer, serverResourceManager, sessionService, gameProfileRepo, userCache, worldGenerationProgressListenerFactory);
    }

    @ModifyArg(method = "setupServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;setOnlineMode(Z)V", ordinal = 0), index = 0)
    private boolean modifySetOnlineModeArg(boolean onlineMode) {
        return Configs.AdvancedIntegratedServer.ONLINE_MODE.getBooleanValue();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;setPvpEnabled(Z)V", ordinal = 0), index = 0)
    private boolean modifySetPvpEnabledArg(boolean arg) {
        return Configs.AdvancedIntegratedServer.PVP.getBooleanValue();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;setFlightEnabled(Z)V", ordinal = 0), index = 0)
    private boolean modifySetFlightEnabledArg(boolean arg) {
        return Configs.AdvancedIntegratedServer.FLIGHT.getBooleanValue();
    }

}
