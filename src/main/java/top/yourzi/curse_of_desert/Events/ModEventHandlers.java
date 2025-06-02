package top.yourzi.curse_of_desert.Events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.init.ModEffect;

@Mod.EventBusSubscriber(modid = Curseofdesert.MOD_ID)
public class ModEventHandlers {

    /**
     * 监听实体治疗事件，如果实体具有萎缩效果，则取消治疗。
     */
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide && entity.hasEffect(ModEffect.ATROPHY.get())) {
            event.setCanceled(true);
        }
    }
}