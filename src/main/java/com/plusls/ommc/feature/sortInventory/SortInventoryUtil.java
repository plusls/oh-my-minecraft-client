package com.plusls.ommc.feature.sortInventory;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SortInventoryUtil {

    public static int getPlayerInventoryStartIdx(ScreenHandler screenHandler) {
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
        int playerInventoryStartIdx = getPlayerInventoryStartIdx(screenHandler);
        if (playerInventoryStartIdx == -1) {
            return false;
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
        int r;
        if (containerInventorySize != -1 && mouseSlot.id < containerInventorySize) {
            clickQueue.addAll(mergeItems(itemStacks, 0, containerInventorySize));
            clickQueue.addAll(mergeItems(itemStacks, 0, containerInventorySize));
            r = 0;
            for (int i = containerInventorySize - 1; i >= 0; --i) {
                if (!itemStacks.get(i).isEmpty()) {
                    r = i + 1;
                    break;
                }
            }
            clickQueue.addAll(quickSort(itemStacks, 0, r));
        } else if (mouseSlot.id >= playerInventoryStartIdx && mouseSlot.id < playerInventoryStartIdx + 27) {
            clickQueue.addAll(mergeItems(itemStacks, playerInventoryStartIdx, playerInventoryStartIdx + 27));
            clickQueue.addAll(mergeItems(itemStacks, playerInventoryStartIdx, playerInventoryStartIdx + 27));
            r = playerInventoryStartIdx;
            for (int i = playerInventoryStartIdx + 26; i >= playerInventoryStartIdx; --i) {
                if (!itemStacks.get(i).isEmpty()) {
                    r = i + 1;
                    break;
                }
            }
            clickQueue.addAll(quickSort(itemStacks, playerInventoryStartIdx, r));
        } else if (mouseSlot.id >= playerInventoryStartIdx + 27 && mouseSlot.id < playerInventoryStartIdx + 36) {
            clickQueue.addAll(mergeItems(itemStacks, playerInventoryStartIdx + 27, playerInventoryStartIdx + 36));
            clickQueue.addAll(mergeItems(itemStacks, playerInventoryStartIdx + 27, playerInventoryStartIdx + 36));
            r = playerInventoryStartIdx + 27;
            for (int i = playerInventoryStartIdx + 35; i >= playerInventoryStartIdx + 27; --i) {
                if (!itemStacks.get(i).isEmpty()) {
                    r = i + 1;
                    break;
                }
            }
            clickQueue.addAll(quickSort(itemStacks, playerInventoryStartIdx + 27, r));
        }

        doClick(player, screenHandler.syncId, client.interactionManager, clickQueue);
        return !clickQueue.isEmpty();
    }

    public static void doClick(PlayerEntity player, int syncId, @NotNull ClientPlayerInteractionManager interactionManager, List<Integer> clickQueue) {
        for (Integer slotId : clickQueue) {
            if (slotId < 0 && slotId != ScreenHandler.EMPTY_SPACE_SLOT_INDEX) {
                interactionManager.clickSlot(syncId, -slotId, 1, SlotActionType.PICKUP, player);
            } else {
                interactionManager.clickSlot(syncId, slotId, 0, SlotActionType.PICKUP, player);
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

    public static ArrayList<Integer> addItemStack(ArrayList<ItemStack> itemStacks, ItemStack stackToAdd, int l, int r) {
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
                NbtCompound nbtCompound = stack.getOrCreateNbt();
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

    private static int getItemId(ItemStack itemStack) {
        return Item.getRawId(itemStack.getItem());
    }


    private static int cmp(ItemStack a, ItemStack b) {
        int aId = getItemId(a);
        int bId = getItemId(b);
        if (ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && !ShulkerBoxItemUtil.isShulkerBoxBlockItem(b)) {
            return 1;
        } else if (!ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && ShulkerBoxItemUtil.isShulkerBoxBlockItem(b)) {
            return -1;
        } else if (ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && ShulkerBoxItemUtil.isShulkerBoxBlockItem(b)) {
            return ShulkerBoxItemUtil.cmpShulkerBox(a.getNbt(), b.getNbt());
        }
        if (aId == bId) {
            // 物品少的排在后面
            return b.getCount() - a.getCount();
        }
        return aId - bId;
    }

    private static ArrayList<Integer> quickSort(ArrayList<ItemStack> itemStacks, int l, int r) {
        ArrayList<Integer> ret = new ArrayList<>();
        int i, j;
        ItemStack p;
        ItemStack temp;

        if (l >= r - 1) {
            return ret;
        }
        p = itemStacks.get(l);
        i = l;
        j = r - 1;
        while (i < j) {
            while (cmp(itemStacks.get(j), p) >= 0 && i < j) {
                j--;
            }
            while (cmp(itemStacks.get(i), p) <= 0 && i < j) {
                i++;
            }
            if (i < j) {
                temp = itemStacks.get(i);
                itemStacks.set(i, itemStacks.get(j));
                itemStacks.set(j, temp);
                ret.add(i);
                ret.add(j);
                ret.add(i);
            }
        }
        if (l != i) {
            itemStacks.set(l, itemStacks.get(i));
            itemStacks.set(i, p);
            ret.add(l);
            ret.add(i);
            ret.add(l);
        }
        ret.addAll(quickSort(itemStacks, l, j));
        ret.addAll(quickSort(itemStacks, j + 1, r));
        return ret;
    }


    private static ArrayList<Integer> mergeItems(ArrayList<ItemStack> itemStacks, int l, int r) {
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
