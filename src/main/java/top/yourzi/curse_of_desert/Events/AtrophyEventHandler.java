package top.yourzi.curse_of_desert.Events;

import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Effects.Atrophy;
import top.yourzi.curse_of_desert.init.ModEffect;

@Mod.EventBusSubscriber(modid = Curseofdesert.MOD_ID)
public class AtrophyEventHandler {
    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().hasEffect(ModEffect.ATROPHY.get())) {
            ((Atrophy) ModEffect.ATROPHY.get()).onHurt(event.getEntity(), event.getAmount());
        }
    }
}