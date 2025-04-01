package top.yourzi.curse_of_desert.Entities.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import top.yourzi.curse_of_desert.Entities.client.animation.ScarabBeetleAnimation;

public class ScarabBeetleModel<T extends Entity> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("curse_of_desert", "scarab"), "main");
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart right1_leg1;
	private final ModelPart right1_leg2;
	private final ModelPart right2_leg1;
	private final ModelPart right2_leg2;
	private final ModelPart right3_leg1;
	private final ModelPart right3_leg2;
	private final ModelPart left1_leg1;
	private final ModelPart left1_leg2;
	private final ModelPart left2_leg1;
	private final ModelPart left2_leg2;
	private final ModelPart left3_leg1;
	private final ModelPart left3_leg2;

	public ScarabBeetleModel(ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.head = this.body.getChild("head");
		this.right1_leg1 = this.root.getChild("right1_leg1");
		this.right1_leg2 = this.right1_leg1.getChild("right1_leg2");
		this.right2_leg1 = this.root.getChild("right2_leg1");
		this.right2_leg2 = this.right2_leg1.getChild("right2_leg2");
		this.right3_leg1 = this.root.getChild("right3_leg1");
		this.right3_leg2 = this.right3_leg1.getChild("right3_leg2");
		this.left1_leg1 = this.root.getChild("left1_leg1");
		this.left1_leg2 = this.left1_leg1.getChild("left1_leg2");
		this.left2_leg1 = this.root.getChild("left2_leg1");
		this.left2_leg2 = this.left2_leg1.getChild("left2_leg2");
		this.left3_leg1 = this.root.getChild("left3_leg1");
		this.left3_leg2 = this.left3_leg1.getChild("left3_leg2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 20.5F, -3.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -3.5F, -2.0F, 8.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-4.0F, -3.5F, -2.0F, 8.0F, 6.0F, 10.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 17).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, -2.0F));

		PartDefinition right1_leg1 = root.addOrReplaceChild("right1_leg1", CubeListBuilder.create().texOffs(0, 25).addBox(-3.0F, -0.5F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 1.0F, -1.0F, 0.0F, 0.0F, 0.4363F));

		PartDefinition right1_leg2 = right1_leg1.addOrReplaceChild("right1_leg2", CubeListBuilder.create().texOffs(0, 27).addBox(-0.5F, -0.5F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 1.0F, 0.0F, 0.0F, 0.0F, -0.3054F));

		PartDefinition right2_leg1 = root.addOrReplaceChild("right2_leg1", CubeListBuilder.create().texOffs(0, 25).addBox(-3.0F, -0.5F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 1.0F, 3.0F, 0.0F, 0.0F, 0.4363F));

		PartDefinition right2_leg2 = right2_leg1.addOrReplaceChild("right2_leg2", CubeListBuilder.create().texOffs(0, 27).addBox(-0.5F, -0.5F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 1.0F, 0.0F, 0.0F, 0.0F, -0.3054F));

		PartDefinition right3_leg1 = root.addOrReplaceChild("right3_leg1", CubeListBuilder.create().texOffs(0, 25).addBox(-3.0F, -0.5F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 1.0F, 7.0F, 0.0F, 0.0F, 0.4363F));

		PartDefinition right3_leg2 = right3_leg1.addOrReplaceChild("right3_leg2", CubeListBuilder.create().texOffs(0, 27).addBox(-0.5F, -0.5F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 1.0F, 0.0F, 0.0F, 0.0F, -0.3054F));

		PartDefinition left1_leg1 = root.addOrReplaceChild("left1_leg1", CubeListBuilder.create().texOffs(0, 25).mirror().addBox(-1.0F, -0.5F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.0F, 1.0F, -1.0F, 0.0F, 0.0F, -0.4363F));

		PartDefinition left1_leg2 = left1_leg1.addOrReplaceChild("left1_leg2", CubeListBuilder.create().texOffs(0, 27).mirror().addBox(-1.5F, -0.5F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5F, 1.0F, 0.0F, 0.0F, 0.0F, 0.3054F));

		PartDefinition left2_leg1 = root.addOrReplaceChild("left2_leg1", CubeListBuilder.create().texOffs(0, 25).mirror().addBox(-1.0F, -0.5F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.5F, 1.0F, 3.0F, 0.0F, 0.0F, -0.4363F));

		PartDefinition left2_leg2 = left2_leg1.addOrReplaceChild("left2_leg2", CubeListBuilder.create().texOffs(0, 27).mirror().addBox(-1.5F, -0.5F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5F, 1.0F, 0.0F, 0.0F, 0.0F, 0.3054F));

		PartDefinition left3_leg1 = root.addOrReplaceChild("left3_leg1", CubeListBuilder.create().texOffs(0, 25).mirror().addBox(-1.0F, -0.5F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(3.5F, 1.0F, 7.0F, 0.0F, 0.0F, -0.4363F));

		PartDefinition left3_leg2 = left3_leg1.addOrReplaceChild("left3_leg2", CubeListBuilder.create().texOffs(0, 27).mirror().addBox(-1.5F, -0.5F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5F, 1.0F, 0.0F, 0.0F, 0.0F, 0.3054F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animateWalk(ScarabBeetleAnimation.walk, limbSwing, limbSwingAmount, 2f, 2.5f);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public ModelPart root() {
        return root;
    }
}