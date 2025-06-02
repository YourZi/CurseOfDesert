package top.yourzi.curse_of_desert.Entities.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Entities.DogHeadedPriests.DogHeadedPriests;
import top.yourzi.curse_of_desert.Entities.client.model.DogHeadedPriestsModel;

public class DogHeadedPriestsRenderer extends MobRenderer<DogHeadedPriests, DogHeadedPriestsModel<DogHeadedPriests>> {
    public DogHeadedPriestsRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new DogHeadedPriestsModel<>(pContext.bakeLayer(DogHeadedPriestsModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DogHeadedPriests pEntity) {
        return new ResourceLocation(Curseofdesert.MOD_ID, "textures/entity/dog_headed_priests.png");
    }

    @Override
    public void render(DogHeadedPriests pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
                       MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
}