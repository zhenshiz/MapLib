package com.map_lib.command;

import com.lowdragmc.lowdraglib2.registry.annotation.LDLRegister;
import com.map_lib.util.MapLibServerUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Set;

@LDLRegister(name = HudCommand.ID, registry = ICommand.COMMAND_ID)
public class HudCommand implements ICommand {
    public static final String ID = "hud";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal(this.startCommandName(ID)).requires(source -> source.hasPermission(2))
                .then(Commands.argument("layer", StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                Arrays.stream(HudCommand.HudType.values()).map(Enum::name).map(String::toLowerCase), builder
                        ))
                        .then(Commands.argument("isHidden", BoolArgumentType.bool())
                                .executes(context -> this.defaultPlayerRunCommand(context, player -> {
                                    String layer = StringArgumentType.getString(context, "layer");
                                    HudType hudType = HudType.fromString(layer);
                                    boolean isHidden = BoolArgumentType.getBool(context, "isHidden");

                                    MapLibServerUtil.setHudHidden(player, hudType, isHidden);
                                    context.getSource().sendSuccess(() -> Component.translatable("command.mapLib.hud.success", layer, isHidden ? Component.translatable("command.mapLib.hidden") : Component.translatable("command.mapLib.show")), true);
                                    return 1;
                                }))
                        )
                )
        );
    }

    public enum HudType {
        ALL,                // 隐藏所有 (相当于 F1)
        HOTBAR,             // 快捷栏
        CROSSHAIR,          // 准星
        HEALTH,             // 生命值 (红心)
        ARMOR,              // 护甲值
        FOOD,               // 饥饿值 (鸡腿)
        AIR,                // 氧气值 (气泡)
        EXPERIENCE,         // 经验条 + 经验等级数字
        CHAT,               // 聊天框
        TAB_LIST,           // 玩家列表 (Tab键)
        EFFECTS,            // 右上角药水状态图标
        BOSS_BAR,           // 顶部 Boss 血条
        SCOREBOARD,         // 右侧计分板
        ITEM_NAME;          // 切换物品时底部浮现的物品名

        public static HudType fromString(String name) {
            try {
                return valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        public boolean isHidden(Set<HudType> hiddenElements) {
            return hiddenElements.contains(this) || hiddenElements.contains(ALL);
        }
    }
}
