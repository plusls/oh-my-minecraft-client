package com.plusls.ommc.feature.autoSwitchElytra;

import com.plusls.ommc.ModInfo;
import com.plusls.ommc.feature.sortInventory.SortInventoryUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

import java.util.ArrayList;
import java.util.function.Predicate;

public class AutoSwitchElytraUtil {
    public static final int CHEST_SLOT_IDX = 6;

    public static boolean myCheckFallFlying(PlayerEntity player) {
        if (!player.isOnGround() && !player.isFallFlying() && !player.isTouchingWater() && !player.hasStatusEffect(StatusEffects.LEVITATION)) {
            return true;
        }
        return false;
    }

    public static void autoSwitch(int sourceSlot, MinecraftClient client, ClientPlayerEntity clientPlayerEntity, Predicate<ItemStack> check) {
        if (client.interactionManager == null) {
            return;
        }
        ScreenHandler screenHandler = clientPlayerEntity.currentScreenHandler;
        ArrayList<Integer> clickQueue = new ArrayList<>();
        ItemStack cursorStack = clientPlayerEntity.inventory.getCursorStack().copy();
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        int playerInventoryStartIdx = SortInventoryUtil.getPlayerInventoryStartIdx(screenHandler);
        for (int i = 0; i < screenHandler.slots.size(); ++i) {
            itemStacks.add(screenHandler.slots.get(i).getStack().copy());
        }
        if (!cursorStack.isEmpty()) {
            // 把鼠标的物品放到玩家仓库中
            clickQueue.addAll(SortInventoryUtil.addItemStack(itemStacks, cursorStack, playerInventoryStartIdx, screenHandler.slots.size()));
        }
        if (!cursorStack.isEmpty()) {
            // 放不下了就扔出去
            clickQueue.add(SortInventoryUtil.EMPTY_SPACE_SLOT_INDEX);
        }
        int idxToSwitch = -1;
        for (int i = 0; i < itemStacks.size(); ++i) {
            if (check.test(itemStacks.get(i))) {
                idxToSwitch = i;
                break;
            }
        }
        boolean sourceSlotEmpty = itemStacks.get(sourceSlot).isEmpty();
        if (idxToSwitch != -1) {
            clickQueue.add(idxToSwitch);
            clickQueue.add(sourceSlot);
            if (!sourceSlotEmpty) {
                clickQueue.add(idxToSwitch);
            }
            SortInventoryUtil.doClick(clientPlayerEntity, screenHandler.syncId, client.interactionManager, clickQueue);
        }
    }
}
