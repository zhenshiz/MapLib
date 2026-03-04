package com.map_lib.mixin;

import com.map_lib.MapLibClientData;
import com.map_lib.command.InputCommand;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    // 限制水平移动
    @ModifyArg(
            method = "turnPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"),
            index = 0
    )
    private double modifyHorizontalTurn(double originalYRot) {
        if (MapLibClientData.isLocked(InputCommand.InputPermission.ROTATION_HORIZONTAL)) {
            return 0.0;
        }
        return originalYRot;
    }

    // 限制垂直移动
    @ModifyArg(
            method = "turnPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"),
            index = 1
    )
    private double modifyVerticalTurn(double originalXRot) {
        if (MapLibClientData.isLocked(InputCommand.InputPermission.ROTATION_VERTICAL)) {
            return 0.0;
        }
        return originalXRot;
    }
}
