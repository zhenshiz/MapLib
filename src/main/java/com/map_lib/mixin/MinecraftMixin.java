package com.map_lib.mixin;

import com.map_lib.MapLibClientData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Final
    @Shadow
    public GameRenderer gameRenderer;

    /**
     * 修改摄像头实体，当有附身实体时返回附身的实体
     * 这样玩家视角就会跟随附身的实体，但玩家本体不会消失
     */
    @Inject(method = "getCameraEntity", at = @At("HEAD"), cancellable = true)
    private void maplib$getPossessedEntity(CallbackInfoReturnable<Entity> cir) {
        if (MapLibClientData.possessedEntityId != -1) {
            Entity possessedEntity = gameRenderer.getMinecraft().player.level().getEntity(MapLibClientData.possessedEntityId);
            if (possessedEntity != null) {
                cir.setReturnValue(possessedEntity);
            } else {
                // 如果实体不存在，清除附身状态
                MapLibClientData.possessedEntityId = -1;
            }
        }
    }
}