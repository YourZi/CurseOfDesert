package top.yourzi.curse_of_desert.Client.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Entities.ScarabBeetle.ScarabBeetle;
import top.yourzi.curse_of_desert.Client.entity.model.ScarabBeetleModel;

@OnlyIn(Dist.CLIENT)
public class ScarabBeetleRenderer extends MobRenderer<ScarabBeetle, ScarabBeetleModel<ScarabBeetle>> {
    public ScarabBeetleRenderer(EntityRendererProvider.Context context) {
        super(context, new ScarabBeetleModel(context.bakeLayer(ScarabBeetleModel.LAYER_LOCATION)), 0.5f);
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public @NotNull RenderType renderType() {
                return RenderType.eyes(new ResourceLocation(Curseofdesert.MOD_ID,"textures/entity/scarab_overlay.png"));
            }
        });
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ScarabBeetle entity) {
        return new ResourceLocation(Curseofdesert.MOD_ID, "textures/entity/scarab.png");
    }

    @Override
    public void render(@NotNull ScarabBeetle entity, float entityYaw, float partialTicks,
                      @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        if (entity.isAttacking()) {
            entity.attack.startIfStopped(entity.tickCount);
            entity.walk.stop();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}