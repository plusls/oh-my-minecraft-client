package com.plusls.ommc.mixin.feature.worldEaterMineHelper;

import com.plusls.ommc.feature.worldEaterMineHelper.WorldEaterMineHelperUtil;
import com.plusls.ommc.mixin.accessor.AccessorBlockModel;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
//#if MC >= 11903
import net.minecraft.core.registries.BuiltInRegistries;
//#else
//$$ import net.minecraft.core.Registry;
//#endif
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
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

    @Shadow @Nullable protected ResourceLocation parentLocation;

    @Inject(
            //#if MC >= 11903
            method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;",
            //#elseif MC > 11404
            //$$ method = "bake(Lnet/minecraft/client/resources/model/ModelBakery;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;",
            //#else
            //$$ method = "bake(Lnet/minecraft/client/resources/model/ModelBakery;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;)Lnet/minecraft/client/resources/model/BakedModel;",
            //#endif
            at = @At(value = "HEAD"), cancellable = true)
    //#if MC >= 11903
    private void generateCustomBakedModel(ModelBaker loader, BlockModel parent,
    //#else
    //$$ private void generateCustomBakedModel(ModelBakery loader, BlockModel parent,
    //#endif
                                          //#if MC > 11404
                                          Function<Material, TextureAtlasSprite> textureGetter,
                                          //#else
                                          //$$ Function<ResourceLocation, TextureAtlasSprite> textureGetter,
                                          //#endif
                                          ModelState settings,
                                          //#if MC > 11404
                                          ResourceLocation id, boolean hasDepth,
                                          //#endif
                                          CallbackInfoReturnable<BakedModel> cir) {
        //#if MC <= 11404
        //$$ ResourceLocation id = this.parentLocation;
        //#endif
        if (id == null) {
            return;
        }
        String[] splitResult = id.getPath().split("/");
        ResourceLocation blockId = new ResourceLocation(splitResult[splitResult.length - 1]);
        //#if MC >= 11903
        Block block = BuiltInRegistries.BLOCK.get(blockId);
        //#else
        //$$ Block block = Registry.BLOCK.get(blockId);
        //#endif
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
        //#if MC > 11903
        Boolean bool = ((AccessorBlockModel) tmpJsonUnbakedModel).getHasAmbientOcclusion();
        boolean tmpAmbientOcclusion = bool == null || bool;
        //#else
        //$$ boolean tmpAmbientOcclusion = ((AccessorBlockModel) tmpJsonUnbakedModel).getHasAmbientOcclusion();
        //#endif
        ((AccessorBlockModel) tmpJsonUnbakedModel).setHasAmbientOcclusion(false);
        // 部分 models
        //#if MC > 11404
        BakedModel customBakedModel = me.bake(loader, parent, textureGetter, settings, id, hasDepth);
        //#else
        //$$ BakedModel customBakedModel = me.bake(loader, parent, textureGetter, settings);
        //#endif
        WorldEaterMineHelperUtil.customModels.put(block, customBakedModel);
        originalModelElements.addAll(originalModelElementsBackup);
        // 完整 models
        //#if MC > 11404
        BakedModel customFullBakedModel = me.bake(loader, parent, textureGetter, settings, id, hasDepth);
        //#else
        //$$ BakedModel customFullBakedModel = me.bake(loader, parent, textureGetter, settings);
        //#endif
        WorldEaterMineHelperUtil.customFullModels.put(block, customFullBakedModel);

        ((AccessorBlockModel) tmpJsonUnbakedModel).setHasAmbientOcclusion(tmpAmbientOcclusion);
        originalModelElements.clear();
        originalModelElements.addAll(originalModelElementsBackup);
        //#if MC > 11404
        BakedModel ret = me.bake(loader, parent, textureGetter, settings, id, hasDepth);
        //#else
        //$$ BakedModel ret = me.bake(loader, parent, textureGetter, settings);
        //#endif
        ommcFirstBake.set(true);
        cir.setReturnValue(ret);
    }
}
