package com.plusls.ommc.feature.highlithtWaypoint;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.Option;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static RegistryKey<World> currentWorld = null;

    public static void init() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal(HIGHLIGHT_COMMAND).then(
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
        ));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> currentWorld = Objects.requireNonNull(client.world).getRegistryKey());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            currentWorld = null;
            highlightPos = null;
        });
        WorldRenderEvents.END.register(context -> HighlightWaypointUtil.drawWaypoint(context.matrixStack(), context.tickDelta()));
    }

    public static void postRespawn(PlayerRespawnS2CPacket packet) {
        RegistryKey<World> newDimension = packet.getDimension();
        if (highlightPos != null && currentWorld != newDimension) {
            if (currentWorld == World.OVERWORLD && newDimension == World.NETHER) {
                highlightPos = new BlockPos(highlightPos.getX() / 8, highlightPos.getY(), highlightPos.getZ() / 8);
            } else if (currentWorld == World.NETHER && newDimension == World.OVERWORLD) {
                highlightPos = new BlockPos(highlightPos.getX() * 8, highlightPos.getY(), highlightPos.getZ() * 8);
            } else {
                highlightPos = null;
            }
        }
        currentWorld = newDimension;
    }

    public static ArrayList<Pair<Integer, String>> getWaypointStrings(String message) {
        ArrayList<Pair<Integer, String>> ret = new ArrayList<>();
        if ((message.contains("[") && message.contains("]")) || (message.contains("(") && message.contains(")"))) {
            getWaypointStringsByPattern(message, ret, pattern1);
            getWaypointStringsByPattern(message, ret, pattern2);
            getWaypointStringsByPattern(message, ret, pattern3);
            getWaypointStringsByPattern(message, ret, pattern4);
        }
        ret.sort(Comparator.comparingInt(Pair::getLeft));
        return ret;
    }

    private static void getWaypointStringsByPattern(String message, ArrayList<Pair<Integer, String>> ret, Pattern pattern) {
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String match = matcher.group();
            BlockPos pos = parseWaypoint(match.substring(1, match.length() - 1));
            if (pos == null) {
                continue;
            }
            ret.add(new Pair<>(matcher.start(), match));
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
                    case "x" -> x = Integer.parseInt(value.strip());
                    case "y" -> y = Integer.parseInt(value.strip());
                    case "z" -> z = Integer.parseInt(value.strip());
                }
            }

        } catch (NumberFormatException ignored) {
        }
        if (x == null || z == null) {
            return null;
        }
        return new BlockPos(x, y, z);
    }

    public static void parseWaypointText(Text chat) {
        if (chat.getSiblings().size() > 0) {
            for (Text text : chat.getSiblings()) {
                parseWaypointText(text);
            }
        }
        if (chat instanceof TranslatableText) {
            Object[] args = ((TranslatableText) chat).getArgs();
            boolean updateTranslatableText = false;
            for (int i = 0; i < args.length; ++i) {
                if (args[i] instanceof Text) {
                    parseWaypointText((Text) args[i]);
                } else if (args[i] instanceof String) {
                    Text text = new LiteralText((String) args[i]);
                    if (updateWaypointsText(text)) {
                        args[i] = text;
                        updateTranslatableText = true;
                    }
                }
            }
            if (updateTranslatableText) {
                // refresh cache
                ((TranslatableText) chat).languageCache = null;
            }
        }
        updateWaypointsText(chat);
    }


    public static boolean updateWaypointsText(Text chat) {
        if (!(chat instanceof LiteralText literalChatText)) {
            return false;
        }


        String message = literalChatText.string;
        ArrayList<Pair<Integer, String>> waypointPairs = getWaypointStrings(message);
        if (waypointPairs.size() > 0) {
            Style style = chat.getStyle();
            TextColor color = style.getColor();
            if (color == null) {
                color = TextColor.fromFormatting(Formatting.GREEN);
            }
            ArrayList<LiteralText> texts = new ArrayList<>();
            int prevIdx = 0;
            for (Pair<Integer, String> waypointPair : waypointPairs) {
                String waypointString = waypointPair.getRight();
                int waypointIdx = waypointPair.getLeft();
                LiteralText prevText = new LiteralText(message.substring(prevIdx, waypointIdx));
                prevText.setStyle(style);
                texts.add(prevText);

                LiteralText clickableWaypoint = new LiteralText(waypointString);
                Style chatStyle = clickableWaypoint.getStyle();
                BlockPos pos = Objects.requireNonNull(parseWaypoint(waypointString.substring(1, waypointString.length() - 1)));
                TranslatableText hover = new TranslatableText("ommc.highlight_waypoint.tooltip");
                chatStyle = chatStyle.withClickEvent(
                                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        String.format("/%s %d %d %d", HIGHLIGHT_COMMAND, pos.getX(), pos.getY(), pos.getZ())))
                        .withColor(color).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
                clickableWaypoint.setStyle(chatStyle);
                texts.add(clickableWaypoint);
                prevIdx = waypointIdx + waypointString.length();
            }
            if (prevIdx < message.length() - 1) {
                LiteralText lastText = new LiteralText(message.substring(prevIdx));
                lastText.setStyle(style);
                texts.add(lastText);
            }
            for (int i = 0; i < texts.size(); ++i) {
                literalChatText.getSiblings().add(i, texts.get(i));
            }
            literalChatText.string = "";
            literalChatText.setStyle(Style.EMPTY);
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
        Vec3d cameraPos = cameraEntity.getCameraPosVec(tickDelta);
        double degrees = 5.0 + Math.min((5.0 / distance), 5.0);
        double angle = degrees * 0.0174533;
        double size = Math.sin(angle) * distance;
        Vec3d cameraPosPlusDirection = cameraEntity.getRotationVec(tickDelta);
        Vec3d cameraPosPlusDirectionTimesDistance = cameraPos.add(cameraPosPlusDirection.getX() * distance, cameraPosPlusDirection.getY() * distance, cameraPosPlusDirection.getZ() * distance);
        Box axisalignedbb = new Box(pos.getX() + 0.5f - size, pos.getY() + 0.5f - size, pos.getZ() + 0.5f - size,
                pos.getX() + 0.5f + size, pos.getY() + 0.5f + size, pos.getZ() + 0.5f + size);
        Optional<Vec3d> raycastResult = axisalignedbb.raycast(cameraPos, cameraPosPlusDirectionTimesDistance);
        return axisalignedbb.contains(cameraPos) ? distance >= 1.0 : raycastResult.isPresent();
    }

    public static void drawWaypoint(MatrixStack matrixStack, float tickDelta) {
        // 多线程可能会出锅？
        BlockPos highlightPos = HighlightWaypointUtil.highlightPos;
        if (highlightPos != null) {
            MinecraftClient mc = MinecraftClient.getInstance();
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
    public static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier textureId, float tickDelta, float heightScale, long worldTime, int yOffset, int maxY, float[] color, float innerRadius, float outerRadius) {
        int i = yOffset + maxY;
        matrices.push();
        matrices.translate(0.5D, 0.0D, 0.5D);
        float f = (float) Math.floorMod(worldTime, 40) + tickDelta;
        float g = maxY < 0 ? f : -f;
        float h = MathHelper.fractionalPart(g * 0.2F - (float) MathHelper.floor(g * 0.1F));
        float j = color[0];
        float k = color[1];
        float l = color[2];
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(f * 2.25F - 45.0F));
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
        // Change layer to getTextSeeThrough
        // it works, but why?
        renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getTextSeeThrough(textureId)), j, k, l, 1.0F, yOffset, i, 0.0F, innerRadius, innerRadius, 0.0F, ac, 0.0F, 0.0F, t, 0.0F, 1.0F, aj, ai);
        matrices.pop();
        y = -outerRadius;
        float z = -outerRadius;
        ab = -outerRadius;
        ac = -outerRadius;
        ag = 0.0F;
        ah = 1.0F;
        ai = -1.0F + h;
        aj = (float) maxY * heightScale + ai;
        renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, true)), j, k, l, 0.125F, yOffset, i, y, z, outerRadius, ab, ac, outerRadius, outerRadius, outerRadius, 0.0F, 1.0F, aj, ai);
        matrices.pop();
    }

    private static void renderBeamFace(Matrix4f modelMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, int yOffset, int height, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
        renderBeamVertex(modelMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x1, z1, u2, v1);
        renderBeamVertex(modelMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x1, z1, u2, v2);
        renderBeamVertex(modelMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x2, z2, u1, v2);
        renderBeamVertex(modelMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x2, z2, u1, v1);
    }

    private static void renderBeamVertex(Matrix4f modelMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, int y, float x, float z, float u, float v) {
        vertices.vertex(modelMatrix, x, (float) y, z).color(red, green, blue, alpha).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
    }

    @SuppressWarnings("all")
    private static void renderBeamLayer(MatrixStack matrices, VertexConsumer vertices, float red, float green, float blue, float alpha, int yOffset, int height, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float u1, float u2, float v1, float v2) {
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x1, z1, x2, z2, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x4, z4, x3, z3, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x2, z2, x4, z4, u1, u2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x3, z3, x1, z1, u1, u2, v1, v2);
    }

    public static void renderLabel(MatrixStack matrixStack, double distance, Entity cameraEntity, float tickDelta, boolean isPointedAt, BlockPos pos) {
        MinecraftClient mc = MinecraftClient.getInstance();

        String name = String.format("x:%d, y:%d, z:%d (%dm)", pos.getX(), pos.getY(), pos.getZ(), (int) distance);
        double baseX = pos.getX() - MathHelper.lerp(tickDelta, cameraEntity.prevX, cameraEntity.getX());
        double baseY = pos.getY() - MathHelper.lerp(tickDelta, cameraEntity.prevY, cameraEntity.getY()) - 1.5;
        double baseZ = pos.getZ() - MathHelper.lerp(tickDelta, cameraEntity.prevZ, cameraEntity.getZ());
        // 当前渲染的最大距离
        double maxDistance = Option.RENDER_DISTANCE.get(mc.options) * 16;
        double adjustedDistance = distance;
        if (distance > maxDistance) {
            baseX = baseX / distance * maxDistance;
            baseY = baseY / distance * maxDistance;
            baseZ = baseZ / distance * maxDistance;
            adjustedDistance = maxDistance;
        }
        // 根据调节后的距离决定绘制的大小
        float scale = (float) (adjustedDistance * 0.1f + 1.0f) * 0.0266f;
        matrixStack.push();
        // 当前绘制位置是以玩家为中心的，转移到目的地
        matrixStack.translate(baseX, baseY, baseZ);

        if (lastBeamTime >= System.currentTimeMillis()) {
            // 画信标光柱
            VertexConsumerProvider.Immediate vertexConsumerProvider0 = mc.getBufferBuilders().getEffectVertexConsumers();
            float[] color = {1.0f, 0.0f, 0.0f};
            renderBeam(matrixStack, vertexConsumerProvider0, BeaconBlockEntityRenderer.BEAM_TEXTURE,
                    tickDelta, 1.0f, Objects.requireNonNull(mc.world).getTime(), (int) (baseY - 512), 1024, color, 0.2F, 0.25F);
            vertexConsumerProvider0.draw();

            // 画完后会关闭半透明，需要手动打开
            RenderSystem.enableBlend();
        }

        // 移动到方块中心
        matrixStack.translate(0.5f, 0.5f, 0.5f);

        // 在玩家正对着的平面进行绘制
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-cameraEntity.getYaw()));
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(mc.getEntityRenderDispatcher().camera.getPitch()));
        // 缩放绘制的大小，让 waypoint 根据距离缩放
        matrixStack.scale(-scale, -scale, -scale);
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();
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
        Sprite icon = HighlightWaypointResourceLoader.targetIdSprite;
        // 不设置渲染不出
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        // 渲染图标
        RenderSystem.enableTexture();
        vertexBuffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        vertexBuffer.vertex(matrix4f, -xWidth, -yWidth, 0.0f).texture(icon.getMinU(), icon.getMinV()).color(iconR, iconG, iconB, fade).next();
        vertexBuffer.vertex(matrix4f, -xWidth, yWidth, 0.0f).texture(icon.getMinU(), icon.getMaxV()).color(iconR, iconG, iconB, fade).next();
        vertexBuffer.vertex(matrix4f, xWidth, yWidth, 0.0f).texture(icon.getMaxU(), icon.getMaxV()).color(iconR, iconG, iconB, fade).next();
        vertexBuffer.vertex(matrix4f, xWidth, -yWidth, 0.0f).texture(icon.getMaxU(), icon.getMinV()).color(iconR, iconG, iconB, fade).next();
        tessellator.draw();
        RenderSystem.disableTexture();

        TextRenderer textRenderer = mc.textRenderer;
        if (isPointedAt && textRenderer != null) {
            // 渲染高度
            int elevateBy = -19;
            RenderSystem.enablePolygonOffset();
            int halfStringWidth = textRenderer.getWidth(name) / 2;
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            // 渲染内框
            RenderSystem.polygonOffset(1.0f, 11.0f);
            vertexBuffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            vertexBuffer.vertex(matrix4f, -halfStringWidth - 2, -2 + elevateBy, 0.0f).color(textFieldR, textFieldG, textFieldB, 0.6f * fade).next();
            vertexBuffer.vertex(matrix4f, -halfStringWidth - 2, 9 + elevateBy, 0.0f).color(textFieldR, textFieldG, textFieldB, 0.6f * fade).next();
            vertexBuffer.vertex(matrix4f, halfStringWidth + 2, 9 + elevateBy, 0.0f).color(textFieldR, textFieldG, textFieldB, 0.6f * fade).next();
            vertexBuffer.vertex(matrix4f, halfStringWidth + 2, -2 + elevateBy, 0.0f).color(textFieldR, textFieldG, textFieldB, 0.6f * fade).next();
            tessellator.draw();

            // 渲染外框
            RenderSystem.polygonOffset(1.0f, 9.0f);
            vertexBuffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            vertexBuffer.vertex(matrix4f, -halfStringWidth - 1, -1 + elevateBy, 0.0f).color(0.0f, 0.0f, 0.0f, 0.15f * fade).next();
            vertexBuffer.vertex(matrix4f, -halfStringWidth - 1, 8 + elevateBy, 0.0f).color(0.0f, 0.0f, 0.0f, 0.15f * fade).next();
            vertexBuffer.vertex(matrix4f, halfStringWidth + 1, 8 + elevateBy, 0.0f).color(0.0f, 0.0f, 0.0f, 0.15f * fade).next();
            vertexBuffer.vertex(matrix4f, halfStringWidth + 1, -1 + elevateBy, 0.0f).color(0.0f, 0.0f, 0.0f, 0.15f * fade).next();
            tessellator.draw();
            RenderSystem.disablePolygonOffset();

            // 渲染文字
            RenderSystem.enableTexture();
            VertexConsumerProvider.Immediate vertexConsumerProvider = mc.getBufferBuilders().getEffectVertexConsumers();
            int textColor = (int) (255.0f * fade) << 24 | 0xCCCCCC;
            RenderSystem.disableDepthTest();
            textRenderer.draw(new LiteralText(name), (float) (-textRenderer.getWidth(name) / 2), elevateBy, textColor, false, matrix4f, vertexConsumerProvider, true, 0, 0xF000F0);
            vertexConsumerProvider.draw();
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
    }
}
