package com.map_lib.util.command.mapgenerator;

import net.minecraft.world.level.material.MapColor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapColorUtil {
    private static final Map<Integer, Byte> COLOR_CACHE = new ConcurrentHashMap<>();

    /**
     * 将标准的 ARGB 颜色转换为最接近的 Minecraft 地图颜色字节
     *
     * @param argb 原始颜色的整数值
     * @return Minecraft 地图颜色 ID (byte)
     */
    public static byte getClosestMapColor(int argb) {
        if (COLOR_CACHE.containsKey(argb)) {
            return COLOR_CACHE.get(argb);
        }

        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;

        if (a < 128) return 0;

        byte closestColor = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 1; i < 64; i++) {
            MapColor mapColor = MapColor.byId(i);
            if (mapColor == MapColor.NONE) continue;

            int mapR = (mapColor.col >> 16) & 0xFF;
            int mapG = (mapColor.col >> 8) & 0xFF;
            int mapB = mapColor.col & 0xFF;

            int[] shadeMultipliers = {180, 220, 255, 135};

            for (int shade = 0; shade < 4; shade++) {
                int mult = shadeMultipliers[shade];

                int finalR = (mapR * mult) / 255;
                int finalG = (mapG * mult) / 255;
                int finalB = (mapB * mult) / 255;

                double distance = Math.pow(r - finalR, 2) +
                        Math.pow(g - finalG, 2) +
                        Math.pow(b - finalB, 2);

                if (distance < minDistance) {
                    minDistance = distance;
                    closestColor = (byte) (i * 4 + shade);
                }
            }
        }

        COLOR_CACHE.put(argb, closestColor);
        return closestColor;
    }

    public static int getRGBFromMapColorId(byte colorId) {
        int unsignedId = Byte.toUnsignedInt(colorId);
        int baseId = unsignedId / 4;
        int shade = unsignedId % 4;

        MapColor mapColor = MapColor.byId(baseId);
        if (mapColor == MapColor.NONE) return 0;

        int rgb = mapColor.col;
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        int[] shadeMultipliers = {180, 220, 255, 135};
        int mult = shadeMultipliers[shade];

        int finalR = (r * mult) / 255;
        int finalG = (g * mult) / 255;
        int finalB = (b * mult) / 255;

        return (0xFF << 24) | (finalR << 16) | (finalG << 8) | finalB;
    }
}