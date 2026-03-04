package com.map_lib.mixin;

import com.map_lib.MapLibClientData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Shadow
    private static float fogRed;
    @Shadow
    private static float fogGreen;
    @Shadow
    private static float fogBlue;

    //设置雾气颜色
    @Inject(method = "setupColor", at = @At("TAIL"))
    private static void maplib$injectCustomFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci) {
        if (MapLibClientData.customSkyColor != null) {

            // 提取原版雾气的亮度 (同样自带了日出日落的完美光影过渡)
            float brightness = Math.max(fogRed, Math.max(fogGreen, fogBlue));

            int color = MapLibClientData.customSkyColor;
            float r = ((color >> 16) & 0xFF) / 255.0F;
            float g = ((color >> 8) & 0xFF) / 255.0F;
            float b = (color & 0xFF) / 255.0F;

            // 覆写雾气颜色
            fogRed = r * brightness;
            fogGreen = g * brightness;
            fogBlue = b * brightness;

            // 强制 OpenGL 应用我们修改后的颜色
            RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
        }
    }
}
