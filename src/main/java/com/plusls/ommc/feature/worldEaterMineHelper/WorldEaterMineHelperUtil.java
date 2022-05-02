package com.plusls.ommc.feature.worldEaterMineHelper;

import com.plusls.ommc.config.Configs;
import com.plusls.ommc.mixin.accessor.AccessorBlockStateBase;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class WorldEaterMineHelperUtil {
    public static final Map<Block, BakedModel> customModels = new HashMap<>();
    public static final Map<Block, BakedModel> customFullModels = new HashMap<>();

    public static boolean blockInWorldEaterMineHelperWhitelist(Block block) {
        String blockName = block.getName().getString();
        String blockId = Registry.BLOCK.getKey(block).toString();
        return Configs.worldEaterMineHelperWhitelist.stream().anyMatch(s -> blockId.contains(s) || blockName.contains(s));
    }

    public static boolean shouldUseCustomModel(BlockState blockState, BlockPos pos) {
        Block block = blockState.getBlock();
        // ModInfo.LOGGER.debug("test model {} {}", pos, block);
        if (Configs.worldEaterMineHelper && blockInWorldEaterMineHelperWhitelist(block)) {
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                int yMax = world.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                if (y < yMax) {
                    int j = 0;
                    for (int i = y + 1; i <= yMax; ++i) {
                        if (world.getBlockState(new BlockPos(x, i, z)).getMaterial().isSolidBlocking() && j < 20) {
                            return false;
                        }
                        ++j;
                    }
                }
                // ModInfo.LOGGER.debug("update model! {} {}", pos, block);
                return true;
            }
        }
        return false;
    }

    static public void emitCustomFullBlockQuads(FabricBakedModel model, BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        Block block = state.getBlock();
        if (WorldEaterMineHelperUtil.shouldUseCustomModel(state, pos)) {
            FabricBakedModel customModel = (FabricBakedModel) WorldEaterMineHelperUtil.customFullModels.get(block);
            if (customModel != null) {
                int luminance = ((AccessorBlockStateBase) state).getLightEmission();
                ((AccessorBlockStateBase) state).setLightEmission(15);
                customModel.emitBlockQuads(blockView, state, pos, randomSupplier, context);
                ((AccessorBlockStateBase) state).setLightEmission(luminance);
                return;
            }
        }
        model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
    }

    static public void emitCustomBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        Block block = state.getBlock();
        if (WorldEaterMineHelperUtil.shouldUseCustomModel(state, pos)) {
            FabricBakedModel customModel = (FabricBakedModel) WorldEaterMineHelperUtil.customModels.get(block);
            if (customModel != null) {
                int luminance = ((AccessorBlockStateBase) state).getLightEmission();
                ((AccessorBlockStateBase) state).setLightEmission(15);
                customModel.emitBlockQuads(blockView, state, pos, randomSupplier, context);
                ((AccessorBlockStateBase) state).setLightEmission(luminance);
            }
        }
    }

}
