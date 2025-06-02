package top.yourzi.curse_of_desert.Entities.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import top.yourzi.curse_of_desert.Entities.GiantScorpion.GiantScorpion;
import top.yourzi.curse_of_desert.Entities.client.animation.GiantScorpionAnimation;

public class GiantScorpionModel<T extends Entity> extends HierarchicalModel<T> {
	
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("curse_of_desert", "giant_scorpion"), "main");
	private final ModelPart root;
	public final ModelPart body;
	private final ModelPart tail;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart aculeus;
	public final ModelPart right_arm;
	public final ModelPart right_claw;
	private final ModelPart right_claw1;
	private final ModelPart right_claw2;
	public final ModelPart right_hand;
	private final ModelPart left_arm;
	private final ModelPart left_claw;
	private final ModelPart left_claw1;
	private final ModelPart left_claw2;
	private final ModelPart bone;
	private final ModelPart right_leg1_1;
	private final ModelPart right_leg1_2;
	private final ModelPart right_leg2_1;
	private final ModelPart right_leg2_2;
	private final ModelPart right_leg3_1;
	private final ModelPart right_leg3_2;
	private final ModelPart left_leg1_1;
	private final ModelPart left_leg1_2;
	private final ModelPart left_leg2_1;
	private final ModelPart left_leg2_2;
	private final ModelPart left_leg3_1;
	private final ModelPart left_leg3_2;

	public GiantScorpionModel(ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.tail = this.body.getChild("tail");
		this.tail2 = this.tail.getChild("tail2");
		this.tail3 = this.tail2.getChild("tail3");
		this.aculeus = this.tail3.getChild("aculeus");
		this.right_arm = this.body.getChild("right_arm");
		this.right_claw = this.right_arm.getChild("right_claw");
		this.right_claw1 = this.right_claw.getChild("right_claw1");
		this.right_claw2 = this.right_claw.getChild("right_claw2");
		this.right_hand = this.right_claw.getChild("right_hand");
		this.left_arm = this.body.getChild("left_arm");
		this.left_claw = this.left_arm.getChild("left_claw");
		this.left_claw1 = this.left_claw.getChild("left_claw1");
		this.left_claw2 = this.left_claw.getChild("left_claw2");
		this.bone = this.left_claw.getChild("bone");
		this.right_leg1_1 = this.root.getChild("right_leg1_1");
		this.right_leg1_2 = this.right_leg1_1.getChild("right_leg1_2");
		this.right_leg2_1 = this.root.getChild("right_leg2_1");
		this.right_leg2_2 = this.right_leg2_1.getChild("right_leg2_2");
		this.right_leg3_1 = this.root.getChild("right_leg3_1");
		this.right_leg3_2 = this.right_leg3_1.getChild("right_leg3_2");
		this.left_leg1_1 = this.root.getChild("left_leg1_1");
		this.left_leg1_2 = this.left_leg1_1.getChild("left_leg1_2");
		this.left_leg2_1 = this.root.getChild("left_leg2_1");
		this.left_leg2_2 = this.left_leg2_1.getChild("left_leg2_2");
		this.left_leg3_1 = this.root.getChild("left_leg3_1");
		this.left_leg3_2 = this.left_leg3_1.getChild("left_leg3_2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, -3.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -5.0F, -14.0F, 16.0F, 10.0F, 28.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 6.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(88, 16).addBox(-5.0F, -3.0F, -1.0F, 10.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 11.0F, 0.9599F, 0.0F, 0.0F));

		PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(136, 10).addBox(-3.0F, -3.0F, -1.0F, 6.0F, 6.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 11.0F, 0.8727F, 0.0F, 0.0F));

		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(192, 12).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 22.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, -1.0F, 20.0F, 1.0472F, 0.0F, 0.0F));

		PartDefinition aculeus = tail3.addOrReplaceChild("aculeus", CubeListBuilder.create().texOffs(232, -12).addBox(0.0F, -3.0F, -3.0F, 0.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 20.0F, 0.9599F, 0.0F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -2.0F, -14.0F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -1.0F, -11.0F, 0.0F, 0.6109F, 0.0F));

		PartDefinition right_claw = right_arm.addOrReplaceChild("right_claw", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, -14.0F, 0.0F, -0.9599F, 0.0F));

		PartDefinition right_claw1 = right_claw.addOrReplaceChild("right_claw1", CubeListBuilder.create().texOffs(0, 80).addBox(-4.0F, -3.0F, -9.0F, 6.0F, 6.0F, 10.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, -0.1745F, 0.0F));

		PartDefinition right_claw2 = right_claw.addOrReplaceChild("right_claw2", CubeListBuilder.create().texOffs(0, 58).addBox(-2.0F, -4.0F, -13.0F, 4.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.1745F, 0.0F));

		PartDefinition right_hand = right_claw.addOrReplaceChild("right_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -4.0F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 38).mirror().addBox(-2.0F, -2.0F, -14.0F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(8.0F, -1.0F, -11.0F, 0.0F, -0.6109F, 0.0F));

		PartDefinition left_claw = left_arm.addOrReplaceChild("left_claw", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, -14.0F, 0.0F, 0.9599F, 0.0F));

		PartDefinition left_claw1 = left_claw.addOrReplaceChild("left_claw1", CubeListBuilder.create().texOffs(0, 80).mirror().addBox(-2.0F, -3.0F, -9.0F, 6.0F, 6.0F, 10.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.1745F, 0.0F));

		PartDefinition left_claw2 = left_claw.addOrReplaceChild("left_claw2", CubeListBuilder.create().texOffs(0, 58).mirror().addBox(-2.0F, -4.0F, -13.0F, 4.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, -0.1745F, 0.0F));

		PartDefinition bone = left_claw.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -4.0F));

		PartDefinition right_leg1_1 = root.addOrReplaceChild("right_leg1_1", CubeListBuilder.create().texOffs(0, 96).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.5F, 2.0F, 0.5F, -0.3491F, 0.0F, 1.7453F));

		PartDefinition right_leg1_2 = right_leg1_1.addOrReplaceChild("right_leg1_2", CubeListBuilder.create().texOffs(0, 113).addBox(-2.5F, -1.0F, -1.5F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 12.0F, -0.5F, 0.0F, 0.0F, -0.7854F));

		PartDefinition right_leg2_1 = root.addOrReplaceChild("right_leg2_1", CubeListBuilder.create().texOffs(0, 96).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.5F, 2.0F, 6.5F, 0.0873F, 0.0F, 1.7453F));

		PartDefinition right_leg2_2 = right_leg2_1.addOrReplaceChild("right_leg2_2", CubeListBuilder.create().texOffs(0, 113).addBox(-2.5F, -1.0F, -1.5F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 12.0F, -0.5F, 0.0F, 0.0F, -0.7854F));

		PartDefinition right_leg3_1 = root.addOrReplaceChild("right_leg3_1", CubeListBuilder.create().texOffs(0, 96).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.5F, 2.0F, 13.5F, 0.4363F, 0.0F, 1.7453F));

		PartDefinition right_leg3_2 = right_leg3_1.addOrReplaceChild("right_leg3_2", CubeListBuilder.create().texOffs(0, 113).addBox(-2.5F, -1.0F, -1.5F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 12.0F, -0.5F, 0.0F, 0.0F, -0.7854F));

		PartDefinition left_leg1_1 = root.addOrReplaceChild("left_leg1_1", CubeListBuilder.create().texOffs(0, 96).mirror().addBox(-1.5F, -2.0F, -1.5F, 3.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(6.5F, 2.0F, 0.5F, -0.3491F, 0.0F, -1.7453F));

		PartDefinition left_leg1_2 = left_leg1_1.addOrReplaceChild("left_leg1_2", CubeListBuilder.create().texOffs(0, 113).mirror().addBox(-1.5F, -1.0F, -1.5F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.5F, 12.0F, -0.5F, 0.0F, 0.0F, 0.7854F));

		PartDefinition left_leg2_1 = root.addOrReplaceChild("left_leg2_1", CubeListBuilder.create().texOffs(0, 96).mirror().addBox(-1.5F, -2.0F, -1.5F, 3.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(7.5F, 2.0F, 6.5F, 0.0873F, 0.0F, -1.7453F));

		PartDefinition left_leg2_2 = left_leg2_1.addOrReplaceChild("left_leg2_2", CubeListBuilder.create().texOffs(0, 113).mirror().addBox(-1.5F, -1.0F, -1.5F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.5F, 12.0F, -0.5F, 0.0F, 0.0F, 0.7854F));

		PartDefinition left_leg3_1 = root.addOrReplaceChild("left_leg3_1", CubeListBuilder.create().texOffs(0, 96).mirror().addBox(-1.5F, -2.0F, -1.5F, 3.0F, 14.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(6.5F, 2.0F, 13.5F, 0.4363F, 0.0F, -1.7453F));

		PartDefinition left_leg3_2 = left_leg3_1.addOrReplaceChild("left_leg3_2", CubeListBuilder.create().texOffs(0, 113).mirror().addBox(-1.5F, -1.0F, -1.5F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.5F, 12.0F, -0.5F, 0.0F, 0.0F, 0.7854F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		
		if (entity instanceof GiantScorpion giantScorpion) {
			// 行走动画
			this.animateWalk(GiantScorpionAnimation.walk, limbSwing, limbSwingAmount, 1.0F, 1.0F);
			
			// 尾部待机动画（与行走动画同时播放）
			this.animate(giantScorpion.tailIdleAnimationState, GiantScorpionAnimation.tail_idle, ageInTicks, 1.0F);
			
			// 攻击动画：根据是否持有武器选择不同的攻击动画
			if (giantScorpion.isAttacking()) {
				if (giantScorpion.getMainHandItem().isEmpty()) {
					// 无武器攻击动画
					this.animate(giantScorpion.attackAnimationState, GiantScorpionAnimation.attack_noweapon, ageInTicks, 1.0F);
				} else {
					// 有武器攻击动画
					this.animate(giantScorpion.attackAnimationState, GiantScorpionAnimation.attack_weapon, ageInTicks, 1.0F);
				}
			}
			
			// 尾部攻击动画
			if (giantScorpion.isTailAttacking()) {
				this.animate(giantScorpion.tailAttackAnimationState, GiantScorpionAnimation.attack_tail, ageInTicks, 1.0F);
			}
		}
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}