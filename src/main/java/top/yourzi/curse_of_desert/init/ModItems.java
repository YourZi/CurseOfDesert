package top.yourzi.curse_of_desert.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Items.BitumenBottleItem;
import top.yourzi.curse_of_desert.Items.BronzeHandAxeItem;
import top.yourzi.curse_of_desert.Items.SandEyeItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Curseofdesert.MOD_ID);

    public static final RegistryObject<Item> BRONZE_HAND_AXE = ITEMS.register("bronze_hand_axe",
        () -> new BronzeHandAxeItem(new Item.Properties()));

    public static final RegistryObject<Item> BITUMEN_BOTTLE_ITEM = ITEMS.register("bitumen_bottle_item",
            () -> new BitumenBottleItem());

    public static final RegistryObject<Item> SAND_EYE = ITEMS.register("sand_eye",
            () -> new SandEyeItem(new Item.Properties().stacksTo(16).rarity(Rarity.RARE)));


    public static final RegistryObject<Item> MUMMY_EGG = ITEMS.register("mummy_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MUMMY, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

    public static final RegistryObject<Item> SCARAB_BEETLE_EGG = ITEMS.register("scarab_beetle_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SCARAB_BEETLE, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

    public static final RegistryObject<Item> BITUMEN_MUMMY_EGG = ITEMS.register("bitumen_mummy_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.BITUMEN_MUMMY, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

    public static final RegistryObject<Item> DOG_HEADED_WARRIOR_EGG = ITEMS.register("dog_headed_warrior_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.DOG_HEADED_WARRIOR, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

    public static final RegistryObject<Item> GIANT_SCORPION_EGG = ITEMS.register("giant_scorpion_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GIANT_SCORPION, 0x8B4513, 0x000000, new Item.Properties()));

    public static final RegistryObject<Item> DOG_HEADED_PRIESTS_EGG = ITEMS.register("dog_headed_priests_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.DOG_HEADED_PRIESTS, 0x8B4513, 0xFFD700, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}