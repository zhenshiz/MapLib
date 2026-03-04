package com.map_lib.command;

import com.lowdragmc.lowdraglib2.registry.annotation.LDLRegister;
import com.map_lib.util.MapLibServerUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Collection;

@LDLRegister(name = SoundCommand.ID, registry = ICommand.COMMAND_ID)
public class SoundCommand implements ICommand {
    public static final String ID = "sound";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal(this.startCommandName(ID)).requires(source -> source.hasPermission(2))
                .then(Commands.literal("play")
                        .then(Commands.argument("sound", ResourceLocationArgument.id())
                                .suggests(SuggestionProviders.AVAILABLE_SOUNDS)
                                .then(Commands.argument("category", StringArgumentType.word())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                Arrays.stream(SoundSource.values()).map(Enum::name).map(String::toLowerCase), builder
                                        ))
                                        .then(Commands.argument("radius", FloatArgumentType.floatArg(0))
                                                .then(Commands.argument("loop", BoolArgumentType.bool())
                                                        .then(Commands.literal("bind")
                                                                .then(Commands.argument("targets", EntityArgument.entities())
                                                                        .executes(ctx -> playEntity(ctx, 1.0f, 1.0f))
                                                                        .then(Commands.argument("volume", FloatArgumentType.floatArg(0))
                                                                                .executes(ctx -> playEntity(ctx, FloatArgumentType.getFloat(ctx, "volume"), 1.0f))
                                                                                .then(Commands.argument("pitch", FloatArgumentType.floatArg(0))
                                                                                        .executes(ctx -> playEntity(ctx, FloatArgumentType.getFloat(ctx, "volume"), FloatArgumentType.getFloat(ctx, "pitch")))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                        .then(Commands.literal("at")
                                                                .then(Commands.argument("pos", Vec3Argument.vec3())
                                                                        .executes(ctx -> playPos(ctx, 1.0f, 1.0f))
                                                                        .then(Commands.argument("volume", FloatArgumentType.floatArg(0))
                                                                                .executes(ctx -> playPos(ctx, FloatArgumentType.getFloat(ctx, "volume"), 1.0f))
                                                                                .then(Commands.argument("pitch", FloatArgumentType.floatArg(0))
                                                                                        .executes(ctx -> playPos(ctx, FloatArgumentType.getFloat(ctx, "volume"), FloatArgumentType.getFloat(ctx, "pitch")))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("stop")
                        .then(Commands.literal("bind")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .executes(context -> {
                                            Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
                                            for (Entity entity : targets) {
                                                MapLibServerUtil.stopEntitySpeaker(entity);
                                            }
                                            context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.sound.stop.entitySound", targets.size()), true);
                                            return targets.size();
                                        })
                                )
                        )
                        .then(Commands.literal("at")
                                .then(Commands.argument("pos", Vec3Argument.vec3())
                                        .executes(context -> {
                                            Vec3 pos = Vec3Argument.getVec3(context, "pos");

                                            MapLibServerUtil.stopPosSpeaker(context.getSource().getLevel(), pos);

                                            context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.sound.stop.posSound", pos.x, pos.y, pos.z)
                                                    , true);
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }

    private int playEntity(CommandContext<CommandSourceStack> context, float volume, float pitch) throws CommandSyntaxException {
        ResourceLocation sound = ResourceLocationArgument.getId(context, "sound");
        float radius = FloatArgumentType.getFloat(context, "radius");
        boolean loop = BoolArgumentType.getBool(context, "loop");
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");

        String categoryName = StringArgumentType.getString(context, "category");
        SoundSource source = getSource(categoryName);

        for (Entity entity : targets) {
            MapLibServerUtil.startEntitySpeaker(entity, sound, source, radius, loop, volume, pitch);
        }

        context.getSource().sendSuccess(() -> Component.translatable(
                        "command.mapLib.sound.play.entitySound", targets.size(), source.getName(), volume, pitch)
                , true);
        return targets.size();
    }

    private int playPos(CommandContext<CommandSourceStack> context, float volume, float pitch) {
        ResourceLocation sound = ResourceLocationArgument.getId(context, "sound");
        float radius = FloatArgumentType.getFloat(context, "radius");
        boolean loop = BoolArgumentType.getBool(context, "loop");
        Vec3 pos = Vec3Argument.getVec3(context, "pos");

        String categoryName = StringArgumentType.getString(context, "category");
        SoundSource source = getSource(categoryName);

        MapLibServerUtil.startPosSpeaker(context.getSource().getLevel(), pos, sound, source, radius, loop, volume, pitch);

        context.getSource().sendSuccess(() -> Component.translatable(
                        "command.mapLib.sound.play.posSound", pos.x, pos.y, pos.z, source.getName(), volume, pitch)
                , true);
        return 1;
    }

    private SoundSource getSource(String name) {
        try {
            return SoundSource.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SoundSource.MASTER;
        }
    }
}