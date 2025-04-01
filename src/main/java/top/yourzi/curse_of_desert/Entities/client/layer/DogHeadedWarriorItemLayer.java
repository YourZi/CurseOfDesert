package top.yourzi.curse_of_desert.Entities.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.yourzi.curse_of_desert.Entities.DogHeadedWarrior.DogHeadedWarrior;
import top.yourzi.curse_of_desert.Entities.client.model.DogHeadedWarriorModel;

@OnlyIn(Dist.CLIENT)
public class DogHeadedWarriorItemLayer extends RenderLayer<DogHeadedWarrior, DogHeadedWarriorModel<DogHeadedWarrior>>{

    public DogHeadedWarriorItemLayer(RenderLayerParent<DogHeadedWarrior, DogHeadedWarriorModel<DogHeadedWarrior>> layer) {
        super(layer);
    }

    public void render(PoseStack pose, MultiBufferSource source, int i1, DogHeadedWarrior entity, float f1, float f2, float f3, float f4, float f5, float f6) {
        ItemStack right = entity.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack left = entity.getItemBySlot(EquipmentSlot.OFFHAND);
        if (!right.isEmpty()) {
            pose.pushPose();
            this.getParentModel().translateItem(HumanoidArm.RIGHT, pose);
            pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
            pose.mulPose(Axis.YP.rotationDegrees(180.0F));
            pose.translate(0.0D, 0.1D, -0.0D);
            Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(entity, right, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, pose, source, i1);
            pose.popPose();
        }
        if (!left.isEmpty()) {
            pose.pushPose();
            this.getParentModel().translateItem(HumanoidArm.LEFT, pose);
            pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
            pose.mulPose(Axis.YP.rotationDegrees(180.0F));
            pose.translate(0.0D, 0.1D, -0.0D);
            Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(entity, left, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, true, pose, source, i1);
            pose.popPose();
        }
    }
}
