package com.plusls.ommc.feature.worldEaterMineHelper;

import com.plusls.ommc.config.Configs;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.Heightmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class WorldEaterMineHelperUtil {
    public static final Map<Block, BakedModel> customModels = new HashMap<>();
    public static final Map<Block, BakedModel> customFullModels = new HashMap<>();

    public static boolean blockInWorldEaterMineHelperWhitelist(Block block) {
        String blockName = block.getName().getString();
        String blockId = Registry.BLOCK.getId(block).toString();
        return Configs.Lists.WORLD_EATER_MINE_HELPER_WHITELIST.getStrings().stream().anyMatch(s -> blockId.contains(s) || blockName.contains(s));
    }

    public static boolean shouldUseCustomModel(BlockState blockState, BlockPos pos) {
        Block block = blockState.getBlock();
        // ModInfo.LOGGER.debug("test model {} {}", pos, block);
        if (Configs.FeatureToggle.WORLD_EATER_MINE_HELPER.getBooleanValue() && blockInWorldEaterMineHelperWhitelist(block)) {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                int yMax = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
                if (y < yMax) {
                    int j = 0;
                    for (int i = y + 1; i <= yMax; ++i) {
                        if (world.getBlockState(new BlockPos(x, i, z)).getMaterial().blocksLight() && j < 20) {
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

    static public void emitCustomFullBlockQuads(FabricBakedModel model, BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        Block block = state.getBlock();
        if (WorldEaterMineHelperUtil.shouldUseCustomModel(state, pos)) {
            FabricBakedModel customModel = (FabricBakedModel) WorldEaterMineHelperUtil.customFullModels.get(block);
            if (customModel != null) {
                int luminance = state.luminance;
                state.luminance = 15;
                customModel.emitBlockQuads(blockView, state, pos, randomSupplier, context);
                state.luminance = luminance;
                return;
            }
        }
        model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
    }

    static public void emitCustomBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        Block block = state.getBlock();
        if (WorldEaterMineHelperUtil.shouldUseCustomModel(state, pos)) {
            FabricBakedModel customModel = (FabricBakedModel) WorldEaterMineHelperUtil.customModels.get(block);
            if (customModel != null) {
                int luminance = state.luminance;
                state.luminance = 15;
                customModel.emitBlockQuads(blockView, state, pos, randomSupplier, context);
                state.luminance = luminance;
            }
        }
    }

}
