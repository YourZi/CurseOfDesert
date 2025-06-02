package top.yourzi.curse_of_desert.Events;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.yourzi.curse_of_desert.Commands.CurseOfDesertCommand;
import top.yourzi.curse_of_desert.Curseofdesert;

/**
 * 指令注册事件处理器
 * 负责注册模组的所有指令
 */
@Mod.EventBusSubscriber(modid = Curseofdesert.MOD_ID)
public class CommandRegistrationHandler {
    
    /**
     * 注册指令事件
     * @param event 注册指令事件
     */
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CurseOfDesertCommand.register(event.getDispatcher());
    }
}