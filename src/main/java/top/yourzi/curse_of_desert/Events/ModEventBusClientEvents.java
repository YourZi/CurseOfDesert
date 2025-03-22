package top.yourzi.curse_of_desert.Events;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Entities.BitumenBottle.BitumenBottleModel;
import top.yourzi.curse_of_desert.Entities.BitumenBottle.BitumenBottleRenderer;
import top.yourzi.curse_of_desert.Entities.Mummy.MummyModel;
import top.yourzi.curse_of_desert.Entities.Mummy.MummyRenderer;
import top.yourzi.curse_of_desert.init.ModEntities;

@Mod.EventBusSubscriber(modid = Curseofdesert.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusClientEvents {


    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MummyModel.LAYER_LOCATION, MummyModel::createBodyLayer);
        event.registerLayerDefinition(BitumenBottleModel.LAYER_LOCATION, BitumenBottleModel::createBodyLayer);
    }


    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MUMMY.get(), MummyRenderer::new);
        event.registerEntityRenderer(ModEntities.BITUMEN_BOTTLE.get(), BitumenBottleRenderer::new);
    }


}
