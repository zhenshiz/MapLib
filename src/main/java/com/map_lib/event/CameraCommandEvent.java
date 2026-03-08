package com.map_lib.event;

import com.map_lib.MapLib;
import com.map_lib.MapLibClientData;
import com.map_lib.util.command.camera.CameraShakeManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = MapLib.MOD_ID, value = Dist.CLIENT)
public class CameraCommandEvent {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        CameraShakeManager.tick();
    }

    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        float pt = (float) event.getPartialTick();

        float shakeYaw = CameraShakeManager.getInterpolatedYaw(pt);
        float shakePitch = CameraShakeManager.getInterpolatedPitch(pt);
        float shakeRoll = CameraShakeManager.getInterpolatedRoll(pt);

        if (shakeYaw != 0 || shakePitch != 0 || shakeRoll != 0) {
            event.setYaw(event.getYaw() + shakeYaw);
            event.setPitch(event.getPitch() + shakePitch);
            event.setRoll(event.getRoll() + shakeRoll);
        }
    }

    // 附身模式下的移动输入控制
    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        // 在附身模式下禁用所有移动控制
        if (MapLibClientData.possessedEntityId != -1) {
            var input = event.getInput();
            input.up = false;
            input.down = false;
            input.left = false;
            input.right = false;
            input.jumping = false;
            input.shiftKeyDown = false;
        }
    }

    // 附身模式下的交互控制
    @SubscribeEvent
    public static void onInteraction(InputEvent.InteractionKeyMappingTriggered event) {
        // 在附身模式下禁用所有交互
        if (MapLibClientData.possessedEntityId != -1) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }
}
