package com.map_lib.mixin;

import com.map_lib.MapLibClientData;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // 拦截原版的着色器检查逻辑 (按 F5、切换视角、附身实体时都会触发)
    @Inject(method = "checkEntityPostEffect", at = @At("HEAD"), cancellable = true)
    private void maplib$protectCustomShader(Entity entity, CallbackInfo ci) {
        if (MapLibClientData.customShader != null) {
            ci.cancel();
        }
    }

    // 在附身模式下隐藏玩家手部
    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void maplib$hideHandInPossessMode(CallbackInfo ci) {
        if (MapLibClientData.possessedEntityId != -1) {
            ci.cancel();
        }
    }
}
