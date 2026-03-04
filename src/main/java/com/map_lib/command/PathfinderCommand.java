package com.map_lib.command;

import com.map_lib.util.MapLibServerUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import lombok.SneakyThrows;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

//@LDLRegister(name = PathfinderCommand.ID, registry = ICommand.COMMAND_ID)
public class PathfinderCommand implements ICommand {
    public static final String ID = "pathfind";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        var root = Commands.literal(this.startCommandName(ID)).requires(source -> source.hasPermission(2));
        root.then(Commands.literal("from")
                .then(Commands.literal("entity")
                        .then(Commands.argument("startEntity", EntityArgument.entity())
                                .then(Commands.literal("to")
                                        .then(Commands.argument("endPos", BlockPosArgument.blockPos())
                                                .executes(ctx -> pathFromEntityToPos(ctx, null))
                                                .then(Commands.argument("particle", ParticleArgument.particle(buildContext))
                                                        .executes(ctx -> pathFromEntityToPos(ctx, ParticleArgument.getParticle(ctx, "particle")))
                                                )
                                        )
                                )
                                .then(Commands.literal("follow")
                                        .then(Commands.argument("endEntity", EntityArgument.entity())
                                                .executes(ctx -> pathFromEntityToEntity(ctx, null))
                                                .then(Commands.argument("particle", ParticleArgument.particle(buildContext))
                                                        .executes(ctx -> pathFromEntityToEntity(ctx, ParticleArgument.getParticle(ctx, "particle")))
                                                )
                                        )
                                )
                        )
                )
        );

        root.then(Commands.literal("from")
                .then(Commands.literal("block")
                        .then(Commands.argument("startPos", BlockPosArgument.blockPos())
                                .then(Commands.literal("to")
                                        .then(Commands.argument("endPos", BlockPosArgument.blockPos())
                                                .executes(ctx -> pathFromPosToPos(ctx, null))
                                                .then(Commands.argument("particle", ParticleArgument.particle(buildContext))
                                                        .executes(ctx -> pathFromPosToPos(ctx, ParticleArgument.getParticle(ctx, "particle")))
                                                )
                                        )
                                )
                                .then(Commands.literal("follow")
                                        .then(Commands.argument("endEntity", EntityArgument.entity())
                                                .executes(ctx -> pathFromPosToEntity(ctx, null))
                                                .then(Commands.argument("particle", ParticleArgument.particle(buildContext))
                                                        .executes(ctx -> pathFromPosToEntity(ctx, ParticleArgument.getParticle(ctx, "particle")))
                                                )
                                        )
                                )
                        )
                )
        );

        dispatcher.register(root);
    }

    @SneakyThrows
    private int pathFromEntityToPos(CommandContext<CommandSourceStack> context, ParticleOptions particle) {
        return executePathfinding(
                context.getSource().getPlayerOrException(),
                EntityArgument.getEntity(context, "startEntity").blockPosition(),
                BlockPosArgument.getLoadedBlockPos(context, "endPos"),
                particle
        );
    }

    @SneakyThrows
    private int pathFromEntityToEntity(CommandContext<CommandSourceStack> context, ParticleOptions particle) {
        return executePathfinding(
                context.getSource().getPlayerOrException(),
                EntityArgument.getEntity(context, "startEntity").blockPosition(),
                EntityArgument.getEntity(context, "endEntity").blockPosition(),
                particle
        );
    }

    @SneakyThrows
    private int pathFromPosToPos(CommandContext<CommandSourceStack> context, ParticleOptions particle) {
        return executePathfinding(
                context.getSource().getPlayerOrException(),
                BlockPosArgument.getLoadedBlockPos(context, "startPos"),
                BlockPosArgument.getLoadedBlockPos(context, "endPos"),
                particle
        );
    }

    @SneakyThrows
    private int pathFromPosToEntity(CommandContext<CommandSourceStack> context, ParticleOptions particle) {
        return executePathfinding(
                context.getSource().getPlayerOrException(),
                BlockPosArgument.getLoadedBlockPos(context, "startPos"),
                EntityArgument.getEntity(context, "endEntity").blockPosition(),
                particle
        );
    }

    private int executePathfinding(ServerPlayer observer, BlockPos start, BlockPos end, ParticleOptions particle) {
        boolean success = MapLibServerUtil.startPathfinding(observer, start, end, particle);

        if (success) {
            observer.sendSystemMessage(Component.literal("§a路径计算完成并已显示。"));
            return 1;
        } else {
            observer.sendSystemMessage(Component.literal("§c无法找到路径或目标太远！"));
            return 0;
        }
    }
}