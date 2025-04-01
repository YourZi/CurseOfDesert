package top.yourzi.curse_of_desert.Entities.client.render;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Entities.Bitumen.Bitumen;

public class BitumenRenderer extends EntityRenderer<Bitumen> {
    // 纹理路径
    private static final ResourceLocation TEXTURE = new ResourceLocation(Curseofdesert.MOD_ID, "textures/entity/bitumen.png");
    private static final ResourceLocation TEXTURE_90 = new ResourceLocation(Curseofdesert.MOD_ID, "textures/entity/bitumen_90.png");
    private static final ResourceLocation TEXTURE_180 = new ResourceLocation(Curseofdesert.MOD_ID, "textures/entity/bitumen_180.png");
    private static final ResourceLocation TEXTURE_270 = new ResourceLocation(Curseofdesert.MOD_ID, "textures/entity/bitumen_270.png");
    // 透明度相关常量
    private static final float INITIAL_ALPHA = 0.8F;
    private static final float FINAL_ALPHA = 0.0F;
    private static final int FADE_DURATION_TICKS = 20 * 20; // 20秒

    public BitumenRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Bitumen entity) {
        switch (entity.getTextureRotation()) {
            case 0:
                return TEXTURE;
            case 90:
                return TEXTURE_90;
            case 180:
                return TEXTURE_180;
            case 270:
                return TEXTURE_270;
            default:
                return TEXTURE;
        }
    }

    @Override
    public void render(Bitumen entity, float entityYaw, float partialTicks, PoseStack poseStack,
                        MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        
        // 获取实体所在的世界和位置
        Level level = entity.level();
        BlockPos entityPos = entity.blockPosition();
        
        // 计算当前透明度，从0.8渐变到0，持续20秒
        float alpha = calculateAlpha(entity);

        // 检测实体周围的方块
        for (int y = -1; y <= 0; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos blockPos = entityPos.offset(x, y, z);
                    BlockState blockState = level.getBlockState(blockPos);

                    // 检查方块是否有完整形状（不是空气、液体等）
                    if (!blockState.isAir() && blockState.isSolid()) {
                        // 渲染方块的各个面
                        renderBlockFaces(entity, blockPos, poseStack, buffer, packedLight);
                    }
                }
            }
        }
    }

    private float calculateAlpha(Bitumen entity) {
        int lifetimeTicks = entity.getLifetimeTicks();

        // 确保不超过淡出持续时间
        lifetimeTicks = Math.min(lifetimeTicks, FADE_DURATION_TICKS);

        // 计算当前透明度：从INITIAL_ALPHA线性减少到FINAL_ALPHA
        float progress = (float) lifetimeTicks / FADE_DURATION_TICKS;
        return INITIAL_ALPHA - progress * (INITIAL_ALPHA - FINAL_ALPHA);
    }
    
    private void renderBlockFaces(Bitumen entity, BlockPos blockPos, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Level level = entity.level();
        
        // 获取当前透明度
        float alpha = calculateAlpha(entity);
        
        // 获取顶点缓冲区，使用半透明渲染类型
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
        
        // 保存当前矩阵状态
        poseStack.pushPose();
        
        // 计算方块相对于实体的位置（用于UV映射）
        double relX = blockPos.getX() - entity.position().x;
        double relY = blockPos.getY() - entity.position().y;
        double relZ = blockPos.getZ() - entity.position().z;
        
        // 将渲染原点移动到方块位置
        poseStack.translate(blockPos.getX() - entity.getX(), 
                           blockPos.getY() - entity.getY(), 
                           blockPos.getZ() - entity.getZ());
        
        // 获取实体的纹理旋转角度
        int textureRotation = entity.getTextureRotation();
        
        double offsetNZ = relZ - 1;
        double offsetSZ = relZ + 1;
        double offsetEX = relX + 1;
        double offsetWX = relX - 1;
        
        if(blockPos.getY() == entity.blockPosition().getY()){
            offsetNZ = relZ;
            offsetSZ = relZ;
            offsetEX = relX;
            offsetWX = relX;
        }
        
        // 渲染侧面
        if (shouldRenderFace(level, blockPos, Direction.UP, entity)) {
            renderFace(relX, relY, relZ, poseStack, vertexConsumer, Direction.UP, packedLight, alpha);
        }
        if (shouldRenderFace(level, blockPos, Direction.NORTH, entity)) {
            renderFace(relX, relY, offsetNZ, poseStack, vertexConsumer, Direction.NORTH, packedLight, alpha);
        }
        if (shouldRenderFace(level, blockPos, Direction.SOUTH, entity)) {
            renderFace(relX, relY, offsetSZ, poseStack, vertexConsumer, Direction.SOUTH, packedLight, alpha);
        }
        if (shouldRenderFace(level, blockPos, Direction.EAST, entity)) {
            renderFace(offsetEX, relY, relZ, poseStack, vertexConsumer, Direction.EAST, packedLight, alpha);
        }
        if (shouldRenderFace(level, blockPos, Direction.WEST, entity)) {
            renderFace(offsetWX, relY, relZ, poseStack, vertexConsumer, Direction.WEST, packedLight, alpha);
        }
        
        // 恢复矩阵状态
        poseStack.popPose();
    }
    
    /**
     * 检查是否应该渲染指定方向的面
     */
    private boolean shouldRenderFace(Level level, BlockPos blockPos, Direction direction, Bitumen entity) {
        // 获取相邻方块位置
        BlockPos neighborPos = blockPos.relative(direction);
        BlockState neighborState = level.getBlockState(neighborPos);
        
        int relX = blockPos.getX() - entity.blockPosition().getX();
        int relY = blockPos.getY() - entity.blockPosition().getY();
        int relZ = blockPos.getZ() - entity.blockPosition().getZ();
        
        // 判断是否是第二层方块（y=0）
        boolean isSecondLayer = relY == 0;
        // 判断是否是第一层方块（y=-1）
        boolean isFirstLayer = relY == -1;
        
        // 判断是否是最外层方块
        boolean isOuterBlock = Math.abs(relX) == 1 || Math.abs(relZ) == 1;
        // 判断是否是角落方块（第一层的四个角落）
        boolean isCornerBlock = isFirstLayer && Math.abs(relX) == 1 && Math.abs(relZ) == 1;
        
        // 如果相邻方块是实心的，则不需要渲染这个面
        if (!neighborState.isAir() && neighborState.isSolid()) {
            return false;
        }
        
        // 如果是第一层的角落方块，不渲染侧面
        if (isCornerBlock && direction != Direction.UP) {
            return false;
        }
        // 如果是第二层的方块，不渲染上面
        if (isSecondLayer && direction == Direction.UP) {
            return false;
        }
        
        // 检查是否有其他方块遮挡了当前面
        if (isSecondLayer && isBlockObstructed(level, blockPos, direction, relX, relY, relZ)) {
            return false;
        }
        
        // 如果是最外层方块，只渲染对应方向的对面
        
        if (isSecondLayer && isOuterBlock) {
            // 北面的方块只渲染南面
            if (relZ == -1 && relX == 0 && direction != Direction.SOUTH) {
                return false;
            }
            // 南面的方块只渲染北面
            if (relZ == 1 && relX == 0 && direction != Direction.NORTH) {
                return false;
            }
            // 东面的方块只渲染西面
            if (relX == 1 && relZ == 0 && direction != Direction.WEST) {
                return false;
            }
            // 西面的方块只渲染东面
            if (relX == -1 && relZ == 0 && direction != Direction.EAST) {
                return false;
            }
        }
        

            //判定是否是最外层面
            boolean isEdgeBlock = false;
            switch (direction) {
                case NORTH: // 北面
                    isEdgeBlock = relZ == -1;
                    break;
                case SOUTH: // 南面
                    isEdgeBlock = relZ == 1;
                    break;
                case EAST: // 东面
                    isEdgeBlock = relX == 1;
                    break;
                case WEST: // 西面
                    isEdgeBlock = relX == -1;
                    break;
                default:
                    break;
            }

            // 如果是边缘方块且面向外侧，则不渲染
            if (isEdgeBlock) {
                return false;
            }
        return true;
    }

    private boolean isBlockObstructed(Level level, BlockPos blockPos, Direction direction, int relX, int relY, int relZ) {
        // 检查方块是否在角落位置，且相邻的两个方向都有方块
        // 例如：西北角的方块，检查北面和西面是否都有方块
        
        // 检查东北角
        if (relX == 1 && relZ == -1) {
            // 检查北面和东面是否都有方块
            BlockPos northPos = blockPos.relative(Direction.NORTH);
            BlockPos eastPos = blockPos.relative(Direction.EAST);
            BlockState northState = level.getBlockState(northPos);
            BlockState eastState = level.getBlockState(eastPos);
            
            // 如果北面和东面都有实心方块，则该角落被遮挡
            if ((!northState.isAir() && northState.isSolid()) && 
                (!eastState.isAir() && eastState.isSolid())) {
                return true;
            }
        }
        
        // 检查东南角
        if (relX == 1 && relZ == 1) {
            // 检查南面和东面是否都有方块
            BlockPos southPos = blockPos.relative(Direction.SOUTH);
            BlockPos eastPos = blockPos.relative(Direction.EAST);
            BlockState southState = level.getBlockState(southPos);
            BlockState eastState = level.getBlockState(eastPos);
            
            // 如果南面和东面都有实心方块，则该角落被遮挡
            if ((!southState.isAir() && southState.isSolid()) && 
                (!eastState.isAir() && eastState.isSolid())) {
                return true;
            }
        }
        
        // 检查西南角
        if (relX == -1 && relZ == 1) {
            // 检查南面和西面是否都有方块
            BlockPos southPos = blockPos.relative(Direction.SOUTH);
            BlockPos westPos = blockPos.relative(Direction.WEST);
            BlockState southState = level.getBlockState(southPos);
            BlockState westState = level.getBlockState(westPos);
            
            // 如果南面和西面都有实心方块，则该角落被遮挡
            if ((!southState.isAir() && southState.isSolid()) && 
                (!westState.isAir() && westState.isSolid())) {
                return true;
            }
        }
        
        // 检查西北角
        if (relX == -1 && relZ == -1) {
            // 检查北面和西面是否都有方块
            BlockPos northPos = blockPos.relative(Direction.NORTH);
            BlockPos westPos = blockPos.relative(Direction.WEST);
            BlockState northState = level.getBlockState(northPos);
            BlockState westState = level.getBlockState(westPos);
            
            // 如果北面和西面都有实心方块，则该角落被遮挡
            if ((!northState.isAir() && northState.isSolid()) && 
                (!westState.isAir() && westState.isSolid())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 渲染一个方块面
     */
    private void renderFace(double relX, double relY, double relZ, PoseStack poseStack, VertexConsumer vertexConsumer, Direction direction, int packedLight, float alpha) {
        poseStack.pushPose();
        
        //避免Z-fighting
        float offsetX = direction.getStepX() * (0.0001F + 0.0001F * alpha);
        float offsetY = direction.getStepY() * (0.0001F + 0.0001F * alpha);
        float offsetZ = direction.getStepZ() * (0.0001F + 0.0001F * alpha);
        poseStack.translate(offsetX, offsetY, offsetZ);
        // 获取当前变换矩阵
        org.joml.Matrix4f matrix = poseStack.last().pose();
        
        // 根据方向设置顶点坐标
        float minX = 0.0F;
        float minY = 0.0F;
        float minZ = 0.0F;
        float maxX = 1.0F;
        float maxY = 1.0F;
        float maxZ = 1.0F;
        
        // 计算面的中心点到渲染中心的水平距离
        float faceCenterX = (float) (relX + 0.5); // 面的中心X坐标
        float faceCenterZ = (float) (relZ + 0.5); // 面的中心Z坐标
        
        // 单格UV大小
        float uvGridSize = 0.125F;
        
        // 将相对位置映射到UV坐标，确保实体中心(0,0)对应纹理中心(0.5,0.5)
        // 将相对坐标除以一个缩放因子，然后加上0.5使其以0.5为中心
        float normalizedX = 0.5F + (faceCenterX / 8.0F);
        float normalizedZ = 0.5F + (faceCenterZ / 8.0F);

        // 计算UV坐标范围
        float minU = normalizedX - (uvGridSize / 2.0F);
        float maxU = normalizedX + (uvGridSize / 2.0F);
        float minV = normalizedZ - (uvGridSize / 2.0F);
        float maxV = normalizedZ + (uvGridSize / 2.0F);
        
        // 根据方向渲染对应的面
        switch (direction) {
            case UP: // 顶面 (Y+)
                vertexConsumer.vertex(matrix, minX, maxY, minZ).color(1.0F, 1.0F, 1.0F, alpha).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();
                vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(1.0F, 1.0F, 1.0F, alpha).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();
                vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(1.0F, 1.0F, 1.0F, alpha).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();
                vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(1.0F, 1.0F, 1.0F, alpha).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 1, 0).endVertex();
                break;
            case NORTH: // 北面 (Z-)
                vertexConsumer.vertex(matrix, minX, minY, minZ).color(1.0F, 1.0F, 1.0F, alpha).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 0, -1).endVertex();
                vertexConsumer.vertex(matrix, maxX, minY, minZ).color(1.0F, 1.0F, 1.0F, alpha).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 0, -1).endVertex();
                vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(1.0F, 1.0F, 1.0F, alpha).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 0, -1).endVertex();
                vertexConsumer.vertex(matrix, minX, maxY, minZ).color(1.0F, 1.0F, 1.0F, alpha).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 0, -1).endVertex();
                break;
            case SOUTH: // 南面 (Z+)
                vertexConsumer.vertex(matrix, maxX, minY, maxZ).color(1.0F, 1.0F, 1.0F, alpha).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 0, 1).endVertex();
                vertexConsumer.vertex(matrix, minX, minY, maxZ).color(1.0F, 1.0F, 1.0F, alpha).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 0, 1).endVertex();
                vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(1.0F, 1.0F, 1.0F, alpha).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 0, 1).endVertex();
                vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(1.0F, 1.0F, 1.0F, alpha).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(0, 0, 1).endVertex();
                break;
            case EAST: // 东面 (X+)
                vertexConsumer.vertex(matrix, maxX, minY, minZ).color(1.0F, 1.0F, 1.0F, alpha).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(1, 0, 0).endVertex();
                vertexConsumer.vertex(matrix, maxX, minY, maxZ).color(1.0F, 1.0F, 1.0F, alpha).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(1, 0, 0).endVertex();
                vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(1.0F, 1.0F, 1.0F, alpha).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(1, 0, 0).endVertex();
                vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(1.0F, 1.0F, 1.0F, alpha).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(1, 0, 0).endVertex();
                break;
            case WEST: // 西面 (X-)
                vertexConsumer.vertex(matrix, minX, minY, maxZ).color(1.0F, 1.0F, 1.0F, alpha).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(-1, 0, 0).endVertex();
                vertexConsumer.vertex(matrix, minX, minY, minZ).color(1.0F, 1.0F, 1.0F, alpha).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(-1, 0, 0).endVertex();
                vertexConsumer.vertex(matrix, minX, maxY, minZ).color(1.0F, 1.0F, 1.0F, alpha).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(-1, 0, 0).endVertex();
                vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(1.0F, 1.0F, 1.0F, alpha).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(-1, 0, 0).endVertex();
                break;
            default:
                break;
        }
        
        // 恢复之前保存的矩阵状态
        poseStack.popPose();
    }
}