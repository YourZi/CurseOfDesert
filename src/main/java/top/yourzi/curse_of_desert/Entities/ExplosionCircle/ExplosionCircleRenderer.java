package top.yourzi.curse_of_desert.Entities.ExplosionCircle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import top.yourzi.curse_of_desert.Curseofdesert;

public class ExplosionCircleRenderer extends EntityRenderer<ExplosionCircle> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Curseofdesert.MOD_ID, "textures/entity/explosion_circle.png");

    public ExplosionCircleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ExplosionCircle entity) {
        return TEXTURE;
    }

    @Override
    public void render(ExplosionCircle entity, float entityYaw, float partialTicks, PoseStack poseStack, 
                      MultiBufferSource buffer, int packedLight) {
        // 这个实体主要通过粒子效果显示，所以这里不需要渲染模型
        // 粒子效果在实体的tick方法中生成
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}