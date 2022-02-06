package com.plusls.ommc.feature.sortInventory;

import com.plusls.ommc.config.Configs;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SortInventoryUtil {
    private static boolean allShulkerBox;

    @Nullable
    public static Pair<Integer, Integer> getSortRange(ScreenHandler screenHandler, Slot mouseSlot) {
        int mouseIdx = mouseSlot.id;
        if (mouseIdx == 0 && mouseSlot.getIndex() != 0) {
            mouseIdx = mouseSlot.getIndex();
        }

        int l = mouseIdx, r = mouseIdx + 1;

        Class<?> clazz = screenHandler.slots.get(mouseIdx).inventory.getClass();
        for (int i = mouseIdx - 1; i >= 0; --i) {
            if (clazz != screenHandler.slots.get(i).inventory.getClass()) {
                l = i + 1;
                break;
            } else if (i == 0) {
                l = 0;
            }
        }
        for (int i = mouseIdx + 1; i < screenHandler.slots.size(); ++i) {
            if (clazz != screenHandler.slots.get(i).inventory.getClass()) {
                r = i;
                break;
            } else if (i == screenHandler.slots.size() - 1) {
                r = screenHandler.slots.size();
            }
        }

        if (mouseSlot.inventory instanceof PlayerInventory) {
            if (l == 5 && r == 46) {
                if (mouseIdx >= 9 && mouseIdx < 36) {
                    return new Pair<>(9, 36);
                } else if (mouseIdx >= 36 && mouseIdx < 45) {
                    return new Pair<>(36, 45);
                }
                return null;
            } else if (r - l == 36) {
                if (mouseIdx >= l && mouseIdx < l + 27) {
                    return new Pair<>(l, l + 27);
                } else {
                    return new Pair<>(l + 27, r);
                }
            }
        }


        if (l + 1 == r) {
            return null;
        }
        return new Pair<>(l, r);
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
        Pair<Integer, Integer> sortRange = getSortRange(screenHandler, mouseSlot);
        if (sortRange == null) {
            return false;
        }
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        ItemStack cursorStack = screenHandler.getCursorStack().copy();

        for (int i = 0; i < screenHandler.slots.size(); ++i) {
            itemStacks.add(screenHandler.slots.get(i).getStack().copy());
        }
        ArrayList<Integer> mergeQueue = mergeItems(cursorStack, itemStacks, sortRange.getLeft(), sortRange.getRight());
        ArrayList<Pair<Integer, Integer>> swapQueue = quickSort(itemStacks, sortRange.getLeft(), sortRange.getRight());

        doClick(player, screenHandler.syncId, client.interactionManager, mergeQueue, swapQueue);
        return !mergeQueue.isEmpty() || !swapQueue.isEmpty();
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
            interactionManager.clickSlot(syncId, slotIdPair.getLeft(), 0, SlotActionType.PICKUP, player);
            interactionManager.clickSlot(syncId, slotIdPair.getRight(), 0, SlotActionType.PICKUP, player);
            interactionManager.clickSlot(syncId, slotIdPair.getLeft(), 0, SlotActionType.PICKUP, player);
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

    private static ArrayList<Pair<Integer, Integer>> quickSort(ArrayList<ItemStack> itemStacks, int l, int r) {
        // sort [l, r)
        ArrayList<Pair<Integer, Integer>> ret = new ArrayList<>();
        ArrayList<ItemStack> sortedItemStacks = new ArrayList<>();
        allShulkerBox = true;
        for (int i = l; i < r; ++i) {
            ItemStack itemStack = itemStacks.get(i);
            if (!itemStack.isEmpty() && !ShulkerBoxItemUtil.isShulkerBoxBlockItem(itemStack)) {
                allShulkerBox = false;
            }
            sortedItemStacks.add(itemStack);
        }
        sortedItemStacks.sort(new ItemStackComparator());
        // 倒序遍历来确保少的方块放在后面，多的方块放在前面
        for (int i = r - 1; i >= l; --i) {
            ItemStack dstStack = sortedItemStacks.get(i - l);
            int dstIdx = -1;
            if (itemStacks.get(i) != dstStack) {
                for (int j = l; j < r; ++j) {
                    if (itemStacks.get(j) == dstStack) {
                        dstIdx = j;
                        break;
                    }
                }
                if (dstIdx == -1) {
                    // wtf???
                    continue;
                }
                if (itemStacks.get(i).getCount() < dstStack.getCount()) {
                    ret.add(new Pair<>(dstIdx, i));
                } else {
                    ret.add(new Pair<>(i, dstIdx));
                }
                itemStacks.set(dstIdx, itemStacks.get(i));
                itemStacks.set(i, dstStack);
            }
        }
        return ret;
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
            ArrayList<Integer> addItemStackClickList = addItemStack(itemStacks, stack, l, i + 1);
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

    static class ItemStackComparator implements Comparator<ItemStack> {
        @Override
        public int compare(ItemStack a, ItemStack b) {
            int aId = getItemId(a);
            int bId = getItemId(b);
            if (Configs.Generic.SORT_INVENTORY_SHULKER_BOX_LAST.getOptionListValue() == Configs.Generic.SortInventoryShulkerBoxLastType.TRUE ||
                    (Configs.Generic.SORT_INVENTORY_SHULKER_BOX_LAST.getOptionListValue() == Configs.Generic.SortInventoryShulkerBoxLastType.AUTO && !allShulkerBox)) {
                if (ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && !ShulkerBoxItemUtil.isShulkerBoxBlockItem(b)) {
                    return 1;
                } else if (!ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && ShulkerBoxItemUtil.isShulkerBoxBlockItem(b)) {
                    return -1;
                }
            }
            if (ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && ShulkerBoxItemUtil.isShulkerBoxBlockItem(b) && a.getItem() == b.getItem()) {
                return -ShulkerBoxItemUtil.cmpShulkerBox(a.getNbt(), b.getNbt());
            }
            if (a.isEmpty() && !b.isEmpty()) {
                return 1;
            } else if (!a.isEmpty() && b.isEmpty()) {
                return -1;
            } else if (a.isEmpty()) {
                return 0;
            }
            if (aId == bId) {
                // 有 nbt 标签的排在前面
                if (!a.hasNbt() && b.hasNbt()) {
                    return 1;
                } else if (a.hasNbt() && !b.hasNbt()) {
                    return -1;
                } else if (a.hasNbt()) {
                    // 如果都有 nbt 的话，确保排序后相邻的物品 nbt 标签一致
                    int nbtRet = Long.signum(((long) Objects.requireNonNull(a.getNbt()).hashCode() - Objects.requireNonNull(b.getNbt()).hashCode()));
                    if (nbtRet != 0) {
                        return nbtRet;
                    }
                }
                // 物品少的排在后面
                return b.getCount() - a.getCount();
            }
            return aId - bId;
        }
    }
}
