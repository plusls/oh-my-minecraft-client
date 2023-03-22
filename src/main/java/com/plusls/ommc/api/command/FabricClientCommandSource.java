package com.plusls.ommc.api.command;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.commands.CommandSource;

//#if MC <= 11605
//$$ import net.minecraft.client.Minecraft;
//$$ import net.minecraft.client.multiplayer.ClientLevel;
//$$ import net.minecraft.client.player.LocalPlayer;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.world.entity.Entity;
//$$ import net.minecraft.world.phys.Vec2;
//$$ import net.minecraft.world.phys.Vec3;
//#endif

// Code from https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/api/client/command/v1/FabricClientCommandSource.java

/**
 * Extensions to {@link CommandSource} for client-sided commands.
 */
@Environment(EnvType.CLIENT)
public interface FabricClientCommandSource extends CommandSource {
	//#if MC <= 11605
    //$$ /**
    //$$  * Sends a feedback message to the player.
    //$$  *
    //$$  * @param message the feedback message
    //$$  */
    //$$ void sendFeedback(Component message);
    //$$
    //$$ /**
    //$$  * Sends an error message to the player.
    //$$  *
    //$$  * @param message the error message
    //$$  */
    //$$ void sendError(Component message);
    //$$
    //$$ /**
    //$$  * Gets the client instance used to run the command.
    //$$  *
    //$$  * @return the client
    //$$  */
    //$$ Minecraft getClient();
    //$$
    //$$ /**
    //$$  * Gets the player that used the command.
    //$$  *
    //$$  * @return the player
    //$$  */
    //$$ LocalPlayer getPlayer();
    //$$
    //$$ /**
    //$$  * Gets the entity that used the command.
    //$$  *
    //$$  * @return the entity
    //$$  */
    //$$ default Entity getEntity() {
    //$$     return getPlayer();
    //$$ }
    //$$
    //$$ /**
    //$$  * Gets the position from where the command has been executed.
    //$$  *
    //$$  * @return the position
    //$$  */
    //$$ default Vec3 getPosition() {
    //$$     return getPlayer().position();
    //$$ }
    //$$
    //$$ /**
    //$$  * Gets the rotation of the entity that used the command.
    //$$  *
    //$$  * @return the rotation
    //$$  */
    //$$ default Vec2 getRotation() {
    //$$     return getPlayer().getRotationVector();
    //$$ }
    //$$
    //$$ /**
    //$$  * Gets the world where the player used the command.
    //$$  *
    //$$  * @return the world
    //$$  */
    //$$ ClientLevel getWorld();
    //$$
    //$$ /**
    //$$  * Gets the meta property under {@code key} that was assigned to this source.
    //$$  *
    //$$  * <p>This method should return the same result for every call with the same {@code key}.
    //$$  *
    //$$  * @param key the meta key
    //$$  * @return the meta
    //$$  */
    //$$ default Object getMeta(String key) {
    //$$     return null;
    //$$ }
	//#endif
}