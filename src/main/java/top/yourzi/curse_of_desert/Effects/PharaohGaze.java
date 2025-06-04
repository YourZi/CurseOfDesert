package top.yourzi.curse_of_desert.Effects;

import com.mojang.blaze3d.shaders.FogShape;

import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.init.ModEffect;

@Mod.EventBusSubscriber(modid = Curseofdesert.MOD_ID, value = Dist.CLIENT)
public class PharaohGaze extends MobEffect {
    public PharaohGaze() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    public float onDamage(LivingEntity target, float amount, LivingEntity source) {
        if (target.hasEffect(this) && source != null && source.getType().is(top.yourzi.curse_of_desert.init.ModTags.CURSE_OF_DESERT)) {
            int amplifier = target.getEffect(this).getAmplifier();
            float increase = amount * (0.25f * (amplifier + 1));
            return amount + increase;
        }
        return amount;
    }

    private static float fogDensityOffset = 0.0f;
    private static long lastUpdateTime = 0;

    // 暂时注释掉沙暴迷雾效果
    /*
    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST)
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Player player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null && player.hasEffect(ModEffect.PHARAOH_GAZE.get())) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime > 50) {
                fogDensityOffset = (float) (Math.sin(currentTime * 0.001) * 0.1);
                lastUpdateTime = currentTime;
            }

            int amplifier = player.getEffect(ModEffect.PHARAOH_GAZE.get()).getAmplifier();
            
            float baseFarDistance = 32.0f - (amplifier * 4.0f);
            float baseNearDistance = Math.max(2.0f, baseFarDistance * 0.75f);
            
            float farDistance = baseFarDistance + (fogDensityOffset * 5.0f);
            float nearDistance = baseNearDistance + (fogDensityOffset * 2.0f);

            float distanceFactor = Math.min(1.0f, (float) player.position().distanceTo(player.position().add(0, farDistance, 0)) / farDistance);
            float[] baseFogColor = {0.85f, 0.75f, 0.5f};
            float[] denseFogColor = {0.75f, 0.7f, 0.45f};
            
            float[] finalColor = {
                baseFogColor[0] + (denseFogColor[0] - baseFogColor[0]) * distanceFactor,
                baseFogColor[1] + (denseFogColor[1] - baseFogColor[1]) * distanceFactor,
                baseFogColor[2] + (denseFogColor[2] - baseFogColor[2]) * distanceFactor
            };
            
            com.mojang.blaze3d.systems.RenderSystem.setShaderFogColor(finalColor[0], finalColor[1], finalColor[2]);

            if (event.getMode() == FogMode.FOG_TERRAIN) {
                event.setFarPlaneDistance(farDistance);
                event.setNearPlaneDistance(nearDistance);
                event.setFogShape(FogShape.CYLINDER);
                event.setCanceled(true);
            }
        }
    }
    */
}