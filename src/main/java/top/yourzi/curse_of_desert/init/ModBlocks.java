package top.yourzi.curse_of_desert.init;

import java.util.function.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Blocks.BitumenBlock;
import top.yourzi.curse_of_desert.Blocks.BitumenBottle;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Curseofdesert.MOD_ID);

    public static final RegistryObject<Block> BITUMEN_BOTTLE = registerBlock("bitumen_bottle",
            () -> new BitumenBottle(BlockBehaviour.Properties.of()));

    public static final RegistryObject<Block> BITUMEN_BLOCK = registerBlock("bitumen_block",
            () -> new BitumenBlock(BlockBehaviour.Properties.of()));


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
            RegistryObject<T> toReturn = BLOCKS.register(name, block);
            registerBlockItem(name, toReturn);
            return toReturn;
    }
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}