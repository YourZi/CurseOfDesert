package top.yourzi.curse_of_desert.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Entities.Mummy.Mummy;
import top.yourzi.curse_of_desert.Entities.BitumenBottle.BitumenBottle;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Curseofdesert.MOD_ID);

    public static final RegistryObject<EntityType<Mummy>> MUMMY =
            ENTITY_TYPES.register("mummy",
            () -> EntityType.Builder.of(Mummy::new, MobCategory.MONSTER)
                    .sized(0.6f,1.8f)
                    .build("mummy"));

    public static final RegistryObject<EntityType<BitumenBottle>> BITUMEN_BOTTLE =
            ENTITY_TYPES.register("bitumen_bottle",
            () -> EntityType.Builder.<BitumenBottle>of((type, level) -> new BitumenBottle(type, level), MobCategory.MISC)
                    .sized(0.6F, 0.6F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("bitumen_bottle"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
