package top.yourzi.curse_of_desert.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import top.yourzi.curse_of_desert.AttackEvent.CurseOfDesertEvent;

public class CurseOfDesertCommand {
    private static CurseOfDesertEvent currentEvent = null;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("curseofdesert")
                .requires(source -> source.hasPermission(2)) // 需要权限等级2
                .then(Commands.literal("start")
                    .executes(context -> startCurseAtPlayer(context))
                    .then(Commands.argument("x", IntegerArgumentType.integer())
                        .then(Commands.argument("y", IntegerArgumentType.integer())
                            .then(Commands.argument("z", IntegerArgumentType.integer())
                                .executes(context -> startCurseAtPosition(context,
                                    IntegerArgumentType.getInteger(context, "x"),
                                    IntegerArgumentType.getInteger(context, "y"),
                                    IntegerArgumentType.getInteger(context, "z")))))))
                .then(Commands.literal("stop")
                    .executes(CurseOfDesertCommand::stopCurse)));
    }

    private static int startCurseAtPlayer(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("commands.curseofdesert.start.error.not_player"));
            return 0;
        }

        if (currentEvent != null && currentEvent.isActive()) {
            source.sendFailure(Component.translatable("commands.curseofdesert.start.error.already_active"));
            return 0;
        }

        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition();

        currentEvent = new CurseOfDesertEvent(level, pos);
        top.yourzi.curse_of_desert.Events.CurseOfDesertEventHandler.setCurrentEvent(currentEvent);
        source.sendSuccess(() -> 
            Component.translatable("commands.curseofdesert.start.success"), true);
        return 1;
    }

    private static int startCurseAtPosition(CommandContext<CommandSourceStack> context, int x, int y, int z) {
        CommandSourceStack source = context.getSource();
        
        if (currentEvent != null && currentEvent.isActive()) {
            source.sendFailure(Component.translatable("commands.curseofdesert.start.error.already_active"));
            return 0;
        }

        ServerLevel level;
        ServerPlayer player = null;
        if (source.getEntity() instanceof ServerPlayer) {
            player = (ServerPlayer) source.getEntity();
            level = player.serverLevel();
        } else {
            level = source.getLevel() instanceof ServerLevel ? (ServerLevel) source.getLevel() : null;
            if (level == null) {
                source.sendFailure(Component.translatable("commands.curseofdesert.start.error.invalid_level"));
                return 0;
            }
        }

        BlockPos pos = new BlockPos(x, y, z);

        currentEvent = new CurseOfDesertEvent(level, pos);
        top.yourzi.curse_of_desert.Events.CurseOfDesertEventHandler.setCurrentEvent(currentEvent);
        source.sendSuccess(() -> 
            Component.translatable("commands.curseofdesert.start.success"), true);
        return 1;
    }

    private static int stopCurse(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        // 获取当前活跃的事件实例
        CurseOfDesertEvent activeEvent = top.yourzi.curse_of_desert.Events.CurseOfDesertEventHandler.getCurrentEvent();
        
        if (activeEvent == null || !activeEvent.isActive()) {
            source.sendFailure(Component.translatable("commands.curseofdesert.stop.error.not_active"));
            return 0;
        }

        // 检查执行命令的实体是否为玩家
        if (source.getEntity() instanceof ServerPlayer player) {
            // 获取玩家位置
            BlockPos playerPos = player.blockPosition();
            // 获取事件中心位置
            BlockPos eventCenter = activeEvent.getCenter();
            // 检查玩家是否在事件范围内（50格范围）
            if (playerPos.distSqr(eventCenter) <= 2500) { // 50 * 50 = 2500
                activeEvent.finish(false);
                top.yourzi.curse_of_desert.Events.CurseOfDesertEventHandler.setCurrentEvent(null);
                source.sendSuccess(() -> 
                    Component.translatable("commands.curseofdesert.stop.success"), true);
                return 1;
            } else {
                source.sendFailure(Component.translatable("commands.curseofdesert.stop.error.not_active"));
                return 0;
            }
        } else {
            // 如果不是玩家执行的命令，直接停止事件
            activeEvent.finish(false);
            top.yourzi.curse_of_desert.Events.CurseOfDesertEventHandler.setCurrentEvent(null);
            source.sendSuccess(() -> 
                Component.translatable("commands.curseofdesert.stop.success"), true);
            return 1;
        }
    }
}