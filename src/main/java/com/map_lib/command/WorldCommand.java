package com.map_lib.command;

import com.lowdragmc.lowdraglib2.registry.annotation.LDLRegister;
import com.map_lib.util.MapLibServerUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

@LDLRegister(name = WorldCommand.ID, registry = ICommand.COMMAND_ID)
public class WorldCommand implements ICommand {
    public static final String ID = "world";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        var envNode = Commands.literal(this.startCommandName(ID)).requires(source -> source.hasPermission(2));

        for (EnvType type : EnvType.values()) {
            String nodeName = type.name().toLowerCase();

            envNode.then(Commands.literal(nodeName)
                    .then(type.buildNode(this))
                    .then(Commands.literal("clear")
                            .executes(context -> this.defaultPlayerRunCommand(context, player -> {
                                type.reset(player);
                                context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.world.clear." + nodeName), false);
                                return 1;
                            }))
                    )
            );
        }

        envNode.then(Commands.literal("clear").executes(context ->
                this.defaultPlayerRunCommand(context, player -> {
                    MapLibServerUtil.resetWorldEnv(player);
                    return 1;
                })
        ));

        dispatcher.register(envNode);
    }

    public enum EnvType {
        SUN {
            @Override
            public ArgumentBuilder<CommandSourceStack, ?> buildNode(WorldCommand cmd) {
                return Commands.argument("texture", ResourceLocationArgument.id())
                        .executes(context -> cmd.defaultPlayerRunCommand(context, player -> {
                            ResourceLocation res = ResourceLocationArgument.getId(context, "texture");
                            MapLibServerUtil.setSunTexture(player, res);
                            context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.world.set.sun", res.toString()), false);
                            return 1;
                        }));
            }

            @Override
            public void reset(ServerPlayer player) {
                MapLibServerUtil.resetSunTexture(player);
            }
        },
        MOON {
            @Override
            public ArgumentBuilder<CommandSourceStack, ?> buildNode(WorldCommand cmd) {
                return Commands.argument("texture", ResourceLocationArgument.id())
                        .executes(context -> cmd.defaultPlayerRunCommand(context, player -> {
                            ResourceLocation res = ResourceLocationArgument.getId(context, "texture");
                            MapLibServerUtil.setMoonTexture(player, res);
                            context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.world.set.moon", res.toString()), false);
                            return 1;
                        }));
            }

            @Override
            public void reset(ServerPlayer player) {
                MapLibServerUtil.resetMoonTexture(player);
            }
        },
        SKY {
            @Override
            public ArgumentBuilder<CommandSourceStack, ?> buildNode(WorldCommand cmd) {
                // 颜色依然用 String 接收，但要求是 16 进制，我们在指令侧提前进行转换拦截
                return Commands.argument("hex_color", StringArgumentType.word())
                        .executes(context -> {
                            String hex = StringArgumentType.getString(context, "hex_color");
                            try {
                                int color = Integer.parseInt(hex.replace("#", ""), 16);
                                return cmd.defaultPlayerRunCommand(context, player -> {
                                    MapLibServerUtil.setSkyColor(player, color);
                                    context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.world.set.sky", hex), false);
                                    return 1;
                                });
                            } catch (NumberFormatException e) {
                                context.getSource().sendFailure(Component.translatable("command.mapLib.world.set.sky.error"));
                                return 0;
                            }
                        });
            }

            @Override
            public void reset(ServerPlayer player) {
                MapLibServerUtil.resetSkyColor(player);
            }
        };

        // 强制每个环境类型必须提供自己的参数节点解析逻辑
        public abstract ArgumentBuilder<CommandSourceStack, ?> buildNode(WorldCommand cmd);

        // 强制实现专属重置逻辑
        public abstract void reset(ServerPlayer player);
    }
}
