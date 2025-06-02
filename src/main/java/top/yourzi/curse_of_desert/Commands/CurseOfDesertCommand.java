package top.yourzi.curse_of_desert.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import top.yourzi.curse_of_desert.AttackEvent.CurseOfDesertEvent;
import top.yourzi.curse_of_desert.Events.CurseOfDesertEventHandler;

/**
 * 沙漠诅咒事件指令类
 * 提供启动和停止沙漠诅咒事件的指令功能
 */
public class CurseOfDesertCommand {
    
    /**
     * 注册指令
     * @param dispatcher 指令分发器
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("curseofdesert")
                .requires(source -> source.hasPermission(2))
                .then(
                    Commands.literal("start")
                        .executes(CurseOfDesertCommand::startEventAtPlayerLocation)
                        .then(
                            Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(CurseOfDesertCommand::startEventAtPosition)
                        )
                )
                .then(
                    Commands.literal("stop")
                        .executes(CurseOfDesertCommand::stopEvent)
                )
                .then(
                    Commands.literal("status")
                        .executes(CurseOfDesertCommand::getEventStatus)
                )
        );
    }
    
    /**
     * 在玩家当前位置启动沙漠诅咒事件
     * @param context 指令上下文
     * @return 指令执行结果
     * @throws CommandSyntaxException 指令语法异常
     */
    private static int startEventAtPlayerLocation(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = source.getLevel();
        BlockPos playerPos = player.blockPosition();
        
        return startEvent(source, level, playerPos);
    }
    
    /**
     * 在指定位置启动沙漠诅咒事件
     * @param context 指令上下文
     * @return 指令执行结果
     * @throws CommandSyntaxException 指令语法异常
     */
    private static int startEventAtPosition(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPosArgument.getBlockPos(context, "pos");
        
        return startEvent(source, level, pos);
    }
    
    /**
     * 启动沙漠诅咒事件的核心逻辑
     * @param source 指令源
     * @param level 服务器世界
     * @param pos 事件位置
     * @return 指令执行结果
     */
    private static int startEvent(CommandSourceStack source, ServerLevel level, BlockPos pos) {
        CurseOfDesertEvent currentEvent = CurseOfDesertEventHandler.getCurrentEvent();
        if (currentEvent != null && currentEvent.isActive()) {
            source.sendFailure(Component.translatable("command.curse_of_desert.start.already_active", 
                currentEvent.getCenter().getX(), 
                currentEvent.getCenter().getY(), 
                currentEvent.getCenter().getZ()));
            return 0;
        }
        
        CurseOfDesertEvent newEvent = new CurseOfDesertEvent(level, pos);
        CurseOfDesertEventHandler.setCurrentEvent(newEvent);
        
        source.sendSuccess(() -> Component.translatable("command.curse_of_desert.start.success", 
            pos.getX(), pos.getY(), pos.getZ()), true);
        
        return 1;
    }
    
    /**
     * 停止当前的沙漠诅咒事件
     * @param context 指令上下文
     * @return 指令执行结果
     */
    private static int stopEvent(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        CurseOfDesertEvent currentEvent = CurseOfDesertEventHandler.getCurrentEvent();
        
        if (currentEvent == null || !currentEvent.isActive()) {
            source.sendFailure(Component.translatable("command.curse_of_desert.stop.no_active_event"));
            return 0;
        }
        
        currentEvent.finish(false);
        CurseOfDesertEventHandler.setCurrentEvent(null);
        
        source.sendSuccess(() -> Component.translatable("command.curse_of_desert.stop.success"), true);
        
        return 1;
    }
    
    /**
     * 获取当前事件状态
     * @param context 指令上下文
     * @return 指令执行结果
     */
    private static int getEventStatus(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        CurseOfDesertEvent currentEvent = CurseOfDesertEventHandler.getCurrentEvent();
        
        if (currentEvent == null || !currentEvent.isActive()) {
            source.sendSuccess(() -> Component.translatable("command.curse_of_desert.status.no_active_event"), false);
        } else {
            BlockPos center = currentEvent.getCenter();
            int currentWave = currentEvent.getCurrentWave();
            int totalWaves = currentEvent.getTotalWaves();
            
            source.sendSuccess(() -> Component.translatable("command.curse_of_desert.status.active", 
                center.getX(), center.getY(), center.getZ(), currentWave, totalWaves), false);
        }
        
        return 1;
    }
}