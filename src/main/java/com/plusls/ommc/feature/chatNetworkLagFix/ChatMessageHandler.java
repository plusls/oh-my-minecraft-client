package com.plusls.ommc.feature.chatNetworkLagFix;

import io.netty.util.internal.ConcurrentSet;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.Util;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ChatMessageHandler implements HudRenderCallback {
    private static final ChatMessageHandler INSTANCE = new ChatMessageHandler();
    private final Collection<BlockListCheckContext> messages = new ConcurrentSet<>();
    private CompletableFuture<?> initBlockListLoader = CompletableFuture.completedFuture(null);

    public static ChatMessageHandler getInstance() {
        return ChatMessageHandler.INSTANCE;
    }

    public void init() {
        HudRenderCallback.EVENT.register(this);
    }

    public void onGameMessage(GameMessageS2CPacket packet) {
        this.messages.add(new BlockListCheckContext(packet));
    }

    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler == null) {
            this.messages.clear();
        }
        this.messages.stream()
                .filter((context) -> context.checker.isDone())
                .forEach((context) -> {
                    if (!context.checker.getNow(false)) {
                        handler.onGameMessage(context.packet);
                    }
                    this.messages.remove(context);
                });
    }

    static class BlockListCheckContext {
        public final CompletableFuture<Boolean> checker;
        public final GameMessageS2CPacket packet;

        BlockListCheckContext(GameMessageS2CPacket packet) {
            SocialInteractionsManager userApi = MinecraftClient.getInstance().getSocialInteractionsManager();
            InGameHud inGameHud = MinecraftClient.getInstance().inGameHud;
            this.packet = packet;
            this.checker = CompletableFuture.completedFuture(false)
                    .thenApplyAsync((blocked) ->
                                    blocked |= userApi.isPlayerMuted(packet.getSender()),
                            Util.getIoWorkerExecutor()
                    )
                    .thenApplyAsync((blocked) ->
                                    blocked |= userApi.isPlayerMuted(inGameHud.extractSender(packet.getMessage())),
                            Util.getIoWorkerExecutor()
                    );
        }
    }
}

