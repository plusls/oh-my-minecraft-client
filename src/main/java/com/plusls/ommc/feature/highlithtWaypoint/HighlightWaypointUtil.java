package com.plusls.ommc.feature.highlithtWaypoint;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.plusls.ommc.config.Configs;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.Option;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// from fabric-voxel map
public class HighlightWaypointUtil {

    @Nullable
    public static BlockPos highlightPos;
    public static Pattern pattern1 = Pattern.compile("\\[(\\w+\\s*:\\s*[-#]?[^\\[\\]]+)(,\\s*\\w+\\s*:\\s*[-#]?[^\\[\\]]+)+\\]", Pattern.CASE_INSENSITIVE);
    public static Pattern pattern2 = Pattern.compile("\\((\\w+\\s*:\\s*[-#]?[^\\[\\]]+)(,\\s*\\w+\\s*:\\s*[-#]?[^\\[\\]]+)+\\)", Pattern.CASE_INSENSITIVE);
    public static Pattern pattern3 = Pattern.compile("\\[(-?\\d+)(,\\s*-?\\d+)(,\\s*-?\\d+)\\]", Pattern.CASE_INSENSITIVE);
    public static Pattern pattern4 = Pattern.compile("\\((-?\\d+)(,\\s*-?\\d+)(,\\s*-?\\d+)\\)", Pattern.CASE_INSENSITIVE);

