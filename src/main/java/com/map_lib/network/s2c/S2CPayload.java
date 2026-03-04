package com.map_lib.network.s2c;

import com.lowdragmc.lowdraglib2.Platform;
import com.lowdragmc.lowdraglib2.networking.rpc.RPCPacket;
import com.lowdragmc.lowdraglib2.syncdata.rpc.RPCSender;
import com.map_lib.MapLib;
import com.map_lib.MapLibClientData;
import com.map_lib.command.CameraCommand;
import com.map_lib.command.HudCommand;
import com.map_lib.command.InputCommand;
import com.map_lib.util.CodecUtil;
import com.map_lib.util.command.camera.CameraShakeManager;
import com.map_lib.util.command.input.ClientInputHandler;
import com.map_lib.util.command.pathfinder.ClientPathRenderer;
import com.map_lib.util.command.sound.SpeakerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

import java.util.List;

public class S2CPayload {
    private static final String MOD_ID = MapLib.MOD_ID + ":";
    public static final String GLOW_ENTITY = MOD_ID + "glow_entity";
    public static final String CLEAR_GLOW_ENTITY = MOD_ID + "clear_glow_entity";
    public static final String LOCK_INPUT = MOD_ID + "lock_input";
    public static final String SIMULATE_INPUT = MOD_ID + "simulate_input";
    public static final String CAMERA_SHAkE = MOD_ID + "camera_shake";
    public static final String SET_SUN = MOD_ID + "set_sun";
    public static final String RESET_SUN = MOD_ID + "reset_sun";
    public static final String SET_MOON = MOD_ID + "set_moon";
    public static final String RESET_MOON = MOD_ID + "reset_moon";
    public static final String SET_SKY_COLOR = MOD_ID + "set_sky_color";
    public static final String RESET_SKY_COLOR = MOD_ID + "reset_sky_color";
    public static final String HIDE_HUD = MOD_ID + "hide_hud";
    public static final String SET_SHADER = MOD_ID + "set_shader";
    public static final String RESET_SHADER = MOD_ID + "reset_shader";
    public static final String PLAY_SPEAKER = MOD_ID + "play_speaker";
    public static final String STOP_SPEAKER = MOD_ID + "stop_speaker";
    public static final String STOP_SPEAKER_POS = MOD_ID + "stop_speaker_pos";
    public static final String RENDER_PATH = MOD_ID + "render_path";

    @RPCPacket(GLOW_ENTITY)
    public static void glowEntity(RPCSender sender, CompoundTag entityIdTag, boolean isGlowing, int color) {
        int[] entityIds = entityIdTag.getIntArray("EntityIds");

        if (isGlowing) {
            for (int id : entityIds) {
                MapLibClientData.GLOWING_ENTITIES.put(id, color);
            }
        } else {
            for (int id : entityIds) {
                MapLibClientData.GLOWING_ENTITIES.remove(id);
            }
        }
    }

    @RPCPacket(CLEAR_GLOW_ENTITY)
    public static void clearGlowEntity(RPCSender sender) {
        MapLibClientData.GLOWING_ENTITIES.clear();
    }

    @RPCPacket(LOCK_INPUT)
    public static void onLockInput(RPCSender sender, InputCommand.InputPermission perm, boolean isLocked) {
        if (perm == null) return;
        if (isLocked) {
            MapLibClientData.LOCKED_PERMISSIONS.add(perm);
        } else {
            MapLibClientData.LOCKED_PERMISSIONS.remove(perm);
        }
    }

    @RPCPacket(SIMULATE_INPUT)
    public static void simulateInput(RPCSender sender, InputCommand.InputAction action, int tick) {
        ClientInputHandler.onSimulatePacket(action, tick);
    }

    @RPCPacket(CAMERA_SHAkE)
    public static void cameraShake(RPCSender sender, float amplitude, int tick, CameraCommand.ShakeDecay shakeDecay) {
        CameraShakeManager.start(amplitude, tick, shakeDecay);
    }

    @RPCPacket(SET_SUN)
    public static void setSunTexture(RPCSender sender, ResourceLocation texture) {
        MapLibClientData.customSunTexture = texture;
    }

    @RPCPacket(RESET_SUN)
    public static void resetSunTexture(RPCSender sender) {
        MapLibClientData.customSunTexture = null;
    }

    @RPCPacket(SET_MOON)
    public static void setMoonTexture(RPCSender sender, ResourceLocation texture) {
        MapLibClientData.customMoonTexture = texture;
    }

    @RPCPacket(RESET_MOON)
    public static void resetMoonTexture(RPCSender sender) {
        MapLibClientData.customMoonTexture = null;
    }

    @RPCPacket(SET_SKY_COLOR)
    public static void setSkyColor(RPCSender sender, Integer color) {
        MapLibClientData.customSkyColor = color;
    }

    @RPCPacket(RESET_SKY_COLOR)
    public static void resetSkyColor(RPCSender sender) {
        MapLibClientData.customSkyColor = null;
    }

    @RPCPacket(HIDE_HUD)
    public static void hideHud(RPCSender sender, HudCommand.HudType type, boolean isHidden) {
        if (isHidden) {
            MapLibClientData.HIDDEN_HUD_ELEMENTS.add(type);
        } else {
            MapLibClientData.HIDDEN_HUD_ELEMENTS.remove(type);
        }
    }

    @RPCPacket(SET_SHADER)
    public static void setShader(RPCSender sender, ResourceLocation shader) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
            MapLibClientData.customShader = shader;
            GameRenderer gameRenderer = minecraft.gameRenderer;
            gameRenderer.loadEffect(shader);
        });
    }

    @RPCPacket(RESET_SHADER)
    public static void setShader(RPCSender sender) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
            MapLibClientData.customShader = null;
            GameRenderer gameRenderer = minecraft.gameRenderer;
            gameRenderer.checkEntityPostEffect(minecraft.getCameraEntity());
        });
    }

    @RPCPacket(PLAY_SPEAKER)
    public static void playSpeaker(RPCSender sender, int entityId, double x, double y, double z, ResourceLocation sound, SoundSource source, float radius, boolean loop, float volume, float pitch) {
        SpeakerManager.onPlaySpeaker(entityId, x, y, z, sound, source, radius, loop, volume, pitch);
    }

    @RPCPacket(STOP_SPEAKER)
    public static void stopSpeaker(RPCSender sender, int entityId) {
        SpeakerManager.onStopEntitySpeaker(entityId);
    }

    @RPCPacket(STOP_SPEAKER_POS)
    public static void stopSpeakerPos(RPCSender sender, double x, double y, double z) {
        SpeakerManager.onStopPosSpeaker(x, y, z);
    }

    @RPCPacket(RENDER_PATH)
    public static void renderPath(RPCSender sender, CompoundTag tag, ParticleOptions particle) {
        List<BlockPos> path = CodecUtil.deserializeNBT(CodecUtil.BLOCK_POS_LIST, tag.get("path"), Platform.getFrozenRegistry());
        ClientPathRenderer.updatePath(path, particle);
    }
}
