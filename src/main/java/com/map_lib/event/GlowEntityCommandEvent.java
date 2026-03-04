package com.map_lib.event;

import com.map_lib.MapLib;
import com.map_lib.MapLibClientData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = MapLib.MOD_ID, value = Dist.CLIENT)
public class GlowEntityCommandEvent {

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        // 玩家退出当前服务器时，彻底清空高亮缓存
        MapLibClientData.GLOWING_ENTITIES.clear();
    }
}
