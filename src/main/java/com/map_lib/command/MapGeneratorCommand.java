package com.map_lib.command;

import com.lowdragmc.lowdraglib2.registry.annotation.LDLRegister;
import com.map_lib.util.command.mapgenerator.MapArtGenerator;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

@LDLRegister(name = MapGeneratorCommand.ID, registry = ICommand.COMMAND_ID)
public class MapGeneratorCommand implements ICommand {
    public static final String ID = "map_generator";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        var root = Commands.literal(this.startCommandName(ID)).requires(source -> source.hasPermission(2));

        root.then(Commands.argument("url", StringArgumentType.string()) // 支持 URL、路径、资源路径
                .then(Commands.argument("width", IntegerArgumentType.integer(1, 16))
                        .then(Commands.argument("height", IntegerArgumentType.integer(1, 16))
                                .then(Commands.argument("mode", StringArgumentType.word())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                Arrays.stream(ArtMode.values()).map(Enum::name).map(String::toLowerCase), builder
                                        ))
                                        .executes(context -> {
                                            String url = StringArgumentType.getString(context, "url");
                                            int w = IntegerArgumentType.getInteger(context, "width");
                                            int h = IntegerArgumentType.getInteger(context, "height");
                                            String modeStr = StringArgumentType.getString(context, "mode");

                                            ArtMode mode;
                                            try {
                                                mode = ArtMode.valueOf(modeStr.toUpperCase());
                                            } catch (IllegalArgumentException e) {
                                                context.getSource().sendFailure(Component.translatable("command.mapLib.mapGenerator.artMode.error"));
                                                return 0;
                                            }

                                            context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.mapGenerator.download"), false);

                                            MapArtGenerator.generate(
                                                    context.getSource().getPlayerOrException(),
                                                    url, w, h, mode
                                            );

                                            return 1;
                                        })
                                )
                        )
                )
        );

        dispatcher.register(root);
    }

    public enum ArtMode {
        COLOR,  // 彩色模式 (原画)
        BW;     // 黑白模式 (自动灰度化)
    }
}
