package com.plusls.ommc.util.command;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

// Code from https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/api/client/command/v1/FabricClientCommandSource.java

/**
 * Extensions to {@link CommandSource} for client-sided commands.
 */
@Environment(EnvType.CLIENT)
public interface FabricClientCommandSource extends CommandSource {
    /**
     * Sends a feedback message to the player.
     *
     * @param message the feedback message
     */
    void sendFeedback(Text message);

    /**
     * Sends an error message to the player.
     *
     * @param message the error message
     */
    void sendError(Text message);

    /**
     * Gets the client instance used to run the command.
     *
     * @return the client
     */
    MinecraftClient getClient();

    /**
     * Gets the player that used the command.
     *
     * @return the player
     */
    ClientPlayerEntity getPlayer();

    /**
     * Gets the entity that used the command.
     *
     * @return the entity
     */
    default Entity getEntity() {
        return getPlayer();
    }

    /**
     * Gets the position from where the command has been executed.
     *
     * @return the position
     */
    default Vec3d getPosition() {
        return getPlayer().getPos();
    }

    /**
     * Gets the rotation of the entity that used the command.
     *
     * @return the rotation
     */
    default Vec2f getRotation() {
        return getPlayer().getRotationClient();
    }

    /**
     * Gets the world where the player used the command.
     *
     * @return the world
     */
    ClientWorld getWorld();

    /**
     * Gets the meta property under {@code key} that was assigned to this source.
     *
     * <p>This method should return the same result for every call with the same {@code key}.
     *
     * @param key the meta key
     * @return the meta
     */
    default Object getMeta(String key) {
        return null;
    }
}
