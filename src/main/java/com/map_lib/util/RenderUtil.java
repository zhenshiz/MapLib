package com.map_lib.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@OnlyIn(Dist.CLIENT)
public class RenderUtil {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final Map<String, PlayerSkin> skins = new HashMap<>();

    public static PlayerSkin getSkin(String input) {
        if (skins.containsKey(input)) return skins.get(input);
        handleGameProfileAsync(input);
        if (skins.containsKey(input)) return skins.get(input);
        return DefaultPlayerSkin.get(minecraft.getUser().getProfileId());
    }

    private static void handleGameProfileAsync(String input) {
        ResolvableProfile component = createProfileComponent(input);
        component.resolve()
                .thenApplyAsync(result -> {
                    GameProfile profile = result.gameProfile();
                    try {
                        PlayerSkin playerSkin = minecraft.getSkinManager().getOrLoad(profile).get();
                        skins.put(input, playerSkin);
                    } catch (InterruptedException | ExecutionException ignored) {
                    }
                    return profile;
                })
                .exceptionally(ex -> null);
    }

    private static ResolvableProfile createProfileComponent(String input) {
        try {
            UUID uuid = UUID.fromString(input);
            return new ResolvableProfile(Optional.empty(), Optional.of(uuid), new PropertyMap());
        } catch (IllegalArgumentException e) {
            return new ResolvableProfile(Optional.of(input), Optional.empty(), new PropertyMap());
        }
    }
}
