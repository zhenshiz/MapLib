package com.map_lib;

import com.lowdragmc.lowdraglib2.registry.AutoRegistry;
import com.lowdragmc.lowdraglib2.syncdata.IPersistedSerializable;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.utils.PersistedParser;
import com.map_lib.command.ICommand;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class MapLibRegistries {
    public static AutoRegistry.LDLibRegister<ICommand, Supplier<ICommand>> COMMANDS;

    //数据附件
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MapLib.MOD_ID);
    public static final Supplier<AttachmentType<MapLibInfo>> MAP_LIB_INFO = ATTACHMENT_TYPES.register("map_lib_info", () -> AttachmentType.builder(MapLibInfo::new)
            .serialize(MapLibInfo.CODEC)
            .sync(MapLibInfo.STREAM_CODEC)
            .copyOnDeath()
            .build()
    );

    //玩家属性
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, MapLib.MOD_ID);
    public static final DeferredHolder<Attribute, Attribute> MULTI_JUMP = ATTRIBUTES.register("multi_jump",
            () -> new RangedAttribute("attribute.name.map_lib.multi_jump", 1, 1, 2048).setSyncable(true));

    static {
        COMMANDS = AutoRegistry.LDLibRegister
                .create(ResourceLocation.parse(ICommand.COMMAND_ID), ICommand.class, AutoRegistry::noArgsCreator);
    }

    @Data
    public static class MapLibInfo implements IPersistedSerializable {
        public static final Codec<MapLibInfo> CODEC = PersistedParser.createCodec(MapLibInfo::new);
        public static final StreamCodec<ByteBuf, MapLibInfo> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
        //playerCommand
        @Persisted
        private String name = "";
        @Persisted
        private String skin = "";
        @Persisted
        private String cape = "";
        //poseCommand
        @Persisted
        private Pose pose;
    }
}
