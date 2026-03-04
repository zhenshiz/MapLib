package com.map_lib.util.command.mapgenerator;

import com.map_lib.MapLib;
import com.map_lib.command.MapGeneratorCommand;
import com.map_lib.util.ImageLoader;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapArtGenerator {
    public static void generate(ServerPlayer player, String path, int wCount, int hCount, MapGeneratorCommand.ArtMode mode) {
        ServerLevel level = player.serverLevel();

        player.getServer().executeBlocking(() -> {
            try {
                BufferedImage original = ImageLoader.load(path, level.getServer().getResourceManager());
                if (original == null) {
                    player.sendSystemMessage(Component.translatable("command.mapLib.mapGenerator.image.error"));
                    return;
                }

                int totalWidth = wCount * 128;
                int totalHeight = hCount * 128;

                // 图片缩放处理
                BufferedImage resized = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = resized.createGraphics();
                g.drawImage(original, 0, 0, totalWidth, totalHeight, null);
                g.dispose();

                if (mode == MapGeneratorCommand.ArtMode.COLOR) {
                    applyFloydSteinbergDithering(resized);
                }

                // 黑白模式处理
                if (mode == MapGeneratorCommand.ArtMode.BW) {
                    BufferedImage gray = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_BYTE_GRAY);
                    Graphics gGray = gray.getGraphics();
                    gGray.drawImage(resized, 0, 0, null);
                    gGray.dispose();
                    resized = gray;
                }

                List<ItemStack> stacks = new ArrayList<>();
                final BufferedImage finalImage = resized;

                player.getServer().execute(() -> {
                    for (int y = 0; y < hCount; y++) {
                        for (int x = 0; x < wCount; x++) {
                            MapId mapId = level.getFreeMapId();

                            MapItemSavedData rawData = MapItemSavedData.createFresh(0, 0, (byte) 3, false, false, level.dimension());
                            MapItemSavedData data = rawData.locked();

                            for (int i = 0; i < 128; i++) {
                                for (int j = 0; j < 128; j++) {
                                    int pixelX = x * 128 + j;
                                    int pixelY = y * 128 + i;
                                    int argb = finalImage.getRGB(pixelX, pixelY);

                                    // 转换颜色
                                    data.colors[i * 128 + j] = MapColorUtil.getClosestMapColor(argb);
                                }
                            }

                            data.setDirty();
                            level.setMapData(mapId, data);

                            ClientboundMapItemDataPacket packet = new ClientboundMapItemDataPacket(
                                    mapId,
                                    data.scale,
                                    data.locked,
                                    null,
                                    new MapItemSavedData.MapPatch(0, 0, 128, 128, data.colors) // 强制推送全量像素
                            );

                            player.connection.send(packet);

                            ItemStack mapItem = new ItemStack(Items.FILLED_MAP);
                            mapItem.set(DataComponents.MAP_ID, mapId);

                            stacks.add(mapItem);
                        }
                    }

                    for (ItemStack stack : stacks) {
                        if (!player.getInventory().add(stack)) {
                            player.drop(stack, false);
                        }
                    }
                    player.sendSystemMessage(Component.translatable("command.mapLib.mapGenerator.download.success"));
                });

            } catch (IOException e) {
                MapLib.LOGGER.error("§c错误: {}", e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * 对图片应用 Floyd-Steinberg 抖动算法
     * 这会直接修改传入的 BufferedImage
     */
    private static void applyFloydSteinbergDithering(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int oldColor = image.getRGB(x, y);
                int a = (oldColor >> 24) & 0xFF;
                if (a < 128) continue;

                byte closestId = MapColorUtil.getClosestMapColor(oldColor);
                int newColor = MapColorUtil.getRGBFromMapColorId(closestId);

                image.setRGB(x, y, (a << 24) | (newColor & 0x00FFFFFF));

                int rOld = (oldColor >> 16) & 0xFF;
                int gOld = (oldColor >> 8) & 0xFF;
                int bOld = oldColor & 0xFF;

                int rNew = (newColor >> 16) & 0xFF;
                int gNew = (newColor >> 8) & 0xFF;
                int bNew = newColor & 0xFF;

                int errR = rOld - rNew;
                int errG = gOld - gNew;
                int errB = bOld - bNew;

                distributeError(image, x + 1, y, errR, errG, errB, 7);
                distributeError(image, x - 1, y + 1, errR, errG, errB, 3);
                distributeError(image, x, y + 1, errR, errG, errB, 5);
                distributeError(image, x + 1, y + 1, errR, errG, errB, 1);
            }
        }
    }

    private static void distributeError(BufferedImage image, int x, int y, int errR, int errG, int errB, int portion) {
        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) return;

        int color = image.getRGB(x, y);
        int a = (color >> 24) & 0xFF;
        if (a < 128) return;

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        // 叠加误差
        r = clamp(r + (errR * portion) / 16);
        g = clamp(g + (errG * portion) / 16);
        b = clamp(b + (errB * portion) / 16);

        image.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
    }

    private static int clamp(int val) {
        return Math.max(0, Math.min(255, val));
    }
}