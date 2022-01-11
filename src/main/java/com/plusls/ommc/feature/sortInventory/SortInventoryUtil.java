package com.plusls.ommc.feature.sortInventory;

import com.plusls.ommc.config.Configs;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SortInventoryUtil {

    public static int getPlayerInventoryStartIdx(ScreenHandler screenHandler, Inventory mouseSlotInventory) {
        if (screenHandler instanceof PlayerScreenHandler ||
                (screenHandler instanceof CreativeInventoryScreen.CreativeScreenHandler && mouseSlotInventory instanceof PlayerInventory)) {
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

    public static boolean sort() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.currentScreen instanceof HandledScreen<?> handledScreen)) {
            return false;
        }

        double x = client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth();
        double y = client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight();
        Slot mouseSlot = handledScreen.getSlotAt(x, y);
        if (mouseSlot == null) {
            return false;
        }

        ClientPlayerEntity player = client.player;
        if (client.interactionManager == null || player == null) {
            return false;
        }
        ScreenHandler screenHandler = player.currentScreenHandler;
        int playerInventoryStartIdx = getPlayerInventoryStartIdx(screenHandler, mouseSlot.inventory);
        if (playerInventoryStartIdx == -1) {
            return false;
        }

        ArrayList<Integer> mergeQueue = null;
        ArrayList<Pair<Integer, Integer>> swapQueue = null;
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        ItemStack cursorStack = screenHandler.getCursorStack().copy();
        int containerInventorySize = getContainerInventorySize(screenHandler);

        for (int i = 0; i < screenHandler.slots.size(); ++i) {
            itemStacks.add(screenHandler.slots.get(i).getStack().copy());
        }

        int mouseIdx = mouseSlot.id;
        if (mouseIdx == 0 && mouseSlot.getIndex() != 0) {
            mouseIdx = mouseSlot.getIndex();
        }
        if (containerInventorySize != -1 && mouseIdx < containerInventorySize) {
            // 整理容器
            mergeQueue = mergeItems(cursorStack, itemStacks, 0, containerInventorySize);
            swapQueue = quickSort(itemStacks, 0, containerInventorySize);
        } else if (mouseIdx >= playerInventoryStartIdx && mouseIdx< playerInventoryStartIdx + 27) {
            // 整理背包
            mergeQueue = mergeItems(cursorStack, itemStacks, playerInventoryStartIdx, playerInventoryStartIdx + 27);
            swapQueue = quickSort(itemStacks, playerInventoryStartIdx, playerInventoryStartIdx + 27);
        } else if (mouseIdx >= playerInventoryStartIdx + 27 && mouseIdx < playerInventoryStartIdx + 36) {
            // 整理快捷栏
            mergeQueue = mergeItems(cursorStack, itemStacks, playerInventoryStartIdx + 27, playerInventoryStartIdx + 36);
            swapQueue = quickSort(itemStacks, playerInventoryStartIdx + 27, playerInventoryStartIdx + 36);
        }
        if (mergeQueue != null) {
            doClick(player, screenHandler.syncId, client.interactionManager, mergeQueue, swapQueue);
            return !mergeQueue.isEmpty() || !swapQueue.isEmpty();
        }
        return false;
    }

    public static void doClick(PlayerEntity player, int syncId, @NotNull ClientPlayerInteractionManager interactionManager, List<Integer> mergeQueue, List<Pair<Integer, Integer>> swapQueue) {
        for (Integer slotId : mergeQueue) {
            if (slotId < 0 && slotId != ScreenHandler.EMPTY_SPACE_SLOT_INDEX) {
                // 放入打捆包需要右键
                interactionManager.clickSlot(syncId, -slotId, 1, SlotActionType.PICKUP, player);
            } else {
                interactionManager.clickSlot(syncId, slotId, 0, SlotActionType.PICKUP, player);
            }
        }
        for (Pair<Integer, Integer> slotIdPair : swapQueue) {
            interactionManager.clickSlot(syncId, slotIdPair.getLeft(), slotIdPair.getRight(), SlotActionType.SWAP, player);
        }
    }

    private static boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() &&
                ItemStack.canCombine(existingStack, stack) &&
                ShulkerBoxItemUtil.isStackable(existingStack) &&
                existingStack.getCount() < ShulkerBoxItemUtil.getMaxCount(existingStack) &&
                existingStack.getCount() < 64;
    }

    public static ArrayList<Integer> addItemStack(ArrayList<ItemStack> itemStacks, ItemStack stackToAdd, int l, int r) {
        // merge in [l, r)
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i = l; i < r; ++i) {
            ItemStack stack = itemStacks.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (canStackAddMore(stack, stackToAdd)) {
                int addNum = ShulkerBoxItemUtil.getMaxCount(stack) - stack.getCount();
                if (addNum <= 0) {
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
                NbtCompound nbtCompound = stack.getOrCreateNbt();
                NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
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

    private static int getItemId(ItemStack itemStack) {
        return Item.getRawId(itemStack.getItem());
    }

    static class ItemStackComparator implements Comparator<ItemStack> {
        @Override
        public int compare(ItemStack a, ItemStack b) {
            int aId = getItemId(a);
            int bId = getItemId(b);
            if (Configs.Generic.SORT_INVENTORY_SHULKER_BOX_LAST.getBooleanValue()) {
                if (ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && !ShulkerBoxItemUtil.isShulkerBoxBlockItem(b)) {
                    return 1;
                } else if (!ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && ShulkerBoxItemUtil.isShulkerBoxBlockItem(b)) {
                    return -1;
                }
            }
            if (ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && ShulkerBoxItemUtil.isShulkerBoxBlockItem(b) && a.getItem() == b.getItem()) {
                return ShulkerBoxItemUtil.cmpShulkerBox(a.getNbt(), b.getNbt());
            }

            if (a.isEmpty() && !b.isEmpty()) {
                return 1;
            } else if (!a.isEmpty() && b.isEmpty()) {
                return -1;
            }
            if (aId == bId) {
                // 物品少的排在后面
                return b.getCount() - a.getCount();
            }
            return aId - bId;
        }
    }


    private static ArrayList<Pair<Integer, Integer>> quickSort(ArrayList<ItemStack> itemStacks, int l, int r) {
        // sort [l, r)
        ArrayList<Pair<Integer, Integer>> ret = new ArrayList<>();
        ArrayList<ItemStack> sortedItemStacks = new ArrayList<>();
        for (int i = l; i < r; ++i) {
            sortedItemStacks.add(itemStacks.get(i));
        }
        sortedItemStacks.sort(new ItemStackComparator());
        for (int i = l; i < r; ++i) {
            ItemStack dstStack = sortedItemStacks.get(i - l);
            if (itemStacks.get(i) != dstStack) {
                int dstIdx = itemStacks.indexOf(dstStack);
                itemStacks.set(dstIdx, itemStacks.get(i));
                itemStacks.set(i, dstStack);
                ret.add(new Pair<>(i, dstIdx));
            }
        }
        return ret;
//        int i, j;
//        ItemStack p;
//        ItemStack temp;
//
//        if (l >= r - 1) {
//            return ret;
//        }
//        p = itemStacks.get(l);
//        i = l;
//        j = r - 1;
//        while (i < j) {
//            while (cmp(itemStacks.get(j), p) >= 0 && i < j) {
//                j--;
//            }
//            while (cmp(itemStacks.get(i), p) <= 0 && i < j) {
//                i++;
//            }
//            if (i < j) {
//                temp = itemStacks.get(i);
//                itemStacks.set(i, itemStacks.get(j));
//                itemStacks.set(j, temp);
//                ret.add(i);
//                ret.add(j);
//                ret.add(i);
//            }
//        }
//        if (l != i) {
//            itemStacks.set(l, itemStacks.get(i));
//            itemStacks.set(i, p);
//            ret.add(l);
//            ret.add(i);
//            ret.add(l);
//        }
//        ret.addAll(quickSort(itemStacks, l, j));
//        ret.addAll(quickSort(itemStacks, j + 1, r));
//        return ret;
    }


    private static ArrayList<Integer> mergeItems(ItemStack cursorStack, ArrayList<ItemStack> itemStacks, int l, int r) {
        ArrayList<Integer> ret = new ArrayList<>();
        // 先把手中的物品尽量的放入背包或容器中，从而保证后续的整理不会被手中物品合并而影响
        if (!cursorStack.isEmpty()) {
            ret.addAll(addItemStack(itemStacks, cursorStack, l, r));
        }
        for (int i = r - 1; i >= l; --i) {
            ItemStack stack = itemStacks.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            itemStacks.set(i, new ItemStack(Blocks.AIR));
            ArrayList<Integer> addItemStackClickList = addItemStack(itemStacks, stack, l, r);
            if (!addItemStackClickList.isEmpty()) {
                ret.add(i);
                ret.addAll(addItemStackClickList);
                if (!stack.isEmpty()) {
                    ret.add(i);
                    itemStacks.set(i, stack);
                } else if (!cursorStack.isEmpty()) {
                    itemStacks.set(i, cursorStack);
                }
            } else {
                itemStacks.set(i, stack);
            }
        }
        // 在合并完后如果鼠标还有物品则尝试把鼠标的物品放进容器或箱子
        if (!cursorStack.isEmpty()) {
            for (int i = l; i < r; ++i) {
                if (itemStacks.get(i).isEmpty()) {
                    ret.add(i);
                    itemStacks.set(i, cursorStack.copy());
                    cursorStack.setCount(0);
                    break;
                }
            }
        }

        return ret;
    }
}
