package top.yourzi.curse_of_desert.Events;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Client.entity.render.BitumenRenderer;
import top.yourzi.curse_of_desert.Client.entity.model.BitumenBottleModel;
import top.yourzi.curse_of_desert.Client.entity.render.BitumenBottleRenderer;
import top.yourzi.curse_of_desert.Client.entity.model.MummyModel;
import top.yourzi.curse_of_desert.Client.entity.render.MummyRenderer;
import top.yourzi.curse_of_desert.Client.entity.model.ScarabBeetleModel;
import top.yourzi.curse_of_desert.Client.entity.render.ScarabBeetleRenderer;
import top.yourzi.curse_of_desert.init.ModEntities;

@Mod.EventBusSubscriber(modid = Curseofdesert.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusClientEvents {


    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MummyModel.LAYER_LOCATION, MummyModel::createBodyLayer);
        event.registerLayerDefinition(BitumenBottleModel.LAYER_LOCATION, BitumenBottleModel::createBodyLayer);
        event.registerLayerDefinition(ScarabBeetleModel.LAYER_LOCATION, ScarabBeetleModel::createBodyLayer);
    }


    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MUMMY.get(), MummyRenderer::new);
        event.registerEntityRenderer(ModEntities.BITUMEN_BOTTLE.get(), BitumenBottleRenderer::new);
        event.registerEntityRenderer(ModEntities.SCARAB_BEETLE.get(), ScarabBeetleRenderer::new);
        event.registerEntityRenderer(ModEntities.BITUMEN_MUMMY.get(), MummyRenderer::new);
        event.registerEntityRenderer(ModEntities.BITUMEN.get(), BitumenRenderer::new);
    }
}
