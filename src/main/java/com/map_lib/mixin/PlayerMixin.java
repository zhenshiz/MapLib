package com.map_lib.mixin;

import com.map_lib.MapLibRegistries;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

    // 修改玩家的pose动作
    @Inject(method = "updatePlayerPose", at = @At("HEAD"), cancellable = true)
    private void injectForcedPose(CallbackInfo ci) {
        Player player = (Player) (Object) this;

        if (player.hasData(MapLibRegistries.MAP_LIB_INFO)) {
            Pose pose = player.getData(MapLibRegistries.MAP_LIB_INFO).getPose();

            if (pose != null) {
                try {
                    player.setPose(pose);
                    ci.cancel();
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }
}
