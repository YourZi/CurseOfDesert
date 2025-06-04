package top.yourzi.curse_of_desert.Entities.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Entities.ExplosionCircle.ExplosionCircle;

public class ExplosionCircleRenderer extends EntityRenderer<ExplosionCircle> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Curseofdesert.MOD_ID, "textures/entity/explode_circle.png");
    private static final float BILLBOARD_SIZE = 4.0F; // 平面大小
    private static final int TEXTURE_HEIGHT = 64; // 纹理总高度
    private static final int FRAME_HEIGHT = 32; // 单帧高度
    private static final int FRAMES = 2; // 总帧数

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
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        

        // 计算当前生命周期进度
        int maxLifeTime = entity.getMaxLifeTime();
        int timer = entity.getExplosionTimer();
        float lifeProgress = 1.0F - (float)timer / maxLifeTime;
        
        int firstFrame = 0;
        float firstFrameAlpha = 1.0F - lifeProgress;
        
        int secondFrame = 1;
        float secondFrameAlpha = lifeProgress;
        
        // 渲染平面纹理
        // 先渲染第一帧（底层）
        renderBillboardFrame(entity, poseStack, buffer, packedLight, firstFrame, firstFrameAlpha);
        
        // 再渲染第二帧（上层），透明度随生命周期变化
        renderBillboardFrame(entity, poseStack, buffer, packedLight, secondFrame, secondFrameAlpha);
    }
    
    /**
     * 渲染平铺在地面上的单一帧平面纹理
     */
    private void renderBillboardFrame(ExplosionCircle entity, PoseStack poseStack, MultiBufferSource buffer, 
                               int packedLight, int frame, float alpha) {
        poseStack.pushPose();
        
        poseStack.translate(0.0, 0.05, 0.0);
        
        // 缩放到合适的大小
        poseStack.scale(BILLBOARD_SIZE, BILLBOARD_SIZE, BILLBOARD_SIZE);
        
        // 获取变换矩阵
        Matrix4f matrix = poseStack.last().pose();
        
        // 获取顶点缓冲区
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        
        // 计算UV坐标
        float minU = 0.0F;
        float maxU = 1.0F;
        
        // 当前帧的UV坐标
        float minV = (float) frame / FRAMES;
        float maxV = minV + (1.0F / FRAMES);
        
        // 渲染平面 (平铺在地面上的四边形)
        float halfSize = 0.5F;
        
        // 左下角
        vertexConsumer.vertex(matrix, -halfSize, 0.0F, -halfSize)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(0, 1, 0)
                .endVertex();
        
        // 右下角
        vertexConsumer.vertex(matrix, halfSize, 0.0F, -halfSize)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(0, 1, 0)
                .endVertex();
        
        // 右上角
        vertexConsumer.vertex(matrix, halfSize, 0.0F, halfSize)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(0, 1, 0)
                .endVertex();
        
        // 左上角
        vertexConsumer.vertex(matrix, -halfSize, 0.0F, halfSize)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(0, 1, 0)
                .endVertex();
        
        poseStack.popPose();
    }
}