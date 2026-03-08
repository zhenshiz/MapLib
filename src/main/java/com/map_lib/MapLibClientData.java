package com.map_lib;

import com.map_lib.command.HudCommand;
import com.map_lib.command.InputCommand;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MapLibClientData {
    // 存储当前客户端需要高亮发光的实体 ID 集合
    public static final Map<Integer, Integer> GLOWING_ENTITIES = new ConcurrentHashMap<>();

    // 存储当前被锁定的权限节点
    public static final Set<InputCommand.InputPermission> LOCKED_PERMISSIONS = ConcurrentHashMap.newKeySet();

    public static boolean isLocked(InputCommand.InputPermission perm) {
        return perm.isLocked(LOCKED_PERMISSIONS);
    }

    // 世界动态环境数据
    public static ResourceLocation customSunTexture = null;
    public static ResourceLocation customMoonTexture = null;
    public static Integer customSkyColor = null;

    // 存储当前被隐藏的 HUD 元素
    public static final Set<HudCommand.HudType> HIDDEN_HUD_ELEMENTS = ConcurrentHashMap.newKeySet();

    public static boolean isHudHidden(HudCommand.HudType type) {
        return type.isHidden(HIDDEN_HUD_ELEMENTS);
    }

    // 记录当前的自定义着色器，null 代表没有
    public static ResourceLocation customShader = null;

    // 记录当前附身的实体 ID，-1 代表没有附身
    public static int possessedEntityId = -1;
}
