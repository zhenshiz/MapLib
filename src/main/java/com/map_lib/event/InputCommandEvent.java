package com.map_lib.event;

import com.map_lib.MapLib;
import com.map_lib.MapLibClientData;
import com.map_lib.command.InputCommand;
import com.map_lib.util.command.input.ClientInputHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

@EventBusSubscriber(modid = MapLib.MOD_ID, value = Dist.CLIENT)
public class InputCommandEvent {

    //移动相关 WASD，跳跃，潜行
    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        Input input = event.getInput();

        if (MapLibClientData.isLocked(InputCommand.InputPermission.UP)) input.up = false;
        if (MapLibClientData.isLocked(InputCommand.InputPermission.DOWN)) input.down = false;
        if (MapLibClientData.isLocked(InputCommand.InputPermission.LEFT)) input.left = false;
        if (MapLibClientData.isLocked(InputCommand.InputPermission.RIGHT)) input.right = false;
        if (MapLibClientData.isLocked(InputCommand.InputPermission.JUMPING)) input.jumping = false;
        if (MapLibClientData.isLocked(InputCommand.InputPermission.SHIFT_KEY_DOWN)) input.shiftKeyDown = false;
    }

    // 鼠标交互相关 (左键攻击、右键放置/使用)
    @SubscribeEvent
    public static void onInteraction(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();

        if (event.getKeyMapping() == mc.options.keyAttack && MapLibClientData.isLocked(InputCommand.InputPermission.MOUSE_ATTACK)) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
        if (event.getKeyMapping() == mc.options.keyUse && MapLibClientData.isLocked(InputCommand.InputPermission.MOUSE_USE)) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
        if (event.getKeyMapping() == mc.options.keyPickItem && MapLibClientData.isLocked(InputCommand.InputPermission.MOUSE_PICK_ITEM)) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    // 杂项 / UI / 交互拦截：通过 Tick 事件提前”抽干”按键缓存
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (MapLibClientData.isLocked(InputCommand.InputPermission.SPRINTING)) {
            mc.options.keySprint.setDown(false);
            mc.player.setSprinting(false);
        }
        if (MapLibClientData.isLocked(InputCommand.InputPermission.PERSPECTIVE))
            drainKey(mc.options.keyTogglePerspective);
        if (MapLibClientData.isLocked(InputCommand.InputPermission.SMOOTH_CAMERA)) drainKey(mc.options.keySmoothCamera);
        if (MapLibClientData.isLocked(InputCommand.InputPermission.SOCIAL_INTERACTION))
            drainKey(mc.options.keySocialInteractions);
        if (MapLibClientData.isLocked(InputCommand.InputPermission.INVENTORY)) drainKey(mc.options.keyInventory);
        if (MapLibClientData.isLocked(InputCommand.InputPermission.ADVANCEMENT)) drainKey(mc.options.keyAdvancements);
        if (MapLibClientData.isLocked(InputCommand.InputPermission.SWAP_HAND)) drainKey(mc.options.keySwapOffhand);
        if (MapLibClientData.isLocked(InputCommand.InputPermission.DROP_ITEM)) drainKey(mc.options.keyDrop);

        if (MapLibClientData.isLocked(InputCommand.InputPermission.CHAT)) {
            drainKey(mc.options.keyChat);    // 默认 T 键
            drainKey(mc.options.keyCommand); // 默认 / 键 (也会打开聊天框)
        }

        if (MapLibClientData.isLocked(InputCommand.InputPermission.HOTBAR_KEYS)) {
            for (KeyMapping key : mc.options.keyHotbarSlots) {
                drainKey(key);
            }
        }
    }

    // 处理滚轮切换快捷栏
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (MapLibClientData.isLocked(InputCommand.InputPermission.HOTBAR_KEYS)) {
            event.setCanceled(true);
        }
    }

    // 模拟按键
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ClientInputHandler.onClientTick();
    }

    private static void drainKey(KeyMapping key) {
        while (key.consumeClick()) {
        }
        key.setDown(false);
    }
}
