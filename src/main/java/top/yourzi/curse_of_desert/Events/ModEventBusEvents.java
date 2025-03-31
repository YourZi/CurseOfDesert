package top.yourzi.curse_of_desert.Events;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Entities.Mummy.Mummy;
import top.yourzi.curse_of_desert.Entities.ScarabBeetle.ScarabBeetle;
import top.yourzi.curse_of_desert.init.ModEntities;


@Mod.EventBusSubscriber(modid = Curseofdesert.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MUMMY.get(), Mummy.createAttributes().build());
        event.put(ModEntities.SCARAB_BEETLE.get(), ScarabBeetle.createAttributes().build());
        event.put(ModEntities.BITUMEN_MUMMY.get(), Mummy.createAttributes().build());
    }
}
