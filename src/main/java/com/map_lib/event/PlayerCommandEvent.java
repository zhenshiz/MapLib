package com.map_lib.event;

import com.map_lib.MapLib;
import com.map_lib.MapLibRegistries;
import com.map_lib.util.common.StrUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = MapLib.MOD_ID)
public class PlayerCommandEvent {

    @SubscribeEvent
    public static void onNameFormat(PlayerEvent.NameFormat event) {
        Player player = event.getEntity();
        if (player.hasData(MapLibRegistries.MAP_LIB_INFO)) {
            String name = player.getData(MapLibRegistries.MAP_LIB_INFO).getName();
            if (!StrUtil.isEmpty(name)) {
                event.setDisplayname(Component.literal(name));
            }
        }
    }

    @SubscribeEvent
    public static void onTabListNameFormat(PlayerEvent.TabListNameFormat event) {
        Player player = event.getEntity();
        if (player.hasData(MapLibRegistries.MAP_LIB_INFO)) {
            String name = player.getData(MapLibRegistries.MAP_LIB_INFO).getName();
            if (!StrUtil.isEmpty(name)) {
                Component baseName = Component.literal(name);
                Component formattedName = PlayerTeam.formatNameForTeam(player.getTeam(), baseName);
                event.setDisplayName(formattedName);
            }
        }
    }
}
