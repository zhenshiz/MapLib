package com.map_lib.util;

import com.lowdragmc.lowdraglib2.Platform;
import com.lowdragmc.lowdraglib2.integration.kjs.KJSBindings;
import com.lowdragmc.lowdraglib2.networking.rpc.RPCPacketDistributor;
import com.map_lib.MapLibRegistries;
import com.map_lib.command.CameraCommand;
import com.map_lib.command.HudCommand;
import com.map_lib.command.InputCommand;
import com.map_lib.command.WorldCommand;
import com.map_lib.network.s2c.S2CPayload;
import com.map_lib.util.command.pathfinder.SimplePathfinder;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.List;

@KJSBindings(value = "MapLibUtil")
public class MapLibServerUtil {

    //PlayerCommand
    @Info("""
            设置玩家名称
            
            player 目标玩家
            name 名称
            """)
    public static void setPlayerName(ServerPlayer player, String name) {
        MapLibRegistries.MapLibInfo mapLibInfo = player.getData(MapLibRegistries.MAP_LIB_INFO);
        mapLibInfo.setName(name);
        player.setData(MapLibRegistries.MAP_LIB_INFO, mapLibInfo);
        player.refreshTabListName();
        player.refreshDisplayName();
    }

    @Info("""
            重置玩家名称
            
            player 目标玩家
            """)
    public static void resetPlayerName(ServerPlayer player) {
        setPlayerName(player, "");
    }

    @Info("""
            设置玩家皮肤
            
            player 目标玩家
            skin 皮肤，可以填玩家id或者资源包路径（区分条件为是否有':'）
            """)
    public static void setPlayerSkin(ServerPlayer player, String skin) {
        MapLibRegistries.MapLibInfo mapLibInfo = player.getData(MapLibRegistries.MAP_LIB_INFO);
        mapLibInfo.setSkin(skin);
        player.setData(MapLibRegistries.MAP_LIB_INFO, mapLibInfo);
    }

    @Info("""
            重置玩家皮肤
            
            player 目标玩家
            """)
    public static void resetPlayerSkin(ServerPlayer player) {
        setPlayerSkin(player, "");
    }

    @Info("""
            设置玩家披风
            
            player 目标玩家
            cape 披风，可以填玩家id或者资源包路径（区分条件为是否有':'）
            """)
    public static void setPlayerCape(ServerPlayer player, String cape) {
        MapLibRegistries.MapLibInfo mapLibInfo = player.getData(MapLibRegistries.MAP_LIB_INFO);
        mapLibInfo.setCape(cape);
        player.setData(MapLibRegistries.MAP_LIB_INFO, mapLibInfo);
    }

    @Info("""
            重置玩家披风
            
            player 目标玩家
            """)
    public static void resetPlayerCape(ServerPlayer player) {
        setPlayerCape(player, "");
    }

    // glowEntityCommand
    @Info("""
            设置目标实体是否发光，仅目标玩家可见
            
            players 可见的玩家列表
            glowEntity 发光的实体列表
            isGlowing 是否发光
            color 发光的颜色
            """)
    public static void glowEntity(Collection<ServerPlayer> players, Collection<? extends Entity> glowEntity, boolean isGlowing, int color) {
        int[] ids = glowEntity.stream().mapToInt(Entity::getId).toArray();
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("EntityIds", ids);
        players.forEach(player -> RPCPacketDistributor.rpcToPlayer(player, S2CPayload.GLOW_ENTITY, tag, isGlowing, color));
    }

    @Info("""
            关闭其它实体对玩家发光的隐藏
            
            players 可见的玩家列表
            """)
    public static void resetGlowEntity(Collection<ServerPlayer> players) {
        players.forEach(player -> RPCPacketDistributor.rpcToPlayer(player, S2CPayload.CLEAR_GLOW_ENTITY));
    }

    //poseCommand
    @Info("""
            设置玩家的Pose
            
            player 目标玩家
            pose 玩家动作
            """)
    public static void setPlayerPose(ServerPlayer player, Pose pose) {
        MapLibRegistries.MapLibInfo mapLibInfo = player.getData(MapLibRegistries.MAP_LIB_INFO);
        mapLibInfo.setPose(pose);
        player.setData(MapLibRegistries.MAP_LIB_INFO, mapLibInfo);
    }

    @Info("""
            重置玩家动作
            
            player 目标玩家
            """)
    public static void resetPlayerPose(ServerPlayer player) {
        setPlayerPose(player, null);
    }

