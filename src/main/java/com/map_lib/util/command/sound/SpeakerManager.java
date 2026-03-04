package com.map_lib.util.command.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpeakerManager {
    private static final Map<Integer, SpeakerSoundInstance> ENTITY_SPEAKERS = new ConcurrentHashMap<>();

    private static final Map<String, SpeakerSoundInstance> FIXED_SPEAKERS = new ConcurrentHashMap<>();

    public static void onPlaySpeaker(int entityId, double x, double y, double z, ResourceLocation sound, SoundSource source, float radius, boolean loop, float volume, float pitch) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        if (entityId >= 0) {
            Entity target = mc.level.getEntity(entityId);
            if (target == null) return;

            if (ENTITY_SPEAKERS.containsKey(entityId)) {
                onStopEntitySpeaker(entityId);
            }

            SpeakerSoundInstance instance = new SpeakerSoundInstance(target, 0, 0, 0, sound, source, radius, loop, volume, pitch);
            mc.getSoundManager().play(instance);
            ENTITY_SPEAKERS.put(entityId, instance);
        } else {
            String key = getPosKey(x, y, z);

            // 如果该位置已经有声音在响，先停掉旧的
            if (FIXED_SPEAKERS.containsKey(key)) {
                onStopPosSpeaker(x, y, z);
            }

            SpeakerSoundInstance instance = new SpeakerSoundInstance(null, x, y, z, sound, source, radius, loop, volume, pitch);
            mc.getSoundManager().play(instance);
            FIXED_SPEAKERS.put(key, instance);
        }
    }

    // 停止实体音响
    public static void onStopEntitySpeaker(int entityId) {
        SpeakerSoundInstance instance = ENTITY_SPEAKERS.remove(entityId);
        if (instance != null) {
            Minecraft.getInstance().getSoundManager().stop(instance);
        }
    }

    // 停止定点音响
    public static void onStopPosSpeaker(double x, double y, double z) {
        String key = getPosKey(x, y, z);
        SpeakerSoundInstance instance = FIXED_SPEAKERS.remove(key);
        if (instance != null) {
            Minecraft.getInstance().getSoundManager().stop(instance);
        }
    }

    private static String getPosKey(double x, double y, double z) {
        return x + "," + y + "," + z;
    }
}