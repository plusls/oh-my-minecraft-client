package com.plusls.ommc.mixin.feature.worldEaterMineHelper;

import com.mojang.math.Vector3f;
import com.plusls.ommc.feature.worldEaterMineHelper.WorldEaterMineHelperUtil;
import com.plusls.ommc.mixin.accessor.AccessorBlockModel;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mixin(value = BlockModel.class, priority = 999)
public abstract class MixinJsonUnbakedModel implements UnbakedModel {

    private final ThreadLocal<Boolean> ommcFirstBake = ThreadLocal.withInitial(() -> Boolean.TRUE);

    @Shadow
    public abstract List<BlockElement> getElements();

    @Inject(method = "bake(Lnet/minecraft/client/resources/model/ModelBakery;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At(value = "HEAD"), cancellable = true)
    private void generateCustomBakedModel(ModelBakery loader, BlockModel parent,
                                          Function<Material, TextureAtlasSprite> textureGetter,
                                          ModelState settings, ResourceLocation id, boolean hasDepth,
                                          CallbackInfoReturnable<BakedModel> cir) {
        String[] splitResult = id.getPath().split("/");
        ResourceLocation blockId = new ResourceLocation(splitResult[splitResult.length - 1]);
        Block block = Registry.BLOCK.get(blockId);
        if (block == Blocks.AIR) {
            return;
        }
        BlockModel me = (BlockModel) (Object) this;
        if (!ommcFirstBake.get()) {
            return;
        }
        ommcFirstBake.set(false);
        List<BlockElement> originalModelElements = this.getElements();
        List<BlockElement> originalModelElementsBackup = new ArrayList<>(originalModelElements);
        originalModelElements.clear();

        for (BlockElement modelElement : originalModelElementsBackup) {
            Vector3f origin = new Vector3f(0f, 80f, 181.82f);
            origin.mul(0.0625F);
            BlockElementRotation newModelRotation = new BlockElementRotation(origin, Direction.Axis.X, 45, false);

            Map<Direction, BlockElementFace> faces = new HashMap<>();
            for (Map.Entry<Direction, BlockElementFace> entry : modelElement.faces.entrySet()) {
                BlockElementFace modelElementFace = entry.getValue();
                BlockElementFace tmpModelElementFace = new BlockElementFace(null, modelElementFace.tintIndex, modelElementFace.texture, modelElementFace.uv);
                faces.put(entry.getKey(), tmpModelElementFace);
            }
            originalModelElements.add(new BlockElement(modelElement.from, modelElement.to, faces, newModelRotation, modelElement.shade));
        }
        BlockModel tmpJsonUnbakedModel = me;
        while ((((AccessorBlockModel) tmpJsonUnbakedModel).getParent() != null)) {
            tmpJsonUnbakedModel = ((AccessorBlockModel) tmpJsonUnbakedModel).getParent();
        }
        boolean tmpAmbientOcclusion = ((AccessorBlockModel)tmpJsonUnbakedModel).getHasAmbientOcclusion();
        ((AccessorBlockModel)tmpJsonUnbakedModel).setHasAmbientOcclusion(false);
        // 部分 models
        BakedModel customBakedModel = me.bake(loader, parent, textureGetter, settings, id, hasDepth);
        WorldEaterMineHelperUtil.customModels.put(block, customBakedModel);
        originalModelElements.addAll(originalModelElementsBackup);
        // 完整 models
        BakedModel customFullBakedModel = me.bake(loader, parent, textureGetter, settings, id, hasDepth);
        WorldEaterMineHelperUtil.customFullModels.put(block, customFullBakedModel);

        ((AccessorBlockModel)tmpJsonUnbakedModel).setHasAmbientOcclusion(tmpAmbientOcclusion);
        originalModelElements.clear();
        originalModelElements.addAll(originalModelElementsBackup);
        BakedModel ret = me.bake(loader, parent, textureGetter, settings, id, hasDepth);
        ommcFirstBake.set(true);
        cir.setReturnValue(ret);
    }
}
