package com.map_lib.event;

import com.map_lib.MapLib;
import com.map_lib.MapLibRegistries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@EventBusSubscriber(modid = MapLib.MOD_ID)
public class MapLibCommonEvents {
    @SubscribeEvent
    public static void onAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, MapLibRegistries.MULTI_JUMP, 1);
    }
}
