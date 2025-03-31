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
        super(MobEffectCategory.HARMFUL, 0x8B0000); // 深红色
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 效果每tick的处理逻辑
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    // 处理伤害增加
    public float onDamage(LivingEntity target, float amount, LivingEntity source) {
        if (target.hasEffect(this) && source != null && source.getType().is(top.yourzi.curse_of_desert.init.ModTags.CURSE_OF_DESERT)) {
            int amplifier = target.getEffect(this).getAmplifier();
            float increase = amount * (0.25f * (amplifier + 1)); // 每级增加25%伤害
            return amount + increase;
        }
        return amount;
    }

    // 处理视距限制效果
    private static float fogDensityOffset = 0.0f;
    private static long lastUpdateTime = 0;

    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST)
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Player player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null && player.hasEffect(ModEffect.PHARAOH_GAZE.get())) {
            // 动态更新迷雾密度
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime > 50) { // 每50ms更新一次
                fogDensityOffset = (float) (Math.sin(currentTime * 0.001) * 0.1); // 添加轻微波动
                lastUpdateTime = currentTime;
            }

            // 获取效果等级
            int amplifier = player.getEffect(ModEffect.PHARAOH_GAZE.get()).getAmplifier();
            
            // 根据效果等级和动态偏移计算迷雾距离
            float baseFarDistance = 32.0f - (amplifier * 4.0f);
            float baseNearDistance = Math.max(2.0f, baseFarDistance * 0.75f);
            
            // 应用动态偏移
            float farDistance = baseFarDistance + (fogDensityOffset * 5.0f);
            float nearDistance = baseNearDistance + (fogDensityOffset * 2.0f);

            // 设置沙暴颜色（添加深度渐变）
            float distanceFactor = Math.min(1.0f, (float) player.position().distanceTo(player.position().add(0, farDistance, 0)) / farDistance);
            float[] baseFogColor = {0.85f, 0.75f, 0.5f}; // 基础沙黄色
            float[] denseFogColor = {0.75f, 0.7f, 0.45f}; // 较深的沙色
            
            float[] finalColor = {
                baseFogColor[0] + (denseFogColor[0] - baseFogColor[0]) * distanceFactor,
                baseFogColor[1] + (denseFogColor[1] - baseFogColor[1]) * distanceFactor,
                baseFogColor[2] + (denseFogColor[2] - baseFogColor[2]) * distanceFactor
            };
            
            com.mojang.blaze3d.systems.RenderSystem.setShaderFogColor(finalColor[0], finalColor[1], finalColor[2]);

            // 应用迷雾效果
            if (event.getMode() == FogMode.FOG_TERRAIN) {
                event.setFarPlaneDistance(farDistance);
                event.setNearPlaneDistance(nearDistance);
                event.setFogShape(FogShape.CYLINDER); // 使用圆柱形迷雾，更符合自然现象
                event.setCanceled(true);
            }
        }
    }
}