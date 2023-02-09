package com.plusls.ommc.feature.highlithtWaypoint;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.brigadier.arguments.IntegerArgumentType;
//#if MC >= 11903
import com.mojang.math.Axis;
//#else
//$$ import top.hendrixshen.magiclib.compat.minecraft.math.Vector3fCompatApi;
//#endif
import com.plusls.ommc.ModInfo;
import com.plusls.ommc.config.Configs;
import com.plusls.ommc.mixin.accessor.AccessorTextComponent;
import com.plusls.ommc.mixin.accessor.AccessorTranslatableComponent;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
//#if MC >= 11903
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
//#endif
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import top.hendrixshen.magiclib.compat.minecraft.blaze3d.vertex.VertexFormatCompatApi;
import top.hendrixshen.magiclib.compat.minecraft.network.chat.ComponentCompatApi;
import top.hendrixshen.magiclib.compat.minecraft.network.chat.StyleCompatApi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//#if MC > 11802
import net.minecraft.network.chat.contents.*;
//#else
//$$ import net.minecraft.client.Option;
//#endif

//#if MC <= 11605
//$$ import net.minecraft.client.renderer.texture.TextureAtlas;
//#endif

//#if MC > 11502
import net.minecraft.resources.ResourceKey;
//#else
//$$ import net.minecraft.world.level.dimension.DimensionType;
//#endif

// from fabric-voxel map
public class HighlightWaypointUtil {

    private static final String HIGHLIGHT_COMMAND = "highlightWaypoint";
    @Nullable
    public static BlockPos highlightPos;
    public static long lastBeamTime = 0;
    public static Pattern pattern1 = Pattern.compile("\\[(\\w+\\s*:\\s*[-#]?[^\\[\\]]+)(,\\s*\\w+\\s*:\\s*[-#]?[^\\[\\]]+)+]", Pattern.CASE_INSENSITIVE);
    public static Pattern pattern2 = Pattern.compile("\\((\\w+\\s*:\\s*[-#]?[^\\[\\]]+)(,\\s*\\w+\\s*:\\s*[-#]?[^\\[\\]]+)+\\)", Pattern.CASE_INSENSITIVE);
    public static Pattern pattern3 = Pattern.compile("\\[(-?\\d+)(,\\s*-?\\d+)(,\\s*-?\\d+)]", Pattern.CASE_INSENSITIVE);
    public static Pattern pattern4 = Pattern.compile("\\((-?\\d+)(,\\s*-?\\d+)(,\\s*-?\\d+)\\)", Pattern.CASE_INSENSITIVE);
    @Nullable
    //#if MC > 11502
    public static ResourceKey<Level> currentWorld = null;
    //#else
    //$$ public static DimensionType currentWorld = null;
    //#endif

