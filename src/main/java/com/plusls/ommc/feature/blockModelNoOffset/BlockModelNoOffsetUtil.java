package com.plusls.ommc.feature.blockModelNoOffset;

import com.plusls.ommc.config.Configs;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

public class BlockModelNoOffsetUtil {
    public static Vec3d blockModelNoOffset(BlockState blockState, BlockView world, BlockPos pos) {
        if (shouldNoOffset(blockState)) {
            return Vec3d.ZERO;
        } else {
            return blockState.getModelOffset(world, pos);
        }
    }

    public static boolean shouldNoOffset(BlockState blockState) {
        if (!Configs.FeatureToggle.BLOCK_MODEL_NO_OFFSET.getBooleanValue()) {
            return false;
        }
        String blockId = Registry.BLOCK.getId(blockState.getBlock()).toString();
        String blockName = blockState.getBlock().getName().getString();

        if (Configs.Lists.BLOCK_MODEL_NO_OFFSET_LIST_TYPE.getOptionListValue() == UsageRestriction.ListType.WHITELIST) {
            if (Configs.Lists.BLOCK_MODEL_NO_OFFSET_WHITELIST.getStrings().stream().anyMatch(s -> blockId.contains(s) || blockName.contains(s))) {
                return true;
            }
        } else if (Configs.Lists.BLOCK_MODEL_NO_OFFSET_LIST_TYPE.getOptionListValue() == UsageRestriction.ListType.BLACKLIST) {
            if (Configs.Lists.BLOCK_MODEL_NO_OFFSET_BLACKLIST.getStrings().stream().anyMatch(s -> !blockId.contains(s) && !blockName.contains(s))) {
                return true;
            }
            return true;
        }
        return false;
    }
}
