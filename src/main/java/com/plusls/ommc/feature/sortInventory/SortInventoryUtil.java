package com.plusls.ommc.feature.sortInventory;

import com.mojang.blaze3d.platform.Window;
import com.plusls.ommc.config.Configs;
import com.plusls.ommc.mixin.accessor.AccessorAbstractContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.hendrixshen.magiclib.compat.minecraft.api.world.item.ItemStackCompatApi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SortInventoryUtil {

    // from AbstractContainerMenu.SLOT_CLICKED_OUTSIDE
    final public static int SLOT_CLICKED_OUTSIDE = -999;

    private static boolean allShulkerBox;

    @Nullable
    public static Tuple<Integer, Integer> getSortRange(AbstractContainerMenu screenHandler, Slot mouseSlot) {
        int mouseIdx = mouseSlot.index;
        if (mouseIdx == 0 && mouseSlot.getContainerSlot() != 0) {
            mouseIdx = mouseSlot.getContainerSlot();
        }

        int l = mouseIdx, r = mouseIdx + 1;

        Class<?> clazz = screenHandler.slots.get(mouseIdx).container.getClass();
        for (int i = mouseIdx - 1; i >= 0; --i) {
            if (clazz != screenHandler.slots.get(i).container.getClass()) {
                l = i + 1;
                break;
            } else if (i == 0) {
                l = 0;
            }
        }
        for (int i = mouseIdx + 1; i < screenHandler.slots.size(); ++i) {
            if (clazz != screenHandler.slots.get(i).container.getClass()) {
                r = i;
                break;
            } else if (i == screenHandler.slots.size() - 1) {
                r = screenHandler.slots.size();
            }
        }

        if (mouseSlot.container instanceof Inventory) {
            if (l == 5 && r == 46) {
                if (mouseIdx >= 9 && mouseIdx < 36) {
                    return new Tuple<>(9, 36);
                } else if (mouseIdx >= 36 && mouseIdx < 45) {
                    return new Tuple<>(36, 45);
                }
                return null;
            } else if (r - l == 36) {
                if (mouseIdx >= l && mouseIdx < l + 27) {
                    return new Tuple<>(l, l + 27);
                } else {
                    return new Tuple<>(l + 27, r);
                }
            }
        }


        if (l + 1 == r) {
            return null;
        }
        return new Tuple<>(l, r);
    }

    public static boolean sort() {
        Minecraft client = Minecraft.getInstance();
        if (!(client.screen instanceof AbstractContainerScreen<?>)) {
            return false;
        }
        AbstractContainerScreen<?> handledScreen = (AbstractContainerScreen<?>) client.screen;
        Window window = client.getWindow();
        double x = client.mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth();
        double y = client.mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight();
        Slot mouseSlot = ((AccessorAbstractContainerScreen) handledScreen).invokeFindSlot(x, y);
        if (mouseSlot == null) {
            return false;
        }

        LocalPlayer player = client.player;
        if (client.gameMode == null || player == null) {
            return false;
        }
        AbstractContainerMenu screenHandler = player.containerMenu;
        Tuple<Integer, Integer> sortRange = getSortRange(screenHandler, mouseSlot);
        if (sortRange == null) {
            return false;
        }
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        //#if MC > 11605
        ItemStack cursorStack = screenHandler.getCarried().copy();
        //#else
        //$$ ItemStack cursorStack = player.inventory.getCarried().copy();
        //#endif

        for (int i = 0; i < screenHandler.slots.size(); ++i) {
            itemStacks.add(screenHandler.slots.get(i).getItem().copy());
        }
        ArrayList<Integer> mergeQueue = mergeItems(cursorStack, itemStacks, sortRange.getA(), sortRange.getB());
        ArrayList<Tuple<Integer, Integer>> swapQueue = quickSort(itemStacks, sortRange.getA(), sortRange.getB());

        doClick(player, screenHandler.containerId, client.gameMode, mergeQueue, swapQueue);
        return !mergeQueue.isEmpty() || !swapQueue.isEmpty();
    }

    public static void doClick(Player player, int syncId, @NotNull MultiPlayerGameMode interactionManager, List<Integer> mergeQueue, List<Tuple<Integer, Integer>> swapQueue) {
        for (Integer slotId : mergeQueue) {
            if (slotId < 0 && slotId != SLOT_CLICKED_OUTSIDE) {
                // 放入打捆包需要右键
                interactionManager.handleInventoryMouseClick(syncId, -slotId, 1, ClickType.PICKUP, player);
            } else {
                interactionManager.handleInventoryMouseClick(syncId, slotId, 0, ClickType.PICKUP, player);
            }
        }
        for (Tuple<Integer, Integer> slotIdPair : swapQueue) {
            interactionManager.handleInventoryMouseClick(syncId, slotIdPair.getA(), 0, ClickType.PICKUP, player);
            interactionManager.handleInventoryMouseClick(syncId, slotIdPair.getB(), 0, ClickType.PICKUP, player);
            interactionManager.handleInventoryMouseClick(syncId, slotIdPair.getA(), 0, ClickType.PICKUP, player);
        }
    }

    private static boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() &&
                ItemStackCompatApi.isSameItemSameTags(existingStack, stack) &&
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
                    stack.grow(stackToAdd.getCount());
                    stackToAdd.shrink(stackToAdd.getCount());
                    break;
                } else {
                    stack.grow(addNum);
                    stackToAdd.shrink(addNum);
                }
            }
            // TODO
