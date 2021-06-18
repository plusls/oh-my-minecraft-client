package com.plusls.ommc.feature.worldEaterMineHelper;

import com.plusls.ommc.ModInfo;
import com.plusls.ommc.config.Configs;
import com.plusls.ommc.mixin.feature.worldEaterMineHelper.JsonUnbakedModelInvoker;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CustomBakedModels {
    public static final Map<Block, BakedModel> models = new HashMap<>();

    public static boolean needBuildCustomBakedModel(Identifier id) {
        for (String blockName : Configs.Lists.WORLD_EATER_MINE_HELPER_WHITELIST.getStrings()) {
            if (id.toString().contains(blockName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean shouldUseCustomModel(Block block, BlockPos pos) {
        // ModInfo.LOGGER.debug("test model {} {}", pos, block);
        if (Configs.FeatureToggle.WORLD_EATER_MINE_HELPER.getBooleanValue() && needBuildCustomBakedModel(Registry.BLOCK.getId(block))) {
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

    public static void addCustomBakedModle(JsonUnbakedModel jsonUnbakedModel, ModelLoader loader,
                                           JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter,
                                           ModelBakeSettings settings, Identifier id, boolean hasDepth) {
        String[] splitResult = id.getPath().split("/");

        Identifier blockId = new Identifier(splitResult[splitResult.length - 1]);
        Block block = Registry.BLOCK.get(blockId);
        if (block == Blocks.AIR) {
            return;
        }
        Sprite sprite = textureGetter.apply(jsonUnbakedModel.resolveSprite("particle"));
        BasicBakedModel.Builder builder = (new BasicBakedModel.Builder(jsonUnbakedModel,
                ((JsonUnbakedModelInvoker) jsonUnbakedModel).invokeCompileOverrides(loader, parent), hasDepth)).setParticle(sprite);

        for (ModelElement modelElement : jsonUnbakedModel.getElements()) {
            for (Direction direction : modelElement.faces.keySet()) {
                ModelElementFace modelElementFace = modelElement.faces.get(direction);
                Sprite sprite2 = textureGetter.apply(jsonUnbakedModel.resolveSprite(modelElementFace.textureId));
                Vec3f origin = new Vec3f(8f, 80f, 178.4f);
                origin.scale(0.0625F);
                ModelRotation newModelRotation = new ModelRotation(origin, Direction.Axis.X, 45, false);
                if (modelElementFace.cullFace == null) {
                    builder.addQuad(
                            createQuad(modelElement, modelElementFace, sprite2, direction, settings, id, modelElement.rotation));

                } else {
                    builder.addQuad(Direction.transform(settings.getRotation().getMatrix(), modelElementFace.cullFace),
                            createQuad(modelElement, modelElementFace, sprite2, direction, settings, id, modelElement.rotation));
                }
                builder.addQuad(
                        createQuad(modelElement, modelElementFace, sprite2, direction, settings, id, newModelRotation));
            }
        }
        models.put(block, builder.build());
    }

    private static BakedQuad createQuad(ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction side, ModelBakeSettings settings, Identifier id, ModelRotation rotation) {
        return JsonUnbakedModelInvoker.getQuadFactory().bake(element.from, element.to, elementFace, sprite, side, settings, rotation, element.shade, id);
    }
}
