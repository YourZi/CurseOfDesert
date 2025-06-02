package top.yourzi.curse_of_desert.Entities.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.yourzi.curse_of_desert.Entities.GiantScorpion.GiantScorpion;
import top.yourzi.curse_of_desert.Entities.client.model.GiantScorpionModel;

/**
 * 巨蝎物品渲染层
 * 用于渲染巨蝎手持的武器
 */
public class GiantScorpionItemLayer extends RenderLayer<GiantScorpion, GiantScorpionModel<GiantScorpion>> {
    private final ItemInHandRenderer itemInHandRenderer;

    public GiantScorpionItemLayer(RenderLayerParent<GiantScorpion, GiantScorpionModel<GiantScorpion>> pRenderer, ItemInHandRenderer itemRenderer) {
        super(pRenderer);
        this.itemInHandRenderer = itemRenderer;
    }

    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, GiantScorpion pLivingEntity,
                       float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks,
                       float pNetHeadYaw, float pHeadPitch) {
        
        ItemStack itemStack = pLivingEntity.getItemBySlot(EquipmentSlot.MAINHAND);
        
        if (!itemStack.isEmpty()) {
            pMatrixStack.pushPose();
            
            // 将物品放置在右爪位置
            getParentModel().root().translateAndRotate(pMatrixStack);
            getParentModel().body.translateAndRotate(pMatrixStack);
            getParentModel().right_arm.translateAndRotate(pMatrixStack);
            getParentModel().right_claw.translateAndRotate(pMatrixStack);
            getParentModel().right_hand.translateAndRotate(pMatrixStack);
            
            // 调整物品位置和旋转
            pMatrixStack.translate(-0.15F, 0.0F, -0.4F);
            pMatrixStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            pMatrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            // 额外旋转90度并放大2倍
            pMatrixStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            pMatrixStack.scale(1.6F, 1.6F, 1.6F);
            
            // 渲染物品
            this.itemInHandRenderer.renderItem(pLivingEntity, itemStack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                    false, pMatrixStack, pBuffer, pPackedLight);
            
            pMatrixStack.popPose();
        }
    }
}