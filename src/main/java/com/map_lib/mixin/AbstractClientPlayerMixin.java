package com.map_lib.mixin;

import com.map_lib.MapLibRegistries;
import com.map_lib.util.RenderUtil;
import com.map_lib.util.common.StrUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {

    //修改玩家皮肤和披风
    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void injectLocalSkinAndCape(CallbackInfoReturnable<PlayerSkin> cir) {
        AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;

        if (player.hasData(MapLibRegistries.MAP_LIB_INFO)) {
            String skinStr = player.getData(MapLibRegistries.MAP_LIB_INFO).getSkin();
            String capeStr = player.getData(MapLibRegistries.MAP_LIB_INFO).getCape();

            if (StrUtil.isEmpty(skinStr) && StrUtil.isEmpty(capeStr)) {
                return;
            }

            PlayerSkin original = cir.getReturnValue();
            ResourceLocation skinLoc = original.texture();
            ResourceLocation capeLoc = original.capeTexture();
            ResourceLocation elytraLoc = original.elytraTexture();
            PlayerSkin.Model model = original.model();

            boolean modified = false;

            if (!StrUtil.isEmpty(skinStr)) {
                modified = true;
                if (skinStr.contains(":")) {
                    skinLoc = ResourceLocation.parse(skinStr);
                } else {
                    PlayerSkin customProfileSkin = RenderUtil.getSkin(skinStr);
                    skinLoc = customProfileSkin.texture();
                    model = customProfileSkin.model();
                }
            }

            if (!StrUtil.isEmpty(capeStr)) {
                modified = true;
                if (capeStr.contains(":")) {
                    capeLoc = ResourceLocation.parse(capeStr);
                    elytraLoc = capeLoc;
                } else {
                    PlayerSkin customProfileCape = RenderUtil.getSkin(capeStr);
                    capeLoc = customProfileCape.capeTexture();
                    elytraLoc = customProfileCape.elytraTexture();
                }
            }

            if (modified) {
                PlayerSkin newSkin = new PlayerSkin(
                        skinLoc,
                        original.textureUrl(),
                        capeLoc,
                        elytraLoc,
                        model,
                        original.secure()
                );
                cir.setReturnValue(newSkin);
            }
        }
    }
}
