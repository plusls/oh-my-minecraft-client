package com.plusls.ommc.util;

import com.plusls.ommc.config.Configs;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class ShulkerBoxItemUtil {
    public static final int SHULKERBOX_MAX_STACK_AMOUNT = 64;

    public static boolean isEmptyShulkerBoxItem(ItemStack itemStack) {
        if (itemStack.getItem() instanceof BlockItem &&
                ((BlockItem) itemStack.getItem()).getBlock() instanceof ShulkerBoxBlock) {
            NbtCompound nbt = itemStack.getTag();
            if (nbt != null && nbt.contains("BlockEntityTag", 10)) {
                NbtCompound tag = nbt.getCompound("BlockEntityTag");
                if (tag.contains("Items", 9)) {
                    NbtList tagList = tag.getList("Items", 10);
                    return tagList.size() <= 0;
                }
            }
            return true;
        } else {
            return false;
        }
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