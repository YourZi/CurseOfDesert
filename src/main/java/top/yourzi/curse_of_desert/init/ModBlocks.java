package top.yourzi.curse_of_desert.init;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.yourzi.curse_of_desert.Curseofdesert;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Curseofdesert.MOD_ID);

    // 在这里添加方块注册

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}