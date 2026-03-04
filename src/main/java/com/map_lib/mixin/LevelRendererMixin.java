package com.map_lib.mixin;

import com.map_lib.MapLibClientData;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    // 修改太阳和月亮的贴图
    @ModifyArg(
            method = "renderSky",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"),
            index = 1
    )
    private ResourceLocation maplib$modifySkyTextures(ResourceLocation originalTexture) {
        //  如果原版准备渲染太阳，并且我们有自定义太阳贴图
        if (originalTexture.getPath().equals("textures/environment/sun.png")) {
            if (MapLibClientData.customSunTexture != null) {
                return MapLibClientData.customSunTexture;
            }
        }

        // 如果原版准备渲染月亮，并且我们有自定义月亮贴图
        if (originalTexture.getPath().equals("textures/environment/moon_phases.png")) {
            if (MapLibClientData.customMoonTexture != null) {
                return MapLibClientData.customMoonTexture;
            }
        }

        // 其它天空渲染（比如降雨、极光等）放行原版贴图
        return originalTexture;
    }
}
