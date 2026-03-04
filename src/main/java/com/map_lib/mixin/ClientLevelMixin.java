package com.map_lib.mixin;

import com.map_lib.MapLibClientData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel .class)
public class ClientLevelMixin {

    //修改天空颜色
    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
    private void maplib$injectCustomSkyColor(Vec3 pos, float partialTick, CallbackInfoReturnable<Vec3> cir) {
        if (MapLibClientData.customSkyColor != null) {
            int color = MapLibClientData.customSkyColor;

            float r = ((color >> 16) & 0xFF) / 255.0F;
            float g = ((color >> 8) & 0xFF) / 255.0F;
            float b = (color & 0xFF) / 255.0F;

            // 强行返回我们的颜色，截断原版所有的时间、生物群系颜色计算
            cir.setReturnValue(new Vec3(r, g, b));
        }
    }
}
