package top.yourzi.curse_of_desert.Entities.client.render;

import org.antlr.v4.runtime.misc.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Entities.GiantScorpion.GiantScorpion;
import top.yourzi.curse_of_desert.Entities.client.layer.GiantScorpionItemLayer;
import top.yourzi.curse_of_desert.Entities.client.model.GiantScorpionModel;


public class GiantScorpionRenderer extends MobRenderer<GiantScorpion, GiantScorpionModel<GiantScorpion>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Curseofdesert.MOD_ID, "textures/entity/giant_scorpion.png");

    public GiantScorpionRenderer(EntityRendererProvider.Context context) {
        super(context, new GiantScorpionModel<>(context.bakeLayer(GiantScorpionModel.LAYER_LOCATION)), 0.8f);
        /*
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public @NotNull RenderType renderType() {
                return RenderType.eyes(new ResourceLocation(Curseofdesert.MOD_ID,"textures/entity/giant_scorpion_overlay.png"));
            }
        }); */
        this.addLayer(new GiantScorpionItemLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(GiantScorpion entity) {
        return TEXTURE;
    }

    @Override
    public void render(GiantScorpion entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.scale(1.2f, 1.2f, 1.2f);
        
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
}