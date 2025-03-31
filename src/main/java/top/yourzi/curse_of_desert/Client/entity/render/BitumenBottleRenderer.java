package top.yourzi.curse_of_desert.Client.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Entities.BitumenBottle.BitumenBottle;
import top.yourzi.curse_of_desert.Client.entity.model.BitumenBottleModel;

@OnlyIn(Dist.CLIENT)
public class BitumenBottleRenderer extends EntityRenderer<BitumenBottle> {
    public static final ResourceLocation TEXTURE =
            new ResourceLocation(Curseofdesert.MOD_ID, "textures/entity/mummy.png");
    
    private final BitumenBottleModel model;

    public BitumenBottleRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new BitumenBottleModel(context.bakeLayer(BitumenBottleModel.LAYER_LOCATION));
    }

    @Override
    public void render(BitumenBottle entity, float entityYaw, float partialTicks,
                      PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, -0.85D, 0.0D);

        this.model.setupAnim(entity, 0.0F, 0.0F, partialTicks, 0.0F, 0.0F);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BitumenBottle entity) {
        return TEXTURE;
    }
}