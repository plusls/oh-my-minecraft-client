package com.plusls.ommc.mixin.feature.worldEaterMineHelper;

import com.plusls.ommc.ModInfo;
import com.plusls.ommc.feature.worldEaterMineHelper.CustomBakedModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(JsonUnbakedModel.class)
public abstract class MixinJsonUnbakedModel implements UnbakedModel {
    @Inject(method = "bake(Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/model/BakedModel;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/BasicBakedModel$Builder;build()Lnet/minecraft/client/render/model/BakedModel;", ordinal = 0), cancellable = true)
    private void generateCustomBakedModel(ModelLoader loader, JsonUnbakedModel parent,
                                          Function<SpriteIdentifier, Sprite> textureGetter,
                                          ModelBakeSettings settings, Identifier id, boolean hasDepth,
                                          CallbackInfoReturnable<BakedModel> cir) {
        if (CustomBakedModels.needBuildCustomBakedModel(id)) {
            ModInfo.LOGGER.debug("add id {} {}", id.toString(), id);
            CustomBakedModels.addCustomBakedModle((JsonUnbakedModel) (Object) this, loader, parent, textureGetter, settings, id, hasDepth);
        }
    }
}
