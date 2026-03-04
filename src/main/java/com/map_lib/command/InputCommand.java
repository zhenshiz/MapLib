package com.map_lib.command;

import com.lowdragmc.lowdraglib2.registry.annotation.LDLRegister;
import com.map_lib.util.MapLibServerUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Set;

@LDLRegister(name = InputCommand.ID, registry = ICommand.COMMAND_ID)
public class InputCommand implements ICommand {
    public static final String ID = "input";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        var root = Commands.literal(this.startCommandName(ID)).requires(source -> source.hasPermission(2));

        root.then(Commands.literal("lock")
                .then(Commands.argument("permission", StringArgumentType.word())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                Arrays.stream(InputPermission.values()).map(Enum::name).map(String::toLowerCase), builder
                        ))
                        .then(Commands.argument("lock", BoolArgumentType.bool())
                                .executes(context -> setInputLock(
                                        context,
                                        StringArgumentType.getString(context, "permission"),
                                        BoolArgumentType.getBool(context, "lock")
                                ))
                        )
                )
        );

        root.then(Commands.literal("simulate")
                .then(Commands.argument("action", StringArgumentType.word())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                Arrays.stream(InputAction.values()).map(Enum::name).map(String::toLowerCase), builder
                        ))
                        .then(Commands.argument("ticks", IntegerArgumentType.integer(1))
                                .executes(context -> simulateInput(
                                        context,
                                        StringArgumentType.getString(context, "action"),
                                        IntegerArgumentType.getInteger(context, "ticks")
                                ))
                        )
                )
        );

        dispatcher.register(root);
    }

    private int setInputLock(CommandContext<CommandSourceStack> context, String permissionName, boolean isLocked) {
        return this.defaultPlayerRunCommand(context, player -> {
            InputPermission perm = InputPermission.fromString(permissionName);

            if (perm == null) {
                context.getSource().sendFailure(Component.translatable("command.mapLib.input.permission.notFound", permissionName));
                return 0;
            }

            MapLibServerUtil.setInputLock(player, perm, isLocked);
            context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.input.permission.success",
                            perm.name(), isLocked ? Component.translatable("command.mapLib.lock") : Component.translatable("command.mapLib.unlock")),
                    true);
            return 1;
        });
    }

    private int simulateInput(CommandContext<CommandSourceStack> context, String actionName, int ticks) {
        return this.defaultPlayerRunCommand(context, player -> {
            InputAction action = InputAction.fromString(actionName);

            if (action == null) {
                context.getSource().sendFailure(Component.translatable("command.mapLib.input.action.notFound", actionName));
                return 0;
            }

            MapLibServerUtil.simulateInput(player, action, ticks);

            context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.input.action.success",
                            action.name(), ticks)
                    , true);
            return 1;
        });
    }

    public enum InputPermission {
        ALL(null),

        // 移动类
        MOVEMENT(ALL),
        LATERAL_MOVE(MOVEMENT),
        UP(LATERAL_MOVE),
        DOWN(LATERAL_MOVE),
        LEFT(LATERAL_MOVE),
        RIGHT(LATERAL_MOVE),
        JUMPING(MOVEMENT),
        SHIFT_KEY_DOWN(MOVEMENT),
        SPRINTING(MOVEMENT),

        // 旋转类 (视角转动)
        ROTATION(ALL),
        ROTATION_HORIZONTAL(ROTATION),
        ROTATION_VERTICAL(ROTATION),

        // 鼠标类
        MOUSE(ALL),
        MOUSE_ATTACK(MOUSE),
        MOUSE_USE(MOUSE),
        MOUSE_PICK_ITEM(MOUSE),

        // 杂项 / UI / 交互
        PERSPECTIVE(ALL),           // F5 切换视角
        SMOOTH_CAMERA(ALL),         // 电影视角 (平滑相机)
        HOTBAR_KEYS(ALL),           // 快捷栏按键 1-9
        SOCIAL_INTERACTION(ALL),    // 社交互动界面
        INVENTORY(ALL),             // 打开背包 (E)
        ADVANCEMENT(ALL),           // 进度界面 (L)
        SWAP_HAND(ALL),             // 交换副手 (F)
        DROP_ITEM(ALL),             // 丢弃物品 (Q)
        CHAT(ALL);                  // 打开聊天 (T / Enter)

        private final InputPermission parent;

        InputPermission(InputPermission parent) {
            this.parent = parent;
        }

        /**
         * 核心递归检查：只要自己或任何一个父级/爷级被锁，就返回 true
         */
        public boolean isLocked(Set<InputPermission> activeLocks) {
            if (activeLocks.contains(this)) {
                return true;
            }
            if (this.parent != null) {
                return this.parent.isLocked(activeLocks);
            }
            return false;
        }

        /**
         * 辅助方法：通过字符串安全地获取枚举（忽略大小写）
         */
        public static InputPermission fromString(String name) {
            try {
                return valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public enum InputAction {
        // 移动
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT,
        JUMP,
        SNEAK,
        SPRINTING,     //疾跑

        // 交互
        ATTACK,      // 左键
        USE,         // 右键
        DROP;        // 丢弃物品 (Q)

        public static InputAction fromString(String name) {
            try {
                return valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
