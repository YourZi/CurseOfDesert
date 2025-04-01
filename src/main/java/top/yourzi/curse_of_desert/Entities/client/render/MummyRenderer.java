package top.yourzi.curse_of_desert.Entities.client.render;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Entities.Mummy.Mummy;
import top.yourzi.curse_of_desert.Entities.client.model.MummyModel;
import top.yourzi.curse_of_desert.Entities.client.layer.*;

@OnlyIn(Dist.CLIENT)

public class MummyRenderer extends MobRenderer<Mummy, MummyModel<Mummy>> {



    public MummyRenderer(EntityRendererProvider.Context context) {
        super(context, new MummyModel(context.bakeLayer(MummyModel.LAYER_LOCATION)), 0.5f);
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public @NotNull RenderType renderType() {
                return RenderType.eyes(new ResourceLocation(Curseofdesert.MOD_ID,"textures/entity/mummy_overlay.png"));
            }
        });
        this.addLayer(new MummyItemLayer(this));
        /*this.addLayer(new MummyArmorLayer(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()
        ));*/ //盔甲渲染层暂时不启用，不会调
    }


    @Override
    public ResourceLocation getTextureLocation(Mummy entity) {
        return new ResourceLocation(Curseofdesert.MOD_ID,"textures/entity/mummy.png");
    }
}