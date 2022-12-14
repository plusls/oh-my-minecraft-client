package com.plusls.ommc.feature.blockModelNoOffset;

import com.plusls.ommc.config.Configs;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import net.minecraft.core.BlockPos;
//#if MC >= 11903
import net.minecraft.core.registries.BuiltInRegistries;
//#else
//$$ import net.minecraft.core.Registry;
//#endif
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
        //#if MC >= 11903
        String blockId = BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString();
        //#else
        //$$ String blockId = Registry.BLOCK.getKey(blockState.getBlock()).toString();
        //#endif
        String blockName = blockState.getBlock().getName().getString();

        if (Configs.blockModelNoOffsetListType == UsageRestriction.ListType.WHITELIST) {
            return Configs.blockModelNoOffsetWhitelist.stream().anyMatch(s -> blockId.contains(s) || blockName.contains(s));
        } else if (Configs.blockModelNoOffsetListType == UsageRestriction.ListType.BLACKLIST) {
            return Configs.blockModelNoOffsetBlacklist.stream().noneMatch(s -> blockId.contains(s) || blockName.contains(s));
        }
        return false;
    }
}
