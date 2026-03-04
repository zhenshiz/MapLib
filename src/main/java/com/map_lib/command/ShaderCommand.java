package com.map_lib.command;

import com.lowdragmc.lowdraglib2.registry.annotation.LDLRegister;
import com.map_lib.util.MapLibServerUtil;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@LDLRegister(name = ShaderCommand.ID, registry = ICommand.COMMAND_ID)
public class ShaderCommand implements ICommand {
    public static final String ID = "shader";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal(this.startCommandName(ID)).requires(source -> source.hasPermission(2))
                .then(Commands.argument("shader", ResourceLocationArgument.id())
                        .executes(context -> this.defaultPlayerRunCommand(context, player -> {
                            ResourceLocation shader = ResourceLocationArgument.getId(context, "shader");

                            MapLibServerUtil.setShader(player, shader);
                            context.getSource().sendSuccess(() -> Component.translatable(
                                    "command.mapLib.shader.set", shader.toString()
                            ), false);
                            return 1;
                        }))
                )
                .then(Commands.literal("clear")
                        .executes(context -> this.defaultPlayerRunCommand(context, player -> {
                            MapLibServerUtil.clearShader(player);
                            context.getSource().sendSuccess(() -> Component.translatable(
                                    "command.mapLib.shader.clear"
                            ), false);
                            return 1;
                        }))
                )
        );
    }
}