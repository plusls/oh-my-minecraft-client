package com.plusls.ommc.feature.autoSwitchElytra;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.function.Predicate;

public class AutoSwitchElytraUtil {
    public static final int CHEST_SLOT_IDX = 6;

    public static boolean myCheckFallFlying(PlayerEntity player) {
        return !player.isOnGround() && !player.isFallFlying() && !player.isTouchingWater() && !player.hasStatusEffect(StatusEffects.LEVITATION);
    }

    public static void autoSwitch(int sourceSlot, MinecraftClient client, ClientPlayerEntity clientPlayerEntity, Predicate<ItemStack> check) {
        if (client.interactionManager == null) {
            return;
        }
        if (clientPlayerEntity.currentScreenHandler != clientPlayerEntity.playerScreenHandler) {
            clientPlayerEntity.closeHandledScreen();
        }
        ScreenHandler screenHandler = clientPlayerEntity.currentScreenHandler;
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
