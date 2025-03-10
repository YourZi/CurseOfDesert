package top.yourzi.curse_of_desert.Entities.Mummy;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import top.yourzi.curse_of_desert.Curseofdesert;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;


@OnlyIn(Dist.CLIENT)

public class MummyRenderer extends MobRenderer<Mummy, MummyModel<Mummy>> {



    public MummyRenderer(EntityRendererProvider.Context context) {
        super(context, new MummyModel(context.bakeLayer(MummyModel.LAYER_LOCATION)), 0.5f);
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public @NotNull RenderType renderType() {
                return RenderType.eyes(new ResourceLocation(Curseofdesert.MOD_ID,"textures/entity/mummy_overlay.png"));
            }
        });
        this.addLayer(new MummyItemLayer(this));
        this.addLayer(new MummyArmorLayer(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()
        ));
    }

    @Override
    public ResourceLocation getTextureLocation(Mummy entity) {
        return new ResourceLocation(Curseofdesert.MOD_ID,"textures/entity/mummy.png");
    }

    @OnlyIn(Dist.CLIENT)
    public static class MummyItemLayer extends RenderLayer<Mummy, MummyModel<Mummy>> {

        public MummyItemLayer(RenderLayerParent<Mummy, MummyModel<Mummy>> layer) {
            super(layer);
        }

        public void render(@NotNull PoseStack pose, @NotNull MultiBufferSource source, int i1, Mummy entity, float f1, float f2, float f3, float f4, float f5, float f6) {
            ItemStack right = entity.getItemBySlot(EquipmentSlot.MAINHAND);
            ItemStack left = entity.getItemBySlot(EquipmentSlot.OFFHAND);
            if (!right.isEmpty()) {
                pose.pushPose();
                this.getParentModel().translateItem(HumanoidArm.RIGHT, pose);
                pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
                pose.mulPose(Axis.YP.rotationDegrees(180.0F));
                pose.translate(0.0D, 0.1D, -0.65D);
                Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(entity, right, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, pose, source, i1);
                pose.popPose();
            }
            if (!left.isEmpty()) {
                pose.pushPose();
                this.getParentModel().translateItem(HumanoidArm.LEFT, pose);
                pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
                pose.mulPose(Axis.YP.rotationDegrees(180.0F));
                pose.translate(0.0D, 0.1D, -0.65D);
                Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(entity, left, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, true, pose, source, i1);
                pose.popPose();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    // 完整盔甲层实现
    @OnlyIn(Dist.CLIENT)
    public static class MummyArmorLayer extends RenderLayer<Mummy, MummyModel<Mummy>> {
        private final HumanoidModel<?> innerModel;
        private final HumanoidModel<?> outerModel;
        private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
        private final TextureAtlas armorTrimAtlas;

        public MummyArmorLayer(RenderLayerParent<Mummy, MummyModel<Mummy>> parent,
                               HumanoidModel<?> innerModel,
                               HumanoidModel<?> outerModel,
                               ModelManager modelManager) {
            super(parent);
            this.innerModel = innerModel;
            this.outerModel = outerModel;

            this.armorTrimAtlas = modelManager.getAtlas(Sheets.ARMOR_TRIMS_SHEET);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                           Mummy entity, float limbSwing, float limbSwingAmount,
                           float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            this.renderArmorPiece(poseStack, buffer, entity, EquipmentSlot.HEAD, packedLight);
            this.renderArmorPiece(poseStack, buffer, entity, EquipmentSlot.CHEST, packedLight);
            this.renderArmorPiece(poseStack, buffer, entity, EquipmentSlot.LEGS, packedLight);
            this.renderArmorPiece(poseStack, buffer, entity, EquipmentSlot.FEET, packedLight);
        }

        private void renderArmorPiece(PoseStack poseStack, MultiBufferSource buffer,
                                      Mummy entity, EquipmentSlot slot, int packedLight) {
            ItemStack itemStack = entity.getItemBySlot(slot);

            if (itemStack.getItem() instanceof ArmorItem armorItem) {
                HumanoidModel<?> armorModel = getArmorModel(slot, armorItem);

                boolean flag = this.usesInnerModel(slot);

                if (armorModel == outerModel) {
                    poseStack.scale(0.98F, 0.98F, 0.98F); // 缩小外层模型
                } else {
                    poseStack.scale(1.08F, 1.08F, 1.08F);
                    poseStack.translate(0.0,-0.0,0.0);// 放大内层模型
                }

                // 同步主模型的动画姿势
                this.getParentModel().mummyCopyPropertiesTo(armorModel, poseStack);
                setPartVisibility(armorModel, slot);

                // 应用部位变换
                poseStack.pushPose();
                applySlotTransform(poseStack, slot);

                // 处理染色
                if (armorItem instanceof DyeableLeatherItem) {
                    int i = ((DyeableLeatherItem)armorItem).getColor(itemStack);
                    float f = (float)(i >> 16 & 255) / 255.0F;
                    float f1 = (float)(i >> 8 & 255) / 255.0F;
                    float f2 = (float)(i & 255) / 255.0F;
                    this.renderModel(poseStack, buffer, packedLight, armorItem, armorModel, flag, f, f1, f2, this.getArmorResource(entity, itemStack, slot, (String)null));
                    this.renderModel(poseStack, buffer, packedLight, armorItem, armorModel, flag, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, itemStack, slot, "overlay"));
                } else {
                    this.renderModel(poseStack, buffer, packedLight, armorItem, armorModel, flag, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, itemStack, slot, (String)null));
                }

                ArmorTrim.getTrim(entity.level().registryAccess(), itemStack).ifPresent((p_289638_) -> this.renderTrim(armorItem.getMaterial(), poseStack, buffer, packedLight, p_289638_, armorModel, flag));
                if (itemStack.hasFoil()) {
                    this.renderGlint(poseStack, buffer, packedLight, armorModel);
                }

                poseStack.popPose();
            }
        }


        private HumanoidModel<?> getArmorModel(EquipmentSlot slot, ArmorItem item) {
            return item.getMaterial() == ArmorMaterials.LEATHER ? this.innerModel : this.outerModel;
        }


        private void setPartVisibility(HumanoidModel<?> model, EquipmentSlot slot) {
            model.setAllVisible(false);
            switch (slot) {
                case HEAD -> {
                    model.head.visible = true;
                    model.hat.visible = true;
                }
                case CHEST -> {
                    model.body.visible = true;
                    model.rightArm.visible = true;
                    model.leftArm.visible = true;
                }
                case LEGS -> {
                    model.body.visible = true;
                    model.rightLeg.visible = true;
                    model.leftLeg.visible = true;
                }
                case FEET -> {
                    model.rightLeg.visible = true;
                    model.leftLeg.visible = true;
                }
            }
        }


        private void renderModel(PoseStack p_289664_, MultiBufferSource p_289689_, int p_289681_, ArmorItem p_289650_, Model p_289658_, boolean p_289668_, float p_289678_, float p_289674_, float p_289693_, ResourceLocation armorResource) {
            VertexConsumer vertexconsumer = p_289689_.getBuffer(RenderType.armorCutoutNoCull(armorResource));
            p_289658_.renderToBuffer(p_289664_, vertexconsumer, p_289681_, OverlayTexture.NO_OVERLAY, p_289678_, p_289674_, p_289693_, 1.0F);
        }


        private void renderTrim(ArmorMaterial p_289690_, PoseStack p_289687_, MultiBufferSource p_289643_, int p_289683_, ArmorTrim p_289692_, Model p_289663_, boolean p_289651_) {
            TextureAtlasSprite textureatlassprite = this.armorTrimAtlas.getSprite(p_289651_ ? p_289692_.innerTexture(p_289690_) : p_289692_.outerTexture(p_289690_));
            VertexConsumer vertexconsumer = textureatlassprite.wrap(p_289643_.getBuffer(Sheets.armorTrimsSheet()));
            p_289663_.renderToBuffer(p_289687_, vertexconsumer, p_289683_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        private void renderGlint(PoseStack p_289673_, MultiBufferSource p_289654_, int p_289649_, Model p_289659_) {
            p_289659_.renderToBuffer(p_289673_, p_289654_.getBuffer(RenderType.armorEntityGlint()), p_289649_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }


        private boolean usesInnerModel(EquipmentSlot pSlot) {
            return pSlot == EquipmentSlot.LEGS;
        }


        public ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
            ArmorItem item = (ArmorItem)stack.getItem();
            String texture = item.getMaterial().getName();
            String domain = "minecraft";
            int idx = texture.indexOf(58);
            if (idx != -1) {
                domain = texture.substring(0, idx);
                texture = texture.substring(idx + 1);
            }

            String s1 = String.format(Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, this.usesInnerModel(slot) ? 2 : 1, type == null ? "" : String.format(Locale.ROOT, "_%s", type));
            s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
            ResourceLocation resourcelocation = (ResourceLocation)ARMOR_LOCATION_CACHE.get(s1);
            if (resourcelocation == null) {
                resourcelocation = new ResourceLocation(s1);
                ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
            }

            return resourcelocation;
        }



        private void applySlotTransform(PoseStack poseStack, EquipmentSlot slot) {
            switch (slot) {
                case HEAD -> {
                    poseStack.translate(0.0D, -1.08D, 0.0D);
                    poseStack.scale(1.45F, 1.45F, 1.45F);

                }
                case CHEST -> {
                    poseStack.translate(0.0D, -0.45D, 0.00D);
                    poseStack.scale(1.75F, 2.0F, 2.1F);

                }
                case LEGS -> {
                    poseStack.translate(0.0D, -0.40D, 0.0D);
                    poseStack.scale(1.85F, 1.85F, 1.85F);


                }
                case FEET -> {
                    poseStack.translate(0.0D, -0.2D, 0.0D);
                    poseStack.scale(2.1F, 2.1F, 2.1F);

                }
            }
        }
    }
}
