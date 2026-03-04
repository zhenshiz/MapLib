package com.map_lib.command;

import com.lowdragmc.lowdraglib2.registry.annotation.LDLRegister;
import com.map_lib.util.MapLibServerUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;

@LDLRegister(name = PlayerCommand.ID, registry = ICommand.COMMAND_ID)
public class PlayerCommand implements ICommand {
    public static final String ID = "player";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal(this.startCommandName(ID)).requires(source -> source.hasPermission(2))
                .then(Commands.literal("name")
                        .executes(context -> setPlayerName(context, null))
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> setPlayerName(context, StringArgumentType.getString(context, "name")))
                        )
                )
                .then(Commands.literal("skin")
                        .executes(context -> setPlayerSkin(context, null))
                        .then(Commands.argument("playerSkin", StringArgumentType.string())
                                .executes(context -> setPlayerSkin(context, StringArgumentType.getString(context, "playerSkin")))
                        )
                        .then(Commands.argument("resourceSkin", ResourceLocationArgument.id())
                                .executes(context -> setPlayerSkin(context, ResourceLocationArgument.getId(context, "resourceSkin").toString()))
                        )
                )
                .then(Commands.literal("cape")
                        .executes(context -> setPlayerCape(context, null))
                        .then(Commands.argument("playerCape", StringArgumentType.string())
                                .executes(context -> setPlayerCape(context, StringArgumentType.getString(context, "playerCape")))
                        )
                        .then(Commands.argument("resourceCape", ResourceLocationArgument.id())
                                .executes(context -> setPlayerSkin(context, ResourceLocationArgument.getId(context, "resourceCape").toString()))
                        )
                )
        );
    }

    private int setPlayerName(CommandContext<CommandSourceStack> context, String name) {
        return this.defaultPlayerRunCommand(context, player -> {
            if (name != null) {
                MapLibServerUtil.setPlayerName(player, name);
                context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.player.setName", name), true);
            } else {
                MapLibServerUtil.resetPlayerName(player);
                context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.player.resetName"), true);
            }
            return 1;
        });
    }

    private int setPlayerSkin(CommandContext<CommandSourceStack> context, String skin) {
        return this.defaultPlayerRunCommand(context, player -> {
            if (skin != null) {
                MapLibServerUtil.setPlayerSkin(player, skin);
                context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.player.setSkin"), true);
            } else {
                MapLibServerUtil.resetPlayerSkin(player);
                context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.player.resetSkin"), true);
            }
            return 1;
        });
    }

    private int setPlayerCape(CommandContext<CommandSourceStack> context, String cape) {
        return this.defaultPlayerRunCommand(context, player -> {
            if (cape != null) {
                MapLibServerUtil.setPlayerCape(player, cape);
                context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.player.setCape", cape), true);
            } else {
                MapLibServerUtil.resetPlayerCape(player);
                context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.player.resetCape"), true);
            }
            return 1;
        });
    }
}
