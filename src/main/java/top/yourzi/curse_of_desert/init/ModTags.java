package top.yourzi.curse_of_desert.init;

import static net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import top.yourzi.curse_of_desert.Curseofdesert;

public class ModTags {
    public static final DeferredRegister<EntityType<?>> TAGS =
            DeferredRegister.create(ENTITY_TYPES, Curseofdesert.MOD_ID);

    public static final TagKey<EntityType<?>> CURSE_OF_DESERT = TagKey.create(Registries.ENTITY_TYPE,
            new ResourceLocation(Curseofdesert.MOD_ID, "curse_of_desert"));

    public static void register(IEventBus eventBus) {
        TAGS.register(eventBus);
    }
}