package top.yourzi.curse_of_desert.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.yourzi.curse_of_desert.Curseofdesert;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Curseofdesert.MOD_ID);

    // 注册治疗粒子效果
    public static final RegistryObject<SimpleParticleType> HEALING_PARTICLE =
            PARTICLE_TYPES.register("healing_particle",
                    () -> new SimpleParticleType(false)); // false表示粒子不受重力影响

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}