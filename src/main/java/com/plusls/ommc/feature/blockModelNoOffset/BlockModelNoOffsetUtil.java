package com.plusls.ommc.feature.blockModelNoOffset;

import com.plusls.ommc.config.Configs;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BlockModelNoOffsetUtil {
    public static Vec3 blockModelNoOffset(BlockState blockState, BlockGetter world, BlockPos pos) {
        if (shouldNoOffset(blockState)) {
            return Vec3.ZERO;
        } else {
            return blockState.getOffset(world, pos);
        }
    }

    public static boolean shouldNoOffset(BlockState blockState) {
        if (!Configs.FeatureToggle.BLOCK_MODEL_NO_OFFSET.getBooleanValue()) {
            return false;
        }
        String blockId = Registry.BLOCK.getKey(blockState.getBlock()).toString();
        String blockName = blockState.getBlock().getName().getString();

        if (Configs.Lists.BLOCK_MODEL_NO_OFFSET_LIST_TYPE.getOptionListValue() == UsageRestriction.ListType.WHITELIST) {
            return Configs.Lists.BLOCK_MODEL_NO_OFFSET_WHITELIST.getStrings().stream().anyMatch(s -> blockId.contains(s) || blockName.contains(s));
        } else if (Configs.Lists.BLOCK_MODEL_NO_OFFSET_LIST_TYPE.getOptionListValue() == UsageRestriction.ListType.BLACKLIST) {
            return Configs.Lists.BLOCK_MODEL_NO_OFFSET_BLACKLIST.getStrings().stream().noneMatch(s -> blockId.contains(s) || blockName.contains(s));
        }
        return false;
    }
}
