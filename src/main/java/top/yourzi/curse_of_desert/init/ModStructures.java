package top.yourzi.curse_of_desert.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import top.yourzi.curse_of_desert.Curseofdesert;

public class ModStructures {
        //好像没什么必要，算了放着吧
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPE_REG = 
            DeferredRegister.create(Registries.STRUCTURE_TYPE, Curseofdesert.MOD_ID);
    
    public static final ResourceKey<Structure> ANCIENT_PYRAMID = ResourceKey.create(
            Registries.STRUCTURE,
            new ResourceLocation(Curseofdesert.MOD_ID, "ancient_pyramid"));
    
    public static void register(IEventBus eventBus) {
        STRUCTURE_TYPE_REG.register(eventBus);
    }
}
