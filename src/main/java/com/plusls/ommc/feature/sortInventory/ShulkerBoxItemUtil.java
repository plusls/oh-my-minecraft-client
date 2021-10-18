package com.plusls.ommc.feature.sortInventory;

import com.plusls.ommc.config.Configs;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.jetbrains.annotations.Nullable;

public class ShulkerBoxItemUtil {
    public static final int SHULKERBOX_MAX_STACK_AMOUNT = 64;

    public static boolean isEmptyShulkerBoxItem(ItemStack itemStack) {
        if (isShulkerBoxBlockItem(itemStack)) {
            CompoundTag nbt = itemStack.getTag();
            if (nbt != null && nbt.contains("BlockEntityTag", 10)) {
                CompoundTag tag = nbt.getCompound("BlockEntityTag");
                if (tag.contains("Items", 9)) {
                    ListTag tagList = tag.getList("Items", 10);
                    return tagList.size() <= 0;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean isShulkerBoxBlockItem(ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem && ((BlockItem) itemStack.getItem()).getBlock() instanceof ShulkerBoxBlock;
    }

    public static int cmpShulkerBox(@Nullable CompoundTag a, @Nullable CompoundTag b) {
        int aSize = 0, bSize = 0;
        if (a != null) {
            CompoundTag tag = a.getCompound("BlockEntityTag");
            if (tag.contains("Items", 9)) {
                ListTag tagList = tag.getList("Items", 10);
                aSize = tagList.size();
            }
        }

        if (b != null) {
            CompoundTag tag = b.getCompound("BlockEntityTag");
            if (tag.contains("Items", 9)) {
                ListTag tagList = tag.getList("Items", 10);
                bSize = tagList.size();
            }
        }
        return aSize - bSize;
    }

    public static int getMaxCount(ItemStack itemStack) {
        if (Configs.Generic.SORT_INVENTORY_SUPPORT_EMPTY_SHULKER_BOX_STACK.getBooleanValue() &&
                ShulkerBoxItemUtil.isEmptyShulkerBoxItem(itemStack)) {
            return ShulkerBoxItemUtil.SHULKERBOX_MAX_STACK_AMOUNT;
        } else {
            return itemStack.getMaxCount();
        }
    }

    public static boolean isStackable(ItemStack itemStack) {
        return getMaxCount(itemStack) > 1 && (!itemStack.isDamageable() || !itemStack.isDamaged());
    }
}