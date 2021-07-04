package com.plusls.ommc.compat.canvas.mixin;

import com.plusls.ommc.compat.Dependencies;
import com.plusls.ommc.compat.Dependency;
import com.plusls.ommc.feature.worldEaterMineHelper.CustomBakedModels;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.function.Supplier;

@Dependencies(dependencyList = @Dependency(modId = "canvas", version = ">=1.0.1511"))
@Pseudo
@Mixin(targets = "grondag.canvas.apiimpl.rendercontext.TerrainRenderContext", remap = false)
public abstract class MixinTerrainRenderContext {

    Field ommc_randomSupplierField;

    Field ommc_regionField;

    boolean ommc_first = true;

    private void ommc_init() {
        Class<?> terrainRenderContext;
        try {
            terrainRenderContext = Class.forName("grondag.canvas.apiimpl.rendercontext.TerrainRenderContext");
            ommc_randomSupplierField = terrainRenderContext.getField("randomSupplier");
            ommc_regionField = terrainRenderContext.getField("region");
            ommc_randomSupplierField.setAccessible(true);
            ommc_regionField.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot init MixinTerrainRenderContext.");
        }
    }

    @SuppressWarnings({"unchecked"})
    @Inject(method = "renderInner", at = @At(value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads", ordinal = 0))
    private void emitCustomBlockQuads(BlockState blockState, BlockPos blockPos, boolean defaultAo, FabricBakedModel model, MatrixStack matrixStack, CallbackInfo ci) {
        if (ommc_first) {
            ommc_init();
        }
        Block block = blockState.getBlock();
        if (CustomBakedModels.shouldUseCustomModel(block, blockPos)) {
            FabricBakedModel customModel = (FabricBakedModel) CustomBakedModels.models.get(block);
            if (customModel != null) {
                try {
                    customModel.emitBlockQuads((BlockRenderView) ommc_regionField.get(this), blockState, blockPos, (Supplier<Random>) ommc_randomSupplierField.get(this), (RenderContext) this);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new IllegalStateException("IllegalAccessException in MixinTerrainRenderContext.");
                }
            }
        }
    }
}
