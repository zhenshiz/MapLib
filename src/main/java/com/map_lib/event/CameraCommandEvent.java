package com.map_lib.event;

import com.map_lib.MapLib;
import com.map_lib.util.command.camera.CameraShakeManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
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
}
