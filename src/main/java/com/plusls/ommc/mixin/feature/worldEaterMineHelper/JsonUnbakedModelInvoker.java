package com.plusls.ommc.mixin.feature.worldEaterMineHelper;

import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(JsonUnbakedModel.class)
public interface JsonUnbakedModelInvoker {
    @Invoker("compileOverrides")
    ModelOverrideList invokeCompileOverrides(ModelLoader modelLoader, JsonUnbakedModel parent);

    @Accessor("QUAD_FACTORY")
    static BakedQuadFactory getQuadFactory() {
        throw new AssertionError();
    }
}
