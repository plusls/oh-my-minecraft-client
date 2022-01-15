package com.plusls.ommc.mixin.feature.worldEaterMineHelper;

import com.plusls.ommc.feature.worldEaterMineHelper.WorldEaterMineHelperUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
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

@Mixin(value = JsonUnbakedModel.class, priority = 999)
public abstract class MixinJsonUnbakedModel implements UnbakedModel {

    private final ThreadLocal<Boolean> ommcFirstBake = ThreadLocal.withInitial(() -> Boolean.TRUE);

    @Shadow
    public abstract List<ModelElement> getElements();

    @Inject(method = "bake(Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/model/BakedModel;",
            at = @At(value = "HEAD"), cancellable = true)
    private void generateCustomBakedModel(ModelLoader loader, JsonUnbakedModel parent,
                                          Function<SpriteIdentifier, Sprite> textureGetter,
                                          ModelBakeSettings settings, Identifier id, boolean hasDepth,
                                          CallbackInfoReturnable<BakedModel> cir) {
        String[] splitResult = id.getPath().split("/");
        Identifier blockId = new Identifier(splitResult[splitResult.length - 1]);
        Block block = Registry.BLOCK.get(blockId);
        if (block == Blocks.AIR) {
            return;
        }
        JsonUnbakedModel me = (JsonUnbakedModel) (Object) this;
        if (!ommcFirstBake.get()) {
            return;
        }
        ommcFirstBake.set(false);
        List<ModelElement> originalModelElements = this.getElements();
        List<ModelElement> originalModelElementsBackup = new ArrayList<>(originalModelElements);
        originalModelElements.clear();

        for (ModelElement modelElement : originalModelElementsBackup) {
            Vec3f origin = new Vec3f(0f, 80f, 181.82f);
            origin.scale(0.0625F);
            ModelRotation newModelRotation = new ModelRotation(origin, Direction.Axis.X, 45, false);

            Map<Direction, ModelElementFace> faces = new HashMap<>();
            for (Map.Entry<Direction, ModelElementFace> entry : modelElement.faces.entrySet()) {
                ModelElementFace modelElementFace = entry.getValue();
                ModelElementFace tmpModelElementFace = new ModelElementFace(null, modelElementFace.tintIndex, modelElementFace.textureId, modelElementFace.textureData);
                faces.put(entry.getKey(), tmpModelElementFace);
            }
            originalModelElements.add(new ModelElement(modelElement.from, modelElement.to, faces, newModelRotation, modelElement.shade));
        }
        JsonUnbakedModel tmpJsonUnbakedModel = me;
        while (tmpJsonUnbakedModel.parent != null) {
            tmpJsonUnbakedModel = tmpJsonUnbakedModel.parent;
        }
        boolean tmpAmbientOcclusion = tmpJsonUnbakedModel.ambientOcclusion;
        tmpJsonUnbakedModel.ambientOcclusion = false;
        // 部分 models
        BakedModel customBakedModel = me.bake(loader, parent, textureGetter, settings, id, hasDepth);
        WorldEaterMineHelperUtil.customModels.put(block, customBakedModel);
        originalModelElements.addAll(originalModelElementsBackup);
        // 完整 models
        BakedModel customFullBakedModel = me.bake(loader, parent, textureGetter, settings, id, hasDepth);
        WorldEaterMineHelperUtil.customFullModels.put(block, customFullBakedModel);

        tmpJsonUnbakedModel.ambientOcclusion = tmpAmbientOcclusion;
        originalModelElements.clear();
        originalModelElements.addAll(originalModelElementsBackup);
        BakedModel ret = me.bake(loader, parent, textureGetter, settings, id, hasDepth);
        ommcFirstBake.set(true);
        cir.setReturnValue(ret);
    }
}