    //inputCommand
    @Info("""
            设置玩家按键权限
            
            player 目标玩家
            perm 按键类型
            isLocked 是否上锁
            """)
    public static void setInputLock(ServerPlayer player, InputCommand.InputPermission perm, boolean isLocked) {
        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.LOCK_INPUT, perm, isLocked);
    }

    @Info("""
            模拟玩家输入
            
            player 目标玩家
            action 动作类型
            ticks 持续时间 (tick)
            """)
    public static void simulateInput(ServerPlayer player, InputCommand.InputAction action, int ticks) {
        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.SIMULATE_INPUT, action, ticks);
    }

    //cameraCommand
    @Info("""
            镜头抖动
            
            player 目标玩家
            amplitude 震动强度
            tick 持续时间
            shakeDecay 衰减模式
            """)
    public static void cameraShake(ServerPlayer player, float amplitude, int tick, CameraCommand.ShakeDecay shakeDecay) {
        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.CAMERA_SHAkE, amplitude, tick, shakeDecay);
    }

    @Info("""
            让指定坐标半径内的所有玩家屏幕震动 (可以做 Boss 落地的 AOE 震动)

            level 服务端的世界
            center 坐标中心
            radius 范围
            amplitude 震动强度
            tick 持续时间
            shakeDecay 衰减模式
            """)
    public static void cameraShakeInRadius(ServerLevel level, Vec3 center, double radius, float amplitude, int tick, CameraCommand.ShakeDecay shakeDecay) {
        for (ServerPlayer player : level.players()) {
            if (player.position().distanceToSqr(center) <= radius * radius) {
                // 距离中心越远，震动越弱
                double distanceRatio = 1.0 - (player.position().distanceTo(center) / radius);
                float finalAmp = (float) (amplitude * distanceRatio);

                RPCPacketDistributor.rpcToPlayer(player, S2CPayload.CAMERA_SHAkE, finalAmp, tick, shakeDecay);
            }
        }
    }

    @Info("""
            让玩家摄像头附身到指定实体上

            player 目标玩家
            entity 要附身的实体
            """)
    public static void setPossessedEntity(ServerPlayer player, Entity entity) {
        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.SET_POSSESSED_ENTITY, entity.getId());
    }

    @Info("""
            清除玩家的附身状态

            player 目标玩家
            """)
    public static void clearPossessedEntity(ServerPlayer player) {
        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.SET_POSSESSED_ENTITY, -1);
    }

    //worldCommand
    @Info("""
            设置玩家看到的太阳贴图
            
            player 目标玩家
            texture 太阳贴图的资源路径 (例如 'map_lib:textures/environment/blood_sun.png')
            """)
    public static void setSunTexture(ServerPlayer player, ResourceLocation texture) {
        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.SET_SUN, texture);
    }

    @Info("""
            重置玩家看到的太阳贴图
            
            player 目标玩家
            """)
    public static void resetSunTexture(ServerPlayer player) {
        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.RESET_SUN);
    }

    @Info("""
            设置玩家看到的月亮贴图
            
            player 目标玩家
            texture 月亮的贴图
            """)
    public static void setMoonTexture(ServerPlayer player, ResourceLocation texture) {
        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.SET_MOON, texture);
    }

    @Info("""
            重置玩家看到的月亮贴图
            
            player 目标玩家
            """)
    public static void resetMoonTexture(ServerPlayer player) {
        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.RESET_MOON);
    }

    @Info("""
            设置玩家看到的天空颜色
            
            player 目标玩家
            color RGB 颜色对应的整数 (如 0xFF0000 代表红色)
            """)
    public static void setSkyColor(ServerPlayer player, Integer color) {
        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.SET_SKY_COLOR, color);
    }

    @Info("""
            重置玩家看到的天空颜色
            
            player 目标玩家
            """)
    public static void resetSkyColor(ServerPlayer player) {
        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.RESET_SKY_COLOR);
    }

    @Info("""
            重置玩家的环境渲染
            
            player 目标玩家
            """)
    public static void resetWorldEnv(ServerPlayer player) {
        for (WorldCommand.EnvType value : WorldCommand.EnvType.values()) {
            value.reset(player);
        }
    }

    //hudCommand
    @Info("""
            设置是否隐藏玩家的特定 HUD 界面元素
            
            player 目标玩家
            type 要隐藏的界面元素类型
            isHidden true 为隐藏，false 为显示
            """)
    public static void setHudHidden(ServerPlayer player, HudCommand.HudType type, boolean isHidden) {
        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.HIDE_HUD, type, isHidden);
    }

    //shaderCommand
    @Info("""
            给玩家的屏幕添加后处理着色器 (Post-processing Shader)
            
            player 目标玩家
            shader 着色器的资源路径 (例如 'minecraft:shaders/post/desaturate.json')
            """)
    public static void setShader(ServerPlayer player, ResourceLocation shader) {
        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.SET_SHADER, shader);
    }

    @Info("""
            清除玩家屏幕上的后处理着色器
            
            player 目标玩家
            """)
    public static void clearShader(ServerPlayer player) {
        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.RESET_SHADER);
    }

    //soundCommand
    @Info("""
            让某个实体变成音响，播放跟随它的声音
            
            target 目标实体 (通常是拿着物品的玩家)
            sound 声音资源路径 (如 'minecraft:music_disc.cat')
            source 声音类别
            radius 声音传播半径 (超出半径静音，回来继续听)
            loop 是否循环播放
            volume 音量
            pitch 音高
            """)
    public static void startEntitySpeaker(Entity target, ResourceLocation sound, SoundSource source, float radius, boolean loop, float volume, float pitch) {
        for (ServerPlayer player : ((ServerLevel) target.level()).players()) {
            RPCPacketDistributor.rpcToPlayer(player, S2CPayload.PLAY_SPEAKER, target.getId(), 0.0, 0.0, 0.0, sound, source, radius, loop, volume, pitch);
        }
    }

    @Info("""
            设置某个坐标点播放音乐
            
            level 维度
            pos 坐标
            sound 声音资源路径 (如 'minecraft:music_disc.cat')
            source 声音类别
            radius 声音传播半径 (超出半径静音，回来继续听)
            loop 是否循环播放
            volume 音量
            pitch 音高
            """)
    public static void startPosSpeaker(ServerLevel level, Vec3 pos, ResourceLocation sound, SoundSource source, float radius, boolean loop, float volume, float pitch) {
        for (ServerPlayer player : level.players()) {
            RPCPacketDistributor.rpcToPlayer(player, S2CPayload.PLAY_SPEAKER, -1, pos.x, pos.y, pos.z, sound, source, radius, loop, volume, pitch);
        }
    }

    @Info("""
            停止某个实体的音响播放
            
            target 目标实体
            """)
    public static void stopEntitySpeaker(Entity target) {
        for (ServerPlayer player : target.getServer().getPlayerList().getPlayers()) {
            RPCPacketDistributor.rpcToPlayer(player, S2CPayload.STOP_SPEAKER, target.getId());
        }
    }

    @Info("""
            停止某个坐标的音响播放
            
            level 维度
            pos 坐标
            """)
    public static void stopPosSpeaker(ServerLevel level, Vec3 pos) {
        for (ServerPlayer player : level.players()) {
            RPCPacketDistributor.rpcToPlayer(player, S2CPayload.STOP_SPEAKER_POS, pos.x, pos.y, pos.z);
        }
    }

    // particleCOmmand
    @Info("""
            开始寻路并渲染粒子 (坐标 -> 坐标)
            
            observer 观察者(看到粒子的玩家)
            start 起点坐标
            end 终点坐标
            particle 粒子效果 (填 null 则默认使用 END_ROD)
            @return 是否成功找到路径
            """)
    public static boolean startPathfinding(ServerPlayer observer, BlockPos start, BlockPos end, ParticleOptions particle) {
        if (start.distManhattan(end) > 200) {
            return false;
        }

        List<BlockPos> path = SimplePathfinder.findPath(observer.serverLevel(), start, end);

        if (path.isEmpty()) {
            return false;
        }

        ParticleOptions finalParticle = (particle != null) ? particle : ParticleTypes.END_ROD;

        renderPath(observer, path, finalParticle);
        return true;
    }

    @Info("""
            开始寻路并渲染粒子 (实体 -> 坐标)
            
            observer 观察者
            startEntity 起点实体
            endPos 终点坐标
            particle 粒子效果
            @return 是否成功
            """)
    public static boolean startPathfinding(ServerPlayer observer, Entity startEntity, BlockPos endPos, ParticleOptions particle) {
        return startPathfinding(observer, startEntity.blockPosition(), endPos, particle);
    }

    @Info("""
            开始寻路并渲染粒子 (坐标 -> 实体)
            
            observer 观察者
            startPos 起点坐标
            endEntity 终点实体
            particle 粒子效果
            @return 是否成功
            """)
    public static boolean startPathfinding(ServerPlayer observer, BlockPos startPos, Entity endEntity, ParticleOptions particle) {
        return startPathfinding(observer, startPos, endEntity.blockPosition(), particle);
    }

    @Info("""
            开始寻路并渲染粒子 (实体 -> 实体)
            
            observer 观察者
            startEntity 起点实体
            endEntity 终点实体
            particle 粒子效果
            @return 是否成功
            """)
    public static boolean startPathfinding(ServerPlayer observer, Entity startEntity, Entity endEntity, ParticleOptions particle) {
        return startPathfinding(observer, startEntity.blockPosition(), endEntity.blockPosition(), particle);
    }

    /**
     * 底层方法：直接渲染给定的路径点列表
     * (通常由 startPathfinding 调用，但也可以供高级用户直接使用)
     *
     * @param player   接收包的玩家
     * @param path     路径点列表
     * @param particle 粒子效果
     */
    private static void renderPath(ServerPlayer player, List<BlockPos> path, ParticleOptions particle) {
        ListTag pathListTag = (ListTag) CodecUtil.serializeNBT(CodecUtil.BLOCK_POS_LIST, path, Platform.getFrozenRegistry());

        CompoundTag pathWrapper = new CompoundTag();
        pathWrapper.put("path", pathListTag);

        if (particle == null) particle = ParticleTypes.END_ROD;

        RPCPacketDistributor.rpcToPlayer(player, S2CPayload.RENDER_PATH, pathWrapper, particle);
    }
}
