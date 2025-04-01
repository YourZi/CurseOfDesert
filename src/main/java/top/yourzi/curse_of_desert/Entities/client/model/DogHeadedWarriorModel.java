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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import top.yourzi.curse_of_desert.Entities.DogHeadedWarrior.DogHeadedWarrior;
import top.yourzi.curse_of_desert.Entities.client.animation.DogHeadedWarriorAnimation;

public class DogHeadedWarriorModel<T extends Entity> extends HierarchicalModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("curse_of_desert", "dog_headed_warrior"), "main");
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart left_arm;
	private final ModelPart left_hand;
	private final ModelPart right_arm;
	private final ModelPart right_hand;
	private final ModelPart head;
	private final ModelPart left_leg;
	private final ModelPart right_leg;

	public DogHeadedWarriorModel(ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.left_arm = this.body.getChild("left_arm");
		this.left_hand = this.left_arm.getChild("left_hand");
		this.right_arm = this.body.getChild("right_arm");
		this.right_hand = this.right_arm.getChild("right_hand");
		this.head = this.body.getChild("head");
		this.left_leg = this.root.getChild("left_leg");
		this.right_leg = this.root.getChild("right_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 25).addBox(-4.0F, -14.5F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(-0.01F))
		.texOffs(0, 41).addBox(-4.0F, -4.5F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 13.5F, 0.0F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(24, 27).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 54).mirror().addBox(-1.0F, 2.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(5.0F, -13.5F, 0.0F));

		PartDefinition left_hand = left_arm.addOrReplaceChild("left_hand", CubeListBuilder.create(), PartPose.offset(-10.0F, 9.0F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(24, 27).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 54).addBox(-1.0F, 2.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, -13.5F, 0.0F));

		PartDefinition right_hand = right_arm.addOrReplaceChild("right_hand", CubeListBuilder.create(), PartPose.offset(0.0F, 9.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 16).addBox(-3.0F, -4.0F, -9.0F, 6.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.5F, 0.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(32, 42).addBox(-8.0F, -14.0F, 0.0F, 16.0F, 22.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, -1.4F, -0.2618F, 0.0F, 0.0F));

		PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 8).addBox(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6F, -7.7F, 0.0F, 0.0F, -0.5236F, -0.2618F));

		PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(32, 8).mirror().addBox(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(3.6F, -7.7F, 0.0F, 0.0F, 0.5236F, 0.2618F));

		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(24, 27).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(8, 58).mirror().addBox(-1.0F, 8.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(2.0F, 11.0F, 0.0F));

		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(24, 27).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, 58).addBox(-1.0F, 8.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offset(-2.0F, 11.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch, float pAgeInTicks) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -80.0F, 80.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -80.0F, 80.0F);

        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
    }

    public void translateItem(HumanoidArm arm, PoseStack pose) {
        this.root.translateAndRotate(pose);
        if (arm == HumanoidArm.LEFT) {
			this.body.translateAndRotate(pose);
            this.left_arm.translateAndRotate(pose);
            this.left_hand.translateAndRotate(pose);
        } else{
            this.body.translateAndRotate(pose);
            this.right_arm.translateAndRotate(pose);
            this.right_hand.translateAndRotate(pose);
        }
    }

	@Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);

        this.animateWalk(DogHeadedWarriorAnimation.walk, limbSwing, limbSwingAmount, 2f, 2.5f);

        this.animate(((DogHeadedWarrior) entity).idle, DogHeadedWarriorAnimation.idle, ageInTicks, 1f);
        this.animate(((DogHeadedWarrior) entity).attack, DogHeadedWarriorAnimation.attack, ageInTicks, 1f);
	}
	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return root;
	}
}