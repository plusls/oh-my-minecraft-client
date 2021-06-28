package com.plusls.ommc.feature.sortInventory;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.Optional;

public class SortInventoryUtil {

    private static int getPlayerInventoryStartIdx(ScreenHandler screenHandler) {
        if (screenHandler instanceof PlayerScreenHandler) {
            return 9;
        } else if (screenHandler instanceof CraftingScreenHandler) {
            return 10;
        } else {
            return getContainerInventorySize(screenHandler);
        }

    }

    private static int getContainerInventorySize(ScreenHandler screenHandler) {
        if (screenHandler instanceof Generic3x3ContainerScreenHandler ||
                screenHandler instanceof GenericContainerScreenHandler ||
                screenHandler instanceof HopperScreenHandler ||
                screenHandler instanceof ShulkerBoxScreenHandler
        ) {
            return screenHandler.getSlot(0).inventory.size();
        }
        return -1;
    }

    public static void sort() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (client.interactionManager == null || player == null) {
            return;
        }
        ScreenHandler screenHandler = player.currentScreenHandler;
        int playerInventoryStartIdx = getPlayerInventoryStartIdx(screenHandler);
        if (playerInventoryStartIdx == -1) {
            return;
        }

        ArrayList<Integer> clickQueue = new ArrayList<>();
        ItemStack cursorStack = screenHandler.getCursorStack().copy();
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        int containerInventorySize = getContainerInventorySize(screenHandler);

        for (int i = 0; i < screenHandler.slots.size(); ++i) {
            itemStacks.add(screenHandler.slots.get(i).getStack().copy());
        }
        if (!cursorStack.isEmpty()) {
            // 把鼠标的物品放到玩家仓库中
            clickQueue.addAll(addItemStack(itemStacks, cursorStack, playerInventoryStartIdx, screenHandler.slots.size()));
        }
        if (!cursorStack.isEmpty() && containerInventorySize != -1) {
            // 把鼠标的物品放到仓库
            clickQueue.addAll(addItemStack(itemStacks, cursorStack, 0, containerInventorySize));
        }
        if (!cursorStack.isEmpty()) {
            // 放不下了就扔出去
            clickQueue.add(ScreenHandler.EMPTY_SPACE_SLOT_INDEX);
        }
        // 执行两次，第一次合并同类项，第二次去除空位
        if (containerInventorySize != -1) {
            clickQueue.addAll(sortInventory(itemStacks, 0, containerInventorySize));
            clickQueue.addAll(sortInventory(itemStacks, 0, containerInventorySize));
        }
        clickQueue.addAll(sortInventory(itemStacks, playerInventoryStartIdx, playerInventoryStartIdx + 27));
        clickQueue.addAll(sortInventory(itemStacks, playerInventoryStartIdx, playerInventoryStartIdx + 27));
        for (Integer slotId : clickQueue) {
            if (slotId < 0 && slotId != ScreenHandler.EMPTY_SPACE_SLOT_INDEX) {
                client.interactionManager.clickSlot(screenHandler.syncId, -slotId, 1, SlotActionType.PICKUP, player);
            } else {
                client.interactionManager.clickSlot(screenHandler.syncId, slotId, 0, SlotActionType.PICKUP, player);
            }
        }
    }

    private static boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() &&
                ItemStack.canCombine(existingStack, stack) &&
                ShulkerBoxItemUtil.isStackable(existingStack) &&
                existingStack.getCount() < ShulkerBoxItemUtil.getMaxCount(existingStack) &&
                existingStack.getCount() < 64;
    }

    private static ArrayList<Integer> addItemStack(ArrayList<ItemStack> itemStacks, ItemStack stackToAdd, int l, int r) {
        // merge in [l, r)
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i = l; i < r; ++i) {
            ItemStack stack = itemStacks.get(i);
            if (stack.isEmpty()) {
                itemStacks.set(i, stackToAdd.copy());
                ret.add(i);
                stackToAdd.decrement(stackToAdd.getCount());
                break;
            } else if (canStackAddMore(stack, stackToAdd)) {
                int addNum = ShulkerBoxItemUtil.getMaxCount(stack) - stack.getCount();
                if (addNum == 0) {
                    continue;
                }
                ret.add(i);
                if (addNum >= stackToAdd.getCount()) {
                    stack.increment(stackToAdd.getCount());
                    stackToAdd.decrement(stackToAdd.getCount());
                    break;
                } else {
                    stack.increment(addNum);
                    stackToAdd.decrement(addNum);
                }
            } else if (stack.getItem() instanceof BundleItem) {
                NbtCompound nbtCompound = stack.getOrCreateTag();
                NbtList nbtList = nbtCompound.getList("Items", 10);
                Optional<NbtCompound> optional = BundleItem.canMergeStack(stackToAdd, nbtList);
                if (optional.isPresent()) {
                    stackToAdd.decrement(BundleItem.addToBundle(stack, stackToAdd));
                    ret.add(-i);
                    if (stackToAdd.isEmpty()) {
                        break;
                    }
                }
            }
        }
        return ret;
    }

    private static ArrayList<Integer> sortInventory(ArrayList<ItemStack> itemStacks, int l, int r) {
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i = r - 1; i >= l; --i) {
            ItemStack stack = itemStacks.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            itemStacks.set(i, new ItemStack(Blocks.AIR));
            ret.add(i);
            ret.addAll(addItemStack(itemStacks, stack, l, r));
            if (ret.size() >= 2 && ret.get(ret.size() - 1).equals(ret.get(ret.size() - 2))) {
                ret.remove(ret.size() - 1);
                ret.remove(ret.size() - 1);
            }
        }
        return ret;
    }
}
