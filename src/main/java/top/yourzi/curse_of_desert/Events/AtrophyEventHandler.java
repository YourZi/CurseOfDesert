package top.yourzi.curse_of_desert.Events;

import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Effects.Atrophy;
import top.yourzi.curse_of_desert.init.ModEffect;

@Mod.EventBusSubscriber(modid = Curseofdesert.MOD_ID)
public class AtrophyEventHandler {
    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().hasEffect(ModEffect.ATROPHY.get())) {
            ((Atrophy) ModEffect.ATROPHY.get()).onHurt(event.getEntity(), event.getAmount());
            if (event.getEntity().getHealth() > 1.0F)
            {
                event.setAmount(0);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().hasEffect(ModEffect.ATROPHY.get())) {
            ((Atrophy) ModEffect.ATROPHY.get()).removeAttributeModifiers(event.getEntity(), event.getEntity().getAttributes(), 0);
            event.getEntity().getPersistentData().putDouble("AtrophyReduction", 0);
        }
    }
}