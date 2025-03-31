package top.yourzi.curse_of_desert.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import top.yourzi.curse_of_desert.Curseofdesert;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Curseofdesert.MOD_ID);

    public static final RegistryObject<CreativeModeTab> BASE_TAB = CREATIVE_MODE_TABS.register("base",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SAND_EYE.get()))
                    .title(Component.translatable("curse_of_desert.creativetab.base"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.SAND_EYE.get());
                        pOutput.accept(ModItems.BITUMEN_BOTTLE_ITEM.get());
                        pOutput.accept(ModBlocks.BITUMEN_BOTTLE.get());
                        pOutput.accept(ModItems.MUMMY_EGG.get());
                        pOutput.accept(ModItems.BITUMEN_MUMMY_EGG.get());
                        pOutput.accept(ModItems.SCARAB_BEETLE_EGG.get());
                    })
                    .build());
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