//            else if (stack.getItem() instanceof BundleItem) {
//                CompoundTag nbtCompound = stack.getOrCreateTag();
//                ListTag nbtList = nbtCompound.getList("Items", Tag.TAG_COMPOUND);
//                Optional<CompoundTag> optional = BundleItem.getMatchingItem(stackToAdd, nbtList);
//                if (optional.isPresent()) {
//                    stackToAdd.shrink(BundleItem.add(stack, stackToAdd));
//                    ret.add(-i);
//                    if (stackToAdd.isEmpty()) {
//                        break;
//                    }
//                }
//            }
        }
        return ret;
    }

    private static int getItemId(ItemStack itemStack) {
        return Item.getId(itemStack.getItem());
    }

    private static ArrayList<Tuple<Integer, Integer>> quickSort(ArrayList<ItemStack> itemStacks, int l, int r) {
        // sort [l, r)
        ArrayList<Tuple<Integer, Integer>> ret = new ArrayList<>();
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
                    ret.add(new Tuple<>(dstIdx, i));
                } else {
                    ret.add(new Tuple<>(i, dstIdx));
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
            if (Configs.sortInventoryShulkerBoxLast == Configs.SortInventoryShulkerBoxLastType.TRUE ||
                    (Configs.sortInventoryShulkerBoxLast == Configs.SortInventoryShulkerBoxLastType.AUTO && !allShulkerBox)) {
                if (ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && !ShulkerBoxItemUtil.isShulkerBoxBlockItem(b)) {
                    return 1;
                } else if (!ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && ShulkerBoxItemUtil.isShulkerBoxBlockItem(b)) {
                    return -1;
                }
            }
            if (ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && ShulkerBoxItemUtil.isShulkerBoxBlockItem(b) && a.getItem() == b.getItem()) {
                return -ShulkerBoxItemUtil.cmpShulkerBox(a.getTag(), b.getTag());
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
                if (!a.hasTag() && b.hasTag()) {
                    return 1;
                } else if (a.hasTag() && !b.hasTag()) {
                    return -1;
                } else if (a.hasTag()) {
                    // 如果都有 nbt 的话，确保排序后相邻的物品 nbt 标签一致
                    int nbtRet = Long.signum(((long) Objects.requireNonNull(a.getTag()).hashCode() - Objects.requireNonNull(b.getTag()).hashCode()));
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
