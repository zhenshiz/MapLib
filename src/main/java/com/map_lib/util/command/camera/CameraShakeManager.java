package com.map_lib.util.command.camera;

import com.map_lib.command.CameraCommand;
import net.minecraft.util.Mth;

import java.util.Random;

public class CameraShakeManager {
    private static float amplitude = 0f;
    private static int maxTicks = 0;
    private static int remainingTicks = 0;
    private static CameraCommand.ShakeDecay decay = CameraCommand.ShakeDecay.LINEAR;
    private static final Random random = new Random();

    // 记录上一 Tick 和当前 Tick 的目标偏移量，用于渲染平滑插值
    private static float prevYaw = 0, prevPitch = 0, prevRoll = 0;
    private static float targetYaw = 0, targetPitch = 0, targetRoll = 0;

    public static void start(float amp, int ticks, CameraCommand.ShakeDecay decayMode) {
        amplitude = amp;
        maxTicks = ticks;
        remainingTicks = ticks;
        decay = decayMode;
    }

    public static void tick() {
        if (remainingTicks > 0) {
            remainingTicks--;

            // 保存上一 Tick 的状态
            prevYaw = targetYaw;
            prevPitch = targetPitch;
            prevRoll = targetRoll;

            // 计算当前 Tick 的实际振幅
            float currentAmp = getCurrentAmplitude();

            // 生成新的随机偏移量 (范围：-currentAmp 到 +currentAmp)
            targetYaw = (random.nextFloat() - 0.5f) * 2f * currentAmp;
            targetPitch = (random.nextFloat() - 0.5f) * 2f * currentAmp;
            targetRoll = (random.nextFloat() - 0.5f) * 2f * currentAmp * 0.5f; // Roll (歪头) 稍微减弱一点，防晕3D
        } else {
            // 震动结束，归零
            prevYaw = targetYaw = 0;
            prevPitch = targetPitch = 0;
            prevRoll = targetRoll = 0;
        }
    }

    private static float getCurrentAmplitude() {
        if (remainingTicks <= 0 || maxTicks <= 0) return 0f;

        // 进度：从 1.0 (开始) 递减到 0.0 (结束)
        float progress = (float) remainingTicks / maxTicks;

        return switch (decay) {
            case NONE -> amplitude;
            case LINEAR -> amplitude * progress;
            case EXPONENTIAL -> amplitude * (progress * progress); // 平方曲线，下降更快
        };
    }

    // 获取当前渲染帧的插值平滑 Yaw
    public static float getInterpolatedYaw(float partialTicks) {
        return Mth.lerp(partialTicks, prevYaw, targetYaw);
    }

    public static float getInterpolatedPitch(float partialTicks) {
        return Mth.lerp(partialTicks, prevPitch, targetPitch);
    }

    public static float getInterpolatedRoll(float partialTicks) {
        return Mth.lerp(partialTicks, prevRoll, targetRoll);
    }
}