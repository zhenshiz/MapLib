package com.map_lib.mixin;

import com.map_lib.MapLibRegistries;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Shadow
    public Input input;
    @Unique
    private int maplib$remainingJumps;
    @Unique
    private boolean maplib$wasOnGround;
    @Unique
    private boolean maplib$wasJumping;
    @Unique
    LocalPlayer maplib$self = (LocalPlayer) (Object) this;

    @Inject(method = "aiStep", at = @At(value = "TAIL"))
    private void handleMultiJump(CallbackInfo ci) {
        int maxJumps = (int) Math.floor(maplib$self.getAttributeValue(MapLibRegistries.MULTI_JUMP));
        boolean isOnGround = maplib$self.onGround();

        if (isOnGround) {
            maplib$remainingJumps = maxJumps;
        } else if (maplib$wasOnGround) {
            maplib$remainingJumps = Math.max(maxJumps - 1, 0);
        }

        if (!maplib$self.isCreative() && !maplib$self.isSpectator()) {
            boolean isJumping = input.jumping;
            if (isJumping && !maplib$wasJumping) {
                if (!isOnGround && !maplib$wasOnGround && maplib$remainingJumps > 0 && maplib$remainingJumps < maxJumps) {
                    maplib$self.jumpFromGround();
                    maplib$self.fallDistance = 0;
                    maplib$remainingJumps--;
                }
            }
            maplib$wasJumping = isJumping;
        }
        maplib$wasOnGround = isOnGround;
    }
}
