package com.map_lib.event;

import com.map_lib.MapLib;
import com.map_lib.MapLibClientData;
import com.map_lib.command.HudCommand;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = MapLib.MOD_ID, value = Dist.CLIENT)
public class HudCommandEvent {

    // 如果选择了 ALL，直接拦截整个 GUI 渲染的源头 (效果等同于 F1)
    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        if (MapLibClientData.isHudHidden(HudCommand.HudType.ALL)) {
            event.setCanceled(true);
        }
    }

    // 颗粒度拦截：根据枚举类型拦截特定的渲染层
    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
        if (MapLibClientData.isHudHidden(HudCommand.HudType.ALL)) return;

        ResourceLocation layer = event.getName();

        if (layer.equals(VanillaGuiLayers.HOTBAR) && MapLibClientData.isHudHidden(HudCommand.HudType.HOTBAR)) {
            event.setCanceled(true);
        } else if (layer.equals(VanillaGuiLayers.CROSSHAIR) && MapLibClientData.isHudHidden(HudCommand.HudType.CROSSHAIR)) {
            event.setCanceled(true);
        } else if (layer.equals(VanillaGuiLayers.PLAYER_HEALTH) && MapLibClientData.isHudHidden(HudCommand.HudType.HEALTH)) {
            event.setCanceled(true);
        } else if (layer.equals(VanillaGuiLayers.ARMOR_LEVEL) && MapLibClientData.isHudHidden(HudCommand.HudType.ARMOR)) {
            event.setCanceled(true);
        } else if (layer.equals(VanillaGuiLayers.FOOD_LEVEL) && MapLibClientData.isHudHidden(HudCommand.HudType.FOOD)) {
            event.setCanceled(true);
        } else if (layer.equals(VanillaGuiLayers.AIR_LEVEL) && MapLibClientData.isHudHidden(HudCommand.HudType.AIR)) {
            event.setCanceled(true);
        }
        // 经验条和经验等级数字原版拆成了两层，我们用一个 EXPERIENCE 节点同时干掉它们
        else if ((layer.equals(VanillaGuiLayers.EXPERIENCE_BAR) || layer.equals(VanillaGuiLayers.EXPERIENCE_LEVEL))
                && MapLibClientData.isHudHidden(HudCommand.HudType.EXPERIENCE)) {
            event.setCanceled(true);
        } else if (layer.equals(VanillaGuiLayers.CHAT) && MapLibClientData.isHudHidden(HudCommand.HudType.CHAT)) {
            event.setCanceled(true);
        } else if (layer.equals(VanillaGuiLayers.TAB_LIST) && MapLibClientData.isHudHidden(HudCommand.HudType.TAB_LIST)) {
            event.setCanceled(true);
        } else if (layer.equals(VanillaGuiLayers.EFFECTS) && MapLibClientData.isHudHidden(HudCommand.HudType.EFFECTS)) {
            event.setCanceled(true);
        } else if (layer.equals(VanillaGuiLayers.BOSS_OVERLAY) && MapLibClientData.isHudHidden(HudCommand.HudType.BOSS_BAR)) {
            event.setCanceled(true);
        } else if (layer.equals(VanillaGuiLayers.SCOREBOARD_SIDEBAR) && MapLibClientData.isHudHidden(HudCommand.HudType.SCOREBOARD)) {
            event.setCanceled(true);
        } else if (layer.equals(VanillaGuiLayers.SELECTED_ITEM_NAME) && MapLibClientData.isHudHidden(HudCommand.HudType.ITEM_NAME)) {
            event.setCanceled(true);
        }
    }
}
