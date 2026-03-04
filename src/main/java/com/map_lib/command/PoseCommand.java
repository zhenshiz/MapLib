package com.map_lib.command;

import com.lowdragmc.lowdraglib2.registry.annotation.LDLRegister;
import com.map_lib.util.MapLibServerUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Pose;

import java.util.Arrays;

@LDLRegister(name = PoseCommand.ID, registry = ICommand.COMMAND_ID)
public class PoseCommand implements ICommand {
    public static final String ID = "pose";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal(this.startCommandName(ID)).requires(source -> source.hasPermission(2))
                .executes(context -> setPose(context, null))
                .then(Commands.argument("pose", StringArgumentType.word())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                Arrays.stream(Pose.values()).map(Enum::name).map(String::toLowerCase), builder
                        ))
                        .executes(context -> setPose(context, StringArgumentType.getString(context, "pose")))
                )
        );
    }

    private int setPose(CommandContext<CommandSourceStack> context, String poseStr) {
        return this.defaultPlayerRunCommand(context, player -> {
            if (poseStr != null) {
                Pose pose = Pose.valueOf(poseStr.toUpperCase());
                MapLibServerUtil.setPlayerPose(player, pose);
                context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.pose.set", pose), true);
            } else {
                MapLibServerUtil.resetPlayerPose(player);
                context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.pose.reset"), true);
            }
            return 1;
        });
    }
}
