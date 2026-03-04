package com.map_lib.command;

import com.lowdragmc.lowdraglib2.registry.ILDLRegister;
import com.map_lib.MapLib;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.SneakyThrows;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ICommand extends ILDLRegister<ICommand, Supplier<ICommand>> {
    String COMMAND_ID = MapLib.MOD_ID + ":command";

    void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection);

    default String startCommandName(String name) {
        return "ml:" + name;
    }

    default CommandSyntaxException playerOnlyException() {
        return new CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(), Component.translatable("command.target.entity.only"));
    }

    default CommandSyntaxException entityOnlyException() {
        return new CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(), Component.translatable("command.target.player.only"));
    }

    @SneakyThrows
    default int defaultPlayerRunCommand(CommandContext<CommandSourceStack> context, Function<ServerPlayer, Integer> execute) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();
        if (player != null) {
            return execute.apply(player);
        } else {
            throw playerOnlyException();
        }
    }

    @SneakyThrows
    default int defaultEntityRunCommand(CommandContext<CommandSourceStack> context, Function<Entity, Integer> execute) {
        CommandSourceStack source = context.getSource();
        Entity entity = source.getEntity();
        if (entity != null) {
            return execute.apply(entity);
        } else {
            throw entityOnlyException();
        }
    }
}