    private static final String HIGHLIGHT_COMMAND = "highlightWaypoint";
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
                                            highlightPos = new BlockPos(x, y, z);
                                            return 0;
                                        })
                        )
                )
        ));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            currentWorld = Objects.requireNonNull(client.world).getRegistryKey();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            currentWorld = null;
            highlightPos = null;
        });
        WorldRenderEvents.END.register(context -> {
            HighlightWaypointUtil.drawWaypoint(context.matrixStack(), context.tickDelta());
        });
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

    public static ArrayList<String> getWaypointStrings(String message) {
        ArrayList<String> ret = new ArrayList<>();
        if ((message.contains("[") && message.contains("]")) || (message.contains("(") && message.contains(")"))) {
            getWaypointStringsByPattern(message, ret, pattern1);
            getWaypointStringsByPattern(message, ret, pattern2);
            getWaypointStringsByPattern(message, ret, pattern3);
            getWaypointStringsByPattern(message, ret, pattern4);
        }
        return ret;
    }

    private static void getWaypointStringsByPattern(String message, ArrayList<String> ret, Pattern pattern) {
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String match = matcher.group();
            BlockPos pos = parseWaypoint(match.substring(1, match.length() - 1));
            if (pos == null) {
                continue;
            }
            ret.add(match);
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
                        x = Integer.parseInt(value);
                        break;
                    case "y":
                        y = Integer.parseInt(value);
                        break;
                    case "z":
                        z = Integer.parseInt(value);
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

    public static boolean parseWaypoints(Text chat, LiteralText result) {
        boolean ret = false;
        if (chat.getSiblings().size() > 0) {
            for (Text text : chat.getSiblings()) {
                ret |= parseWaypoints(text, result);
            }
        } else {
            ret = parseWaypointsText(chat, result);
        }
        return ret;
    }

    public static boolean parseWaypointsText(Text chat, LiteralText result) {
        String message = chat.getString();
        ArrayList<String> waypointStrings = getWaypointStrings(message);
        Style oldStyle = chat.getStyle();
        boolean haveOldEvent = oldStyle.getClickEvent() != null || oldStyle.getHoverEvent() != null;
        if (waypointStrings.size() > 0 && (!haveOldEvent || Configs.Generic.FORCE_PARSE_WAYPOINT_FROM_CHAT.getBooleanValue())) {
            ArrayList<LiteralText> texts = new ArrayList<>();
            int prevIdx = 0, currentIdx;
            for (String waypointString : waypointStrings) {
                currentIdx = message.indexOf(waypointString, prevIdx);
                texts.add(new LiteralText(message.substring(prevIdx, currentIdx)));
                LiteralText clickableWaypoint = new LiteralText(waypointString);
                Style chatStyle = clickableWaypoint.getStyle();
                BlockPos pos = Objects.requireNonNull(parseWaypoint(waypointString.substring(1, waypointString.length() - 1)));
                TranslatableText hover = new TranslatableText("ommc.highlight_waypoint.tooltip");
                chatStyle = chatStyle.withClickEvent(
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                String.format("/%s %d %d %d", HIGHLIGHT_COMMAND, pos.getX(), pos.getY(), pos.getZ())))
                        .withColor(Formatting.GREEN).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
                clickableWaypoint.setStyle(chatStyle);
                texts.add(clickableWaypoint);
                prevIdx = currentIdx + waypointString.length();
            }
            if (prevIdx < message.length() - 1) {
                texts.add(new LiteralText(message.substring(prevIdx)));
            }
            LiteralText finalText = new LiteralText("");
            for (LiteralText text : texts) {
                finalText.append(text);
            }
            result.append(finalText);
            return true;
        }
        result.append(chat);
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
        matrixStack.translate(baseX + 0.5f, baseY + 0.5f, baseZ + 0.5f);
        // 在玩家正对着的平面进行绘制
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-cameraEntity.yaw));
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(mc.getEntityRenderDispatcher().camera.getPitch()));
        // 缩放绘制的大小，让 waypoint 根据距离缩放
        matrixStack.scale(-scale, -scale, -scale);
        Matrix4f matrix4f = matrixStack.peek().getModel();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        // 透明度
        float fade = distance < 5.0 ? 1.0f : (float) distance / 5.0f;
        fade = Math.min(fade, 1.0f);
        // 渲染的图标的大小
        float width = 10.0f;
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
//        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.bindTexture(Objects.requireNonNull(mc.getTextureManager().getTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)).getGlId());

        // 渲染图标
        RenderSystem.enableTexture();
        vertexBuffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        vertexBuffer.vertex(matrix4f, -width, -width, 0.0f).texture(icon.getMinU(), icon.getMinV()).color(iconR, iconG, iconB, fade).next();
        vertexBuffer.vertex(matrix4f, -width, width, 0.0f).texture(icon.getMinU(), icon.getMaxV()).color(iconR, iconG, iconB, fade).next();
        vertexBuffer.vertex(matrix4f, width, width, 0.0f).texture(icon.getMaxU(), icon.getMaxV()).color(iconR, iconG, iconB, fade).next();
        vertexBuffer.vertex(matrix4f, width, -width, 0.0f).texture(icon.getMaxU(), icon.getMinV()).color(iconR, iconG, iconB, fade).next();
        tessellator.draw();
        RenderSystem.disableTexture();

        TextRenderer textRenderer = mc.textRenderer;
        if (isPointedAt && textRenderer != null) {
            // 渲染高度
            int elevateBy = -19;
            RenderSystem.enablePolygonOffset();
            int halfStringWidth = textRenderer.getWidth(name) / 2;
//            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            // 渲染内框
            RenderSystem.polygonOffset(1.0f, 11.0f);
            vertexBuffer.begin(7, VertexFormats.POSITION_COLOR);
            vertexBuffer.vertex(matrix4f, -halfStringWidth - 2, -2 + elevateBy, 0.0f).color(textFieldR, textFieldG, textFieldB, 0.6f * fade).next();
            vertexBuffer.vertex(matrix4f, -halfStringWidth - 2, 9 + elevateBy, 0.0f).color(textFieldR, textFieldG, textFieldB, 0.6f * fade).next();
            vertexBuffer.vertex(matrix4f, halfStringWidth + 2, 9 + elevateBy, 0.0f).color(textFieldR, textFieldG, textFieldB, 0.6f * fade).next();
            vertexBuffer.vertex(matrix4f, halfStringWidth + 2, -2 + elevateBy, 0.0f).color(textFieldR, textFieldG, textFieldB, 0.6f * fade).next();
            tessellator.draw();

            // 渲染外框
            RenderSystem.polygonOffset(1.0f, 9.0f);
            vertexBuffer.begin(7, VertexFormats.POSITION_COLOR);
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
//        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
    }
}
