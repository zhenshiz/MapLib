package com.map_lib.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class ImageLoader {
    public static BufferedImage load(String path, ResourceManager resourceManager) throws IOException {
        // HTTP/HTTPS 网络图片
        if (path.startsWith("http://") || path.startsWith("https://")) {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0"); // 伪装成浏览器防止被拦截
            try (InputStream in = connection.getInputStream()) {
                return ImageIO.read(in);
            }
        }

        // 资源包路径 (格式如 minecraft:textures/block/stone.png)
        // 只能读取服务端能访问到的资源 (原版+模组资源)
        if (path.contains(":") && !path.contains("\\") && !path.contains("/")) {
            try {
                ResourceLocation rl = ResourceLocation.parse(path);
                Optional<Resource> res = resourceManager.getResource(rl);
                if (res.isPresent()) {
                    try (InputStream in = res.get().open()) {
                        return ImageIO.read(in);
                    }
                }
            } catch (Exception ignored) {
                // 解析失败说明可能不是资源路径，继续尝试本地文件
            }
        }

        //本地文件
        File file = new File(path);
        if (file.exists()) {
            return ImageIO.read(file);
        }

        throw new IOException("无法找到图片资源: " + path);
    }
}
