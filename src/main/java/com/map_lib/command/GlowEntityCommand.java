package com.map_lib.command;

import com.lowdragmc.lowdraglib2.registry.annotation.LDLRegister;
import com.map_lib.util.MapLibServerUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import lombok.SneakyThrows;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

@LDLRegister(name = GlowEntityCommand.ID, registry = ICommand.COMMAND_ID)
public class GlowEntityCommand implements ICommand {
    public static final String ID = "glow_entity";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal(this.startCommandName(ID)).requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.players())
                        .executes(this::clearGlowEntity)
                        .then(Commands.argument("glowEntity", EntityArgument.entities())
                                .then(Commands.argument("isGlowing", BoolArgumentType.bool())
                                        .executes(context -> glowEntity(context, "FFFFFF"))
                                        .then(Commands.argument("hexColor", StringArgumentType.word())
                                                .executes(context -> glowEntity(context, StringArgumentType.getString(context, "hexColor")))
                                        )
                                )
                        )
                )
        );
    }

    @SneakyThrows
    private int clearGlowEntity(CommandContext<CommandSourceStack> context) {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "player");
        if (players.isEmpty()) {
            context.getSource().sendFailure(Component.translatable("command.mapLib.glowEntity.player.isEmpty"));
            return 0;
        }
        MapLibServerUtil.resetGlowEntity(players);
        context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.glowEntity.clear"), true);
        return 1;
    }

    @SneakyThrows
    private int glowEntity(CommandContext<CommandSourceStack> context, String hexColor) {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "player");
        Collection<? extends Entity> glowEntity = EntityArgument.getEntities(context, "glowEntity");
        boolean isGlowing = BoolArgumentType.getBool(context, "isGlowing");
        int color;
        try {
            color = Integer.parseInt(hexColor.replace("#", ""), 16);
        } catch (NumberFormatException e) {
            return 0;
        }

        if (players.isEmpty()) {
            context.getSource().sendFailure(Component.translatable("command.mapLib.glowEntity.player.isEmpty"));
            return 0;
        }
        if (glowEntity.isEmpty()) {
            context.getSource().sendFailure(Component.translatable("command.mapLib.glowEntity.glowEntity.isEmpty"));
            return 0;
        }
        MapLibServerUtil.glowEntity(players, glowEntity, isGlowing, color);
        context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.glowEntity.set"), true);
        return 1;
    }
}
