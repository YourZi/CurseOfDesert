package top.yourzi.curse_of_desert;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import org.slf4j.Logger;
import top.yourzi.curse_of_desert.init.ModCreativeTab;
import top.yourzi.curse_of_desert.init.ModEntities;
import top.yourzi.curse_of_desert.init.ModItems;
import top.yourzi.curse_of_desert.init.ModSounds;


@Mod(Curseofdesert.MOD_ID)
public class Curseofdesert {

    public static final String MOD_ID = "curse_of_desert";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);


    public Curseofdesert() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        CREATIVE_MODE_TABS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);

        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        ModCreativeTab.register(modEventBus);
        ModItems.register(modEventBus);

    }


}
