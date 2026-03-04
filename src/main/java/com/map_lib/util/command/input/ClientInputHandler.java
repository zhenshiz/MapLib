package com.map_lib.util.command.input;

import com.map_lib.command.InputCommand;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientInputHandler {
    private static final Map<InputCommand.InputAction, Integer> ACTIVE_SIMULATIONS = new ConcurrentHashMap<>();

    public static void onSimulatePacket(InputCommand.InputAction action, int ticks) {
        if (action != null) {
            ACTIVE_SIMULATIONS.put(action, ticks);
        }
    }

    public static void onClientTick() {
        if (ACTIVE_SIMULATIONS.isEmpty()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Iterator<Map.Entry<InputCommand.InputAction, Integer>> it = ACTIVE_SIMULATIONS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<InputCommand.InputAction, Integer> entry = it.next();
            InputCommand.InputAction action = entry.getKey();
            int ticks = entry.getValue();

            if (ticks > 0) {
                handleAction(mc, action);
                entry.setValue(ticks - 1);
            } else {
                releaseKey(mc, action);
                it.remove();
            }
        }
    }

    private static void handleAction(Minecraft mc, InputCommand.InputAction action) {
        switch (action) {
            case DROP -> {
                if (mc.player != null) {
                    mc.player.drop(false);
                }
            }
            default -> pressKey(mc, action);
        }
    }

    private static void pressKey(Minecraft mc, InputCommand.InputAction action) {
        KeyMapping key = getKeyMapping(mc, action);
        if (key != null) {
            KeyMapping.set(key.getKey(), true);
            key.setDown(true);
        }
    }

    private static void releaseKey(Minecraft mc, InputCommand.InputAction action) {
        if (action == InputCommand.InputAction.DROP) return;

        KeyMapping key = getKeyMapping(mc, action);
        if (key != null) {
            KeyMapping.set(key.getKey(), false);
            key.setDown(false);
        }
    }

    private static KeyMapping getKeyMapping(Minecraft mc, InputCommand.InputAction action) {
        return switch (action) {
            case FORWARD -> mc.options.keyUp;
            case BACKWARD -> mc.options.keyDown;
            case LEFT -> mc.options.keyLeft;
            case RIGHT -> mc.options.keyRight;
            case JUMP -> mc.options.keyJump;
            case SNEAK -> mc.options.keyShift;
            case SPRINTING -> mc.options.keySprint;
            case ATTACK -> mc.options.keyAttack;
            case USE -> mc.options.keyUse;
            case DROP -> mc.options.keyDrop;
        };
    }
}