package com.plusls.ommc.feature.autoSwitchElytra;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Container;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.function.Predicate;

public class AutoSwitchElytraUtil {
    public static final int CHEST_SLOT_IDX = 6;

    public static boolean myCheckFallFlying(PlayerEntity player) {
        return !player.onGround && !player.isFallFlying() && !player.isTouchingWater() && !player.hasStatusEffect(StatusEffects.LEVITATION);
    }

    public static void autoSwitch(int sourceSlot, MinecraftClient client, ClientPlayerEntity clientPlayerEntity, Predicate<ItemStack> check) {
        if (client.interactionManager == null) {
            return;
        }
        if (clientPlayerEntity.container != clientPlayerEntity.playerContainer) {
            clientPlayerEntity.closeScreen();
        }
        Container screenHandler = clientPlayerEntity.container;
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i < screenHandler.slots.size(); ++i) {
            itemStacks.add(screenHandler.slots.get(i).getStack().copy());
        }

        int idxToSwitch = -1;
        for (int i = 0; i < itemStacks.size(); ++i) {
            if (check.test(itemStacks.get(i))) {
                idxToSwitch = i;
                break;
            }
        }
        if (idxToSwitch != -1) {
            client.interactionManager.clickSlot(screenHandler.syncId, idxToSwitch, 0, SlotActionType.PICKUP, clientPlayerEntity);
            client.interactionManager.clickSlot(screenHandler.syncId, sourceSlot, 0, SlotActionType.PICKUP, clientPlayerEntity);
            client.interactionManager.clickSlot(screenHandler.syncId, idxToSwitch, 0, SlotActionType.PICKUP, clientPlayerEntity);
        }
    }
}
