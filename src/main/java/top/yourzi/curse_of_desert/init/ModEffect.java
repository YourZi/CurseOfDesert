package top.yourzi.curse_of_desert.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Effects.*;;

public class ModEffect {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Curseofdesert.MOD_ID);

    public static final RegistryObject<MobEffect> ATROPHY = EFFECTS.register("atrophy",
            () -> new Atrophy());

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
