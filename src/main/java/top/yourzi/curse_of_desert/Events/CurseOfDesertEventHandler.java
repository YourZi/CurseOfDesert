package top.yourzi.curse_of_desert.Events;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.AttackEvent.CurseOfDesertEvent;

@Mod.EventBusSubscriber(modid = Curseofdesert.MOD_ID)
public class CurseOfDesertEventHandler {
    private static CurseOfDesertEvent currentEvent;

    public static CurseOfDesertEvent getCurrentEvent() {
        return currentEvent;
    }

    public static void setCurrentEvent(CurseOfDesertEvent event) {
        currentEvent = event;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && currentEvent != null) {
            if (currentEvent.isActive()) {
                currentEvent.tick();
            } else {
                currentEvent = null;
            }
        }
    }
}