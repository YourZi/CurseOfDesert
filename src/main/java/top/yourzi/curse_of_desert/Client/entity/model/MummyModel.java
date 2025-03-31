package top.yourzi.curse_of_desert.Client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.yourzi.curse_of_desert.Entities.BitumenMummy.BitumenMummy;
import top.yourzi.curse_of_desert.Entities.Mummy.Mummy;
import top.yourzi.curse_of_desert.Entities.Mummy.MummyAnimation;

@OnlyIn(Dist.CLIENT)
public class MummyModel<T extends Entity> extends HierarchicalModel<T>{
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "textures/entity/mummy"), "main");
    private final ModelPart root;
    private final ModelPart body_all;
    private final ModelPart head;
    private final ModelPart arm_right;
    private final ModelPart itemr;
    private final ModelPart arm_left;
    private final ModelPart iteml;
    private final ModelPart body;
    private final ModelPart bottle;
    private final ModelPart leg_right;
    private final ModelPart leg_left;

    public MummyModel(ModelPart root) {
        this.root = root.getChild("root");
		this.body_all = this.root.getChild("body_all");
		this.head = this.body_all.getChild("head");
		this.arm_right = this.body_all.getChild("arm_right");
		this.bottle = this.body_all.getChild("bottle");
		this.arm_left = this.body_all.getChild("arm_left");
		this.body = this.body_all.getChild("body");
		this.leg_right = this.root.getChild("leg_right");
		this.leg_left = this.root.getChild("leg_left");
        this.itemr = this.arm_right.getChild("itemr");
        this.iteml = this.arm_left.getChild("iteml");
    }

    public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body_all = root.addOrReplaceChild("body_all", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition head = body_all.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.51F))
		.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition arm_right = body_all.addOrReplaceChild("arm_right", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(16, 16).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F))
		.texOffs(0, 32).addBox(-2.0F, -1.0F, 2.0F, 4.0F, 12.0F, 10.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(-6.0F, -10.0F, 1.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition bottle = body_all.addOrReplaceChild("bottle", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -10.0F, 1.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r1 = bottle.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(92, 86).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 11.0F, -6.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r2 = bottle.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(92, 94).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.01F))
		.texOffs(92, 110).addBox(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition arm_left = body_all.addOrReplaceChild("arm_left", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(16, 16).mirror().addBox(-2.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false)
		.texOffs(0, 32).mirror().addBox(-2.0F, -1.0F, 2.0F, 4.0F, 12.0F, 10.0F, new CubeDeformation(-0.001F)).mirror(false), PartPose.offsetAndRotation(6.0F, -10.0F, 1.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition body = body_all.addOrReplaceChild("body", CubeListBuilder.create().texOffs(32, 16).addBox(-4.0F, -6.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(56, 16).addBox(-4.0F, -6.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -6.0F, 0.0F));

		PartDefinition leg_right = root.addOrReplaceChild("leg_right", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(16, 16).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-2.0F, 13.0F, 0.0F));

		PartDefinition leg_left = root.addOrReplaceChild("leg_left", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(16, 16).mirror().addBox(-2.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(2.0F, 13.0F, 0.0F));

		PartDefinition itemr = arm_right.addOrReplaceChild("itemr", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 8.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, 1.0F));

		PartDefinition iteml = arm_left.addOrReplaceChild("iteml", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 8.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.0F, 1.0F));
		return LayerDefinition.create(meshdefinition, 128, 128);
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
            this.body_all.translateAndRotate(pose);
            this.arm_left.translateAndRotate(pose);
            this.iteml.translateAndRotate(pose);
        } else{
            this.body_all.translateAndRotate(pose);
            this.arm_right.translateAndRotate(pose);
            this.itemr.translateAndRotate(pose);
        }
    }


    public void copyPropertiesTo(HumanoidModel<?> model,  PoseStack pose) {
        // 应用根节点和身体节点的变换
        this.root.translateAndRotate(pose);
        this.body_all.translateAndRotate(pose);

        // 复制模型部件的姿势
		model.head.copyFrom(this.head);
        model.hat.copyFrom(model.head);
		model.body.copyFrom(this.body);
		model.leftArm.copyFrom(this.arm_left);
		model.rightArm.copyFrom(this.arm_right);
		model.rightLeg.copyFrom(this.leg_right);
		model.leftLeg.copyFrom(this.leg_left);

	}



    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);

        this.animateWalk(MummyAnimation.walk, limbSwing, limbSwingAmount, 2f, 2.5f);

        this.animate(((Mummy) entity).idle, MummyAnimation.idle, ageInTicks, 1f);
        this.animate(((Mummy) entity).attack, MummyAnimation.attack, ageInTicks, 1f);
        this.animate(((Mummy) entity).throwbottle, MummyAnimation.throwbottle, ageInTicks, 1f);

        // 控制瓶子的显示
        if (entity instanceof BitumenMummy && ((BitumenMummy) entity).hasBottle()) {
            this.bottle.xScale = 1.0F;
            this.bottle.yScale = 1.0F;
            this.bottle.zScale = 1.0F;
        } else {
            this.bottle.xScale = 0.0F;
            this.bottle.yScale = 0.0F;
            this.bottle.zScale = 0.0F;
        }
    }


	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

    public ModelPart root() {
        return root;
    }
}