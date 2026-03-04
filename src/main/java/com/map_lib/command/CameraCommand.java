package com.map_lib.command;

import com.lowdragmc.lowdraglib2.registry.annotation.LDLRegister;
import com.map_lib.util.MapLibServerUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Arrays;

@LDLRegister(name = CameraCommand.ID, registry = ICommand.COMMAND_ID)
public class CameraCommand implements ICommand {
    public static final String ID = "camera";


    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal(this.startCommandName(ID)).requires(source -> source.hasPermission(2))
                .then(Commands.literal("shake")
                        .then(Commands.argument("amplitude", FloatArgumentType.floatArg(0))
                                .then(Commands.argument("tick", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("decay", StringArgumentType.word())
                                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                        Arrays.stream(ShakeDecay.values()).map(Enum::name).map(String::toLowerCase), builder
                                                ))
                                                .executes(this::cameraShake)
                                        )
                                )
                        )
                )
        );
    }

    private int cameraShake(CommandContext<CommandSourceStack> context) {
        return this.defaultPlayerRunCommand(context, player -> {
            float amplitude = FloatArgumentType.getFloat(context, "amplitude");
            int tick = IntegerArgumentType.getInteger(context, "tick");
            String decay = StringArgumentType.getString(context, "decay");
            ShakeDecay shakeDecay = ShakeDecay.valueOf(decay.toUpperCase());
            MapLibServerUtil.cameraShake(player, amplitude, tick, shakeDecay);
            return 1;
        });
    }

    public enum ShakeDecay {
        NONE,       // 不衰减：全程保持最大震动，时间到瞬间停止（适合持续的地震）
        LINEAR,     // 线性衰减：震动幅度随时间匀速变小（最常用）
        EXPONENTIAL // 指数衰减：开始震动很大，然后迅速变小（适合爆炸、重击）
    }
}
