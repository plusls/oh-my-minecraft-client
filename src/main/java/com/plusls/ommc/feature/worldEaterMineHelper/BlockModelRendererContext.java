package com.plusls.ommc.feature.worldEaterMineHelper;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockModelRendererContext {
    public BlockPos pos;
    public BlockState state;

    public void clear() {
        pos = null;
        state = null;
    }
}
