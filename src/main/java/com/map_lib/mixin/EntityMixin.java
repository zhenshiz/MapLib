package com.map_lib.mixin;

import com.map_lib.MapLibClientData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract int getId();

    @Shadow
    public abstract Level level();

    // 设置该实体是否发光
    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void injectClientGlow(CallbackInfoReturnable<Boolean> cir) {
        if (this.level() != null && this.level().isClientSide()) {
            if (MapLibClientData.GLOWING_ENTITIES.containsKey(this.getId())) {
                cir.setReturnValue(true);
            }
        }
    }

    //修改发光的颜色
    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void maplib$customGlowColor(CallbackInfoReturnable<Integer> cir) {
        Integer customColor = MapLibClientData.GLOWING_ENTITIES.get(this.getId());

        if (customColor != null) {
            cir.setReturnValue(customColor);
        }
    }
}
