package top.yourzi.curse_of_desert.init;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import top.yourzi.curse_of_desert.Curseofdesert;

import static net.minecraftforge.registries.ForgeRegistries.SOUND_EVENTS;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(SOUND_EVENTS, Curseofdesert.MOD_ID);
    public static final RegistryObject<SoundEvent> MUMMY_AMBIENT =
            registerSoundEvents("mummy_ambient");
    public static final RegistryObject<SoundEvent> MUMMY_DEATH =
            registerSoundEvents("mummy_death");
    public static final RegistryObject<SoundEvent> MUMMY_HURT =
            registerSoundEvents("mummy_hurt");
    public static final RegistryObject<SoundEvent> MUMMY_STEP =
            registerSoundEvents("mummy_step");


    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Curseofdesert.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
