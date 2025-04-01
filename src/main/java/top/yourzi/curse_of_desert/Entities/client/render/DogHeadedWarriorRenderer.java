package top.yourzi.curse_of_desert.Entities.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.Entities.DogHeadedWarrior.DogHeadedWarrior;
import top.yourzi.curse_of_desert.Entities.client.layer.DogHeadedWarriorItemLayer;
import top.yourzi.curse_of_desert.Entities.client.model.DogHeadedWarriorModel;

@OnlyIn(Dist.CLIENT)
public class DogHeadedWarriorRenderer extends MobRenderer<DogHeadedWarrior, DogHeadedWarriorModel<DogHeadedWarrior>> {
    public DogHeadedWarriorRenderer(EntityRendererProvider.Context context) {
        super(context, new DogHeadedWarriorModel<>(context.bakeLayer(DogHeadedWarriorModel.LAYER_LOCATION)), 0.5f);
        this.addLayer(new DogHeadedWarriorItemLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DogHeadedWarrior entity) {
        return new ResourceLocation(Curseofdesert.MOD_ID, "textures/entity/dog_headed_warrior.png");
    }
}