    public static void init() {
        //#if MC >= 11903
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
        //#else
        //$$ ClientCommandManager.DISPATCHER.register(
        //#endif
            ClientCommandManager.literal(HIGHLIGHT_COMMAND).then(
                    ClientCommandManager.argument("x", IntegerArgumentType.integer()).then(
                            ClientCommandManager.argument("y", IntegerArgumentType.integer()).then(
                                    ClientCommandManager.argument("z", IntegerArgumentType.integer()).
                                            executes(context -> {
                                                int x = IntegerArgumentType.getInteger(context, "x");
                                                int y = IntegerArgumentType.getInteger(context, "y");
                                                int z = IntegerArgumentType.getInteger(context, "z");
                                                BlockPos pos = new BlockPos(x, y, z);
                                                if (pos.equals(highlightPos)) {
                                                    lastBeamTime = System.currentTimeMillis() + 10 * 1000;
                                                } else {
                                                    highlightPos = new BlockPos(x, y, z);
                                                    lastBeamTime = 0;
                                                }
                                                return 0;
                                            })
                            )
                    )
        //#if MC >= 11903
            )));
        //#else
        //$$ ));
        //#endif
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
                        //#if MC > 11502
                        currentWorld = Objects.requireNonNull(client.level).dimension()
                //#else
                //$$ currentWorld = Objects.requireNonNull(client.level).getDimension().getType()
                //#endif
        );
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            currentWorld = null;
            highlightPos = null;
        });
    }

    public static void postRespawn(ClientboundRespawnPacket packet) {
        //#if MC > 11502
        ResourceKey<Level> newDimension = packet.getDimension();
        //#else
        //$$ DimensionType newDimension = packet.getDimension();
        //#endif
        if (highlightPos != null && currentWorld != newDimension) {
            //#if MC > 11502
            if (currentWorld == Level.OVERWORLD && newDimension == Level.NETHER) {
            //#else
            //$$ if (currentWorld == DimensionType.OVERWORLD && newDimension == DimensionType.NETHER) {
            //#endif
                highlightPos = new BlockPos(highlightPos.getX() / 8, highlightPos.getY(), highlightPos.getZ() / 8);
            //#if MC > 11502
            } else if (currentWorld == Level.NETHER && newDimension == Level.OVERWORLD) {
            //#else
            //$$ } else if (currentWorld == DimensionType.NETHER && newDimension == DimensionType.OVERWORLD) {
            //#endif
                highlightPos = new BlockPos(highlightPos.getX() * 8, highlightPos.getY(), highlightPos.getZ() * 8);
            } else {
                highlightPos = null;
            }
        }
        currentWorld = newDimension;
    }

    public static ArrayList<Tuple<Integer, String>> getWaypointStrings(String message) {
        ArrayList<Tuple<Integer, String>> ret = new ArrayList<>();
        if ((message.contains("[") && message.contains("]")) || (message.contains("(") && message.contains(")"))) {
            getWaypointStringsByPattern(message, ret, pattern1);
            getWaypointStringsByPattern(message, ret, pattern2);
            getWaypointStringsByPattern(message, ret, pattern3);
            getWaypointStringsByPattern(message, ret, pattern4);
        }
        ret.sort(Comparator.comparingInt(Tuple::getA));
        return ret;
    }

    private static void getWaypointStringsByPattern(String message, ArrayList<Tuple<Integer, String>> ret, Pattern pattern) {
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String match = matcher.group();
            BlockPos pos = parseWaypoint(match.substring(1, match.length() - 1));
            if (pos == null) {
                continue;
            }
            ret.add(new Tuple<>(matcher.start(), match));
        }
    }

    @Nullable
    private static BlockPos parseWaypoint(String details) {
        String[] pairs = details.split(",");
        Integer x = null;
        Integer z = null;
        int y = 64;
        try {
            for (int i = 0; i < pairs.length; ++i) {
                int splitIndex = pairs[i].indexOf(":");
                String key, value;
                if (splitIndex == -1 && pairs.length == 3) {
                    if (i == 0) {
                        key = "x";
                    } else if (i == 1) {
                        key = "y";
                    } else {
                        key = "z";
                    }
                    value = pairs[i];
                } else {
                    key = pairs[i].substring(0, splitIndex).toLowerCase().trim();
                    value = pairs[i].substring(splitIndex + 1).trim();
                }

                switch (key) {
                    case "x":
                        x = Integer.parseInt(value.replace(" ", ""));
                        break;
                    case "y":
                        y = Integer.parseInt(value.replace(" ", ""));
                        break;
                    case "z":
                        z = Integer.parseInt(value.replace(" ", ""));
                        break;
                }
            }

        } catch (NumberFormatException ignored) {
        }
        if (x == null || z == null) {
            return null;
        }
        return new BlockPos(x, y, z);
    }

    public static void parseWaypointText(Component chat) {
        if (chat.getSiblings().size() > 0) {
            for (Component text : chat.getSiblings()) {
                parseWaypointText(text);
            }
        }
        //#if MC > 11802
        ComponentContents componentContents = chat.getContents();
        if (componentContents instanceof TranslatableContents) {
        //#else
        //$$ if (chat instanceof TranslatableComponent) {
        //#endif

            //#if MC > 11802
            Object[] args = ((TranslatableContents) componentContents).getArgs();
            //#else
            //$$ Object[] args = ((TranslatableComponent) chat).getArgs();
            //#endif
            boolean updateTranslatableText = false;
            for (int i = 0; i < args.length; ++i) {
                if (args[i] instanceof Component) {
                    parseWaypointText((Component) args[i]);
                } else if (args[i] instanceof String) {
                    Component text = ComponentCompatApi.literal((String) args[i]);
                    if (updateWaypointsText(text)) {
                        args[i] = text;
                        updateTranslatableText = true;
                    }
                }
            }
            if (updateTranslatableText) {
                // refresh cache
                //#if MC > 11802
                ((AccessorTranslatableComponent) componentContents).setDecomposedWith(null);
                //#elseif MC > 11502
                //$$ ((AccessorTranslatableComponent) chat).setDecomposedWith(null);
                //#else
                //$$ ((AccessorTranslatableComponent) chat).setDecomposedLanguageTime(-1);
                //#endif
            }
        }
        updateWaypointsText(chat);
    }


    public static boolean updateWaypointsText(Component chat) {
        //#if MC > 11802
        ComponentContents componentContents = chat.getContents();
        if (!(componentContents instanceof LiteralContents)) {
        //#else
        //$$ if (!(chat instanceof TextComponent)) {
        //#endif
            return false;
        }
        //#if MC > 11802
        LiteralContents literalChatText = (LiteralContents) componentContents;
        //#else
        //$$ TextComponent literalChatText = (TextComponent) chat;
        //#endif

        String message = ((AccessorTextComponent) (Object) literalChatText).getText();
        ArrayList<Tuple<Integer, String>> waypointPairs = getWaypointStrings(message);
        if (waypointPairs.size() > 0) {
            Style style = chat.getStyle();
            ClickEvent clickEvent = style.getClickEvent();
            //#if MC > 11502
            TextColor color = style.getColor();
            //#else
            //$$ ChatFormatting color = style.getColor();
            //#endif
            if (color == null) {
                //#if MC > 11502
                color = TextColor.fromLegacyFormat(ChatFormatting.GREEN);
                //#else
                //$$ color = ChatFormatting.GREEN;
                //#endif
            }
            ArrayList<Component> texts = new ArrayList<>();
            int prevIdx = 0;
            for (Tuple<Integer, String> waypointPair : waypointPairs) {
                String waypointString = waypointPair.getB();
                int waypointIdx = waypointPair.getA();
                Component prevText = ComponentCompatApi.literal(message.substring(prevIdx, waypointIdx)).withStyle(style);
                texts.add(prevText);

                //#if MC > 11502
                MutableComponent clickableWaypoint = ComponentCompatApi.literal(waypointString);
                //#else
                //$$ BaseComponent clickableWaypoint = ComponentCompatApi.literal(waypointString);
                //#endif
                Style chatStyle = clickableWaypoint.getStyle();
                BlockPos pos = Objects.requireNonNull(parseWaypoint(waypointString.substring(1, waypointString.length() - 1)));
                Component hover = ComponentCompatApi.literal(ModInfo.translate("highlight_waypoint.tooltip"));
                if (clickEvent == null || Configs.forceParseWaypointFromChat) {
                    clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            String.format("/%s %d %d %d", HIGHLIGHT_COMMAND, pos.getX(), pos.getY(), pos.getZ()));
                }
                chatStyle = chatStyle.withClickEvent(clickEvent)
                        //#if MC > 11502
                        .withColor(color)
                        //#else
                        //$$ .setColor(color)
                        //#endif
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
                clickableWaypoint.withStyle(chatStyle);
                texts.add(clickableWaypoint);
                prevIdx = waypointIdx + waypointString.length();
            }
            if (prevIdx < message.length() - 1) {
                Component lastText = ComponentCompatApi.literal(message.substring(prevIdx)).withStyle(style);
                texts.add(lastText);
            }
            for (int i = 0; i < texts.size(); ++i) {
                chat.getSiblings().add(i, texts.get(i));
            }
            ((AccessorTextComponent) (Object) literalChatText).setText("");
            //#if MC > 11502
            ((MutableComponent) chat).withStyle(StyleCompatApi.empty());
            //#else
            //$$ ((BaseComponent) chat).withStyle(StyleCompatApi.empty());
            //#endif
            return true;
        }
        return false;
    }

    private static double getDistanceToEntity(Entity entity, BlockPos pos) {
        double dx = pos.getX() + 0.5 - entity.getX();
        double dy = pos.getY() + 0.5 - entity.getY();
        double dz = pos.getZ() + 0.5 - entity.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }


    private static boolean isPointedAt(BlockPos pos, double distance, Entity cameraEntity, float tickDelta) {
        Vec3 cameraPos = cameraEntity.getEyePosition(tickDelta);
        double degrees = 5.0 + Math.min((5.0 / distance), 5.0);
        double angle = degrees * 0.0174533;
        double size = Math.sin(angle) * distance;
        Vec3 cameraPosPlusDirection = cameraEntity.getViewVector(tickDelta);
        Vec3 cameraPosPlusDirectionTimesDistance = cameraPos.add(cameraPosPlusDirection.x() * distance, cameraPosPlusDirection.y() * distance, cameraPosPlusDirection.z() * distance);
        AABB axisalignedbb = new AABB(pos.getX() + 0.5f - size, pos.getY() + 0.5f - size, pos.getZ() + 0.5f - size,
                pos.getX() + 0.5f + size, pos.getY() + 0.5f + size, pos.getZ() + 0.5f + size);
        Optional<Vec3> raycastResult = axisalignedbb.clip(cameraPos, cameraPosPlusDirectionTimesDistance);
        return axisalignedbb.contains(cameraPos) ? distance >= 1.0 : raycastResult.isPresent();
    }

    public static void drawWaypoint(PoseStack matrixStack, float tickDelta) {
        // 多线程可能会出锅？
        BlockPos highlightPos = HighlightWaypointUtil.highlightPos;
        if (highlightPos != null) {
            Minecraft mc = Minecraft.getInstance();
            Entity cameraEntity = Objects.requireNonNull(mc.getCameraEntity());
            // 半透明
            RenderSystem.enableBlend();
            // 允许透过方块渲染
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);

            double distance = getDistanceToEntity(cameraEntity, highlightPos);

            renderLabel(matrixStack, distance, cameraEntity, tickDelta, isPointedAt(highlightPos, distance, cameraEntity, tickDelta), highlightPos);

            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        }
    }

    // code from BeaconBlockEntityRenderer
    @SuppressWarnings("all")
    public static void renderBeam(PoseStack matrices, float tickDelta, float heightScale, long worldTime,
                                  int yOffset, int maxY, float[] color, float innerRadius, float outerRadius) {
        ResourceLocation textureId = new ResourceLocation("textures/entity/beacon_beam.png");
        //#if MC > 11605
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, textureId);
        //#else
        //$$ Minecraft.getInstance().getTextureManager().bind(textureId);
        //#endif
        int i = yOffset + maxY;
        matrices.pushPose();
        matrices.translate(0.5D, 0.0D, 0.5D);
        float f = (float) Math.floorMod(worldTime, 40L) + tickDelta;
        float g = maxY < 0 ? f : -f;
        float h = (float) Mth.frac(g * 0.2F - (float) Mth.floor(g * 0.1F));
        float red = color[0];
        float green = color[1];
        float blue = color[2];
        matrices.pushPose();
        //#if MC >= 11903
        matrices.mulPose(Axis.YP.rotationDegrees(f * 2.25F - 45.0F));
        //#else
        //$$ matrices.mulPose(Vector3fCompatApi.YP.rotationDegrees(f * 2.25F - 45.0F));
        //#endif
        float y = 0.0F;
        float ab = 0.0F;
        float ac = -innerRadius;
        float r = 0.0F;
        float s = 0.0F;
        float t = -innerRadius;
        float ag = 0.0F;
        float ah = 1.0F;
        float ai = -1.0F + h;
        float aj = (float) maxY * heightScale * (0.5F / innerRadius) + ai;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormatCompatApi.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        renderBeamLayer(matrices, bufferBuilder, red, green, green, 1.0F, yOffset, i, 0.0F, innerRadius, innerRadius,
                0.0F, ac, 0.0F, 0.0F, t, 0.0F, 1.0F, aj, ai);
        tesselator.end();
        matrices.popPose();
        y = -outerRadius;
        float z = -outerRadius;
        ab = -outerRadius;
        ac = -outerRadius;
        ag = 0.0F;
        ah = 1.0F;
        ai = -1.0F + h;
        aj = (float) maxY * heightScale + ai;
        bufferBuilder.begin(VertexFormatCompatApi.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        renderBeamLayer(matrices, bufferBuilder, red, green, green, 0.125F, yOffset, i, y, z, outerRadius, ab, ac, outerRadius, outerRadius, outerRadius, 0.0F, 1.0F, aj, ai);
        tesselator.end();
        matrices.popPose();
        //#if MC > 11605
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        //#endif
    }

    private static void renderBeamFace(Matrix4f modelMatrix, Matrix3f normalMatrix, BufferBuilder vertices, float red, float green, float blue, float alpha, int yOffset, int height, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
        renderBeamVertex(modelMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x1, z1, u2, v1);
        renderBeamVertex(modelMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x1, z1, u2, v2);
        renderBeamVertex(modelMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x2, z2, u1, v2);
        renderBeamVertex(modelMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x2, z2, u1, v1);
    }

    private static void renderBeamVertex(Matrix4f modelMatrix, Matrix3f normalMatrix, BufferBuilder vertices, float red, float green, float blue, float alpha, int y, float x, float z, float u, float v) {
        vertices.vertex(modelMatrix, x, (float) y, z)
                .uv(u, v)
                .color(red, green, blue, alpha).endVertex();
    }

    @SuppressWarnings("all")
    private static void renderBeamLayer(PoseStack matrices, BufferBuilder vertices, float red, float green, float blue, float alpha, int yOffset, int height, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float u1, float u2, float v1, float v2) {
        PoseStack.Pose entry = matrices.last();
        Matrix4f matrix4f = entry.pose();
        Matrix3f matrix3f = entry.normal();
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x1, z1, x2, z2, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x4, z4, x3, z3, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x2, z2, x4, z4, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x3, z3, x1, z1, u1, u2, v1, v2);
    }

    public static void renderLabel(PoseStack matrixStack, double distance, Entity cameraEntity, float tickDelta, boolean isPointedAt, BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();

        String name = String.format("x:%d, y:%d, z:%d (%dm)", pos.getX(), pos.getY(), pos.getZ(), (int) distance);
        double baseX = pos.getX() - Mth.lerp(tickDelta, cameraEntity.xo, cameraEntity.getX());
        double baseY = pos.getY() - Mth.lerp(tickDelta, cameraEntity.yo, cameraEntity.getY()) - 1.5;
        double baseZ = pos.getZ() - Mth.lerp(tickDelta, cameraEntity.zo, cameraEntity.getZ());
        // 当前渲染的最大距离
        //#if MC > 11802
        double maxDistance = Minecraft.getInstance().options.renderDistance().get();
        //#else
        //$$ double maxDistance = Option.RENDER_DISTANCE.get(mc.options) * 16;
        //#endif
        double adjustedDistance = distance;
        if (distance > maxDistance) {
            baseX = baseX / distance * maxDistance;
            baseY = baseY / distance * maxDistance;
            baseZ = baseZ / distance * maxDistance;
            adjustedDistance = maxDistance;
        }
        // 根据调节后的距离决定绘制的大小
        float scale = (float) (adjustedDistance * 0.1f + 1.0f) * 0.0266f;
        matrixStack.pushPose();
        // 当前绘制位置是以玩家为中心的，转移到目的地
        matrixStack.translate(baseX, baseY, baseZ);

        if (lastBeamTime >= System.currentTimeMillis()) {
            // 画信标光柱
            float[] color = {1.0f, 0.0f, 0.0f};
            renderBeam(matrixStack, tickDelta, 1.0f,
                    Objects.requireNonNull(mc.level).getGameTime(),
                    (int) (baseY - 512), 1024, color, 0.2F, 0.25F);

            // 画完后会关闭半透明，需要手动打开
            RenderSystem.enableBlend();
        }

        // 移动到方块中心
        matrixStack.translate(0.5f, 0.5f, 0.5f);

        // 在玩家正对着的平面进行绘制
        //#if MC >= 11903
        matrixStack.mulPose(Axis.YP.rotationDegrees(-cameraEntity.getYRot()));
        matrixStack.mulPose(Axis.XP.rotationDegrees(mc.getEntityRenderDispatcher().camera.getXRot()));
        //#else
        //$$ matrixStack.mulPose(Vector3fCompatApi.YP.rotationDegrees(-cameraEntity.getYRot()));
        //$$ matrixStack.mulPose(Vector3fCompatApi.XP.rotationDegrees(mc.getEntityRenderDispatcher().camera.getXRot()));
        //#endif
        // 缩放绘制的大小，让 waypoint 根据距离缩放
        matrixStack.scale(-scale, -scale, -scale);
        Matrix4f matrix4f = matrixStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuilder();
        // 透明度
        float fade = distance < 5.0 ? 1.0f : (float) distance / 5.0f;
        fade = Math.min(fade, 1.0f);
        // 渲染的图标的大小
        float xWidth = 10.0f;
        float yWidth = 10.0f;
        // 绿色
        float iconR = 1.0f;
        float iconG = 0.0f;
        float iconB = 0.0f;
        float textFieldR = 3.0f;
        float textFieldG = 0.0f;
        float textFieldB = 0.0f;
        // 图标
        TextureAtlasSprite icon = HighlightWaypointResourceLoader.targetIdSprite;
        // 不设置渲染不出
        //#if MC > 11605
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        //#else
        //$$ RenderSystem.bindTexture(Objects.requireNonNull(mc.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS)).getId());
        //#endif

        // 渲染图标
        RenderSystem.enableTexture();
        vertexBuffer.begin(VertexFormatCompatApi.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        vertexBuffer.vertex(matrix4f, -xWidth, -yWidth, 0.0f).uv(icon.getU0(), icon.getV0()).color(iconR, iconG, iconB, fade).endVertex();
        vertexBuffer.vertex(matrix4f, -xWidth, yWidth, 0.0f).uv(icon.getU0(), icon.getV1()).color(iconR, iconG, iconB, fade).endVertex();
        vertexBuffer.vertex(matrix4f, xWidth, yWidth, 0.0f).uv(icon.getU1(), icon.getV1()).color(iconR, iconG, iconB, fade).endVertex();
        vertexBuffer.vertex(matrix4f, xWidth, -yWidth, 0.0f).uv(icon.getU1(), icon.getV0()).color(iconR, iconG, iconB, fade).endVertex();
        tessellator.end();
        RenderSystem.disableTexture();

        Font textRenderer = mc.font;
        if (isPointedAt && textRenderer != null) {
            // 渲染高度
            int elevateBy = -19;
            RenderSystem.enablePolygonOffset();
            int halfStringWidth = textRenderer.width(name) / 2;
            //#if MC > 11605
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            //#endif

            // 渲染内框
            RenderSystem.polygonOffset(1.0f, 11.0f);
            vertexBuffer.begin(VertexFormatCompatApi.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            vertexBuffer.vertex(matrix4f, -halfStringWidth - 2, -2 + elevateBy, 0.0f).color(textFieldR, textFieldG, textFieldB, 0.6f * fade).endVertex();
            vertexBuffer.vertex(matrix4f, -halfStringWidth - 2, 9 + elevateBy, 0.0f).color(textFieldR, textFieldG, textFieldB, 0.6f * fade).endVertex();
            vertexBuffer.vertex(matrix4f, halfStringWidth + 2, 9 + elevateBy, 0.0f).color(textFieldR, textFieldG, textFieldB, 0.6f * fade).endVertex();
            vertexBuffer.vertex(matrix4f, halfStringWidth + 2, -2 + elevateBy, 0.0f).color(textFieldR, textFieldG, textFieldB, 0.6f * fade).endVertex();
            tessellator.end();

            // 渲染外框
            RenderSystem.polygonOffset(1.0f, 9.0f);
            vertexBuffer.begin(VertexFormatCompatApi.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            vertexBuffer.vertex(matrix4f, -halfStringWidth - 1, -1 + elevateBy, 0.0f).color(0.0f, 0.0f, 0.0f, 0.15f * fade).endVertex();
            vertexBuffer.vertex(matrix4f, -halfStringWidth - 1, 8 + elevateBy, 0.0f).color(0.0f, 0.0f, 0.0f, 0.15f * fade).endVertex();
            vertexBuffer.vertex(matrix4f, halfStringWidth + 1, 8 + elevateBy, 0.0f).color(0.0f, 0.0f, 0.0f, 0.15f * fade).endVertex();
            vertexBuffer.vertex(matrix4f, halfStringWidth + 1, -1 + elevateBy, 0.0f).color(0.0f, 0.0f, 0.0f, 0.15f * fade).endVertex();
            tessellator.end();
            RenderSystem.disablePolygonOffset();

            // 渲染文字
            RenderSystem.enableTexture();
            int textColor = (int) (255.0f * fade) << 24 | 0xCCCCCC;
            RenderSystem.disableDepthTest();
            textRenderer.drawInBatch(ComponentCompatApi.literal(name), (float) (-textRenderer.width(name) / 2), elevateBy, textColor, false, matrix4f, true, 0, 0xF000F0);
        }
        //#if MC > 11605
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        //#endif
        matrixStack.popPose();
        // 1.14 need enableTexture
        RenderSystem.enableTexture();
    }
}
