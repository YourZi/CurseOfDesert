package top.yourzi.curse_of_desert.Entities.BitumenBottle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.yourzi.curse_of_desert.Curseofdesert;

@OnlyIn(Dist.CLIENT)
public class BitumenBottleModel extends EntityModel<BitumenBottle> {
    @Override
    public void setupAnim(BitumenBottle entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
  // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Curseofdesert.MOD_ID, "textures/entity/mummy"), "main");
	private final ModelPart bottle;

	public BitumenBottleModel(ModelPart root) {
		this.bottle = root.getChild("bottle");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bottle = partdefinition.addOrReplaceChild("bottle", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 17.5F, 0.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r1 = bottle.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(92, 86).addBox(-1.5F, 11.5F, -2.5F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(-0.5F, -0.5F, -17.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r2 = bottle.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(92, 94).addBox(-3.5F, 9.5F, -4.5F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.01F))
		.texOffs(92, 110).addBox(-4.0F, 9.0F, -5.0F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -0.5F, -11.5F, 1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bottle.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}