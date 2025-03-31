package top.yourzi.curse_of_desert.Effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Atrophy extends MobEffect {
    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("d5d0d878-b4c0-4c4c-8461-89c1b2967a9d");
    private static final String ATROPHY_REDUCTION_TAG = "AtrophyReduction";
    private final Map<LivingEntity, Double> originalMaxHealth = new HashMap<>();
    private final Map<LivingEntity, Double> healthReduction = new HashMap<>();

    public Atrophy() {
        super(MobEffectCategory.HARMFUL, 0xCDBE70);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!originalMaxHealth.containsKey(entity)) {
            originalMaxHealth.put(entity, entity.getAttributeValue(Attributes.MAX_HEALTH));
        }
        
        // 检查并应用已保存的生命值削减
        if (entity.getPersistentData().contains(ATROPHY_REDUCTION_TAG)) {
            double savedReduction = entity.getPersistentData().getDouble(ATROPHY_REDUCTION_TAG);
            if (!healthReduction.containsKey(entity) || healthReduction.get(entity) != savedReduction) {
                // 移除旧的修改器
                entity.getAttribute(Attributes.MAX_HEALTH).removeModifier(HEALTH_MODIFIER_UUID);
                
                // 应用保存的削减值
                AttributeModifier modifier = new AttributeModifier(
                    HEALTH_MODIFIER_UUID,
                    "Atrophy health reduction",
                    -savedReduction,
                    AttributeModifier.Operation.ADDITION
                );
                entity.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(modifier);
                healthReduction.put(entity, savedReduction);
            }
        }
    }

    public void onHurt(LivingEntity entity, float amount) {
        if (entity.hasEffect(this)) {
            // 获取原始最大生命值
            double originalHealth = originalMaxHealth.getOrDefault(entity, entity.getAttributeValue(Attributes.MAX_HEALTH));
            
            // 获取当前的生命值削减
            double currentReduction = healthReduction.getOrDefault(entity, 0.0);
            if (currentReduction == 0.0 && entity.getPersistentData().contains(ATROPHY_REDUCTION_TAG)) {
                currentReduction = entity.getPersistentData().getDouble(ATROPHY_REDUCTION_TAG);
            }
            
            // 计算新的削减值，确保不会导致最大生命值低于1
            double newReduction = Math.min(currentReduction + (amount * 0.5), originalHealth - 1.0);
            
            // 移除旧的修改器
            entity.getAttribute(Attributes.MAX_HEALTH).removeModifier(HEALTH_MODIFIER_UUID);
            
            // 应用新的生命值修改器
            AttributeModifier modifier = new AttributeModifier(
                    HEALTH_MODIFIER_UUID,
                    "Atrophy health reduction",
                    -newReduction,
                    AttributeModifier.Operation.ADDITION
            );
            entity.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(modifier);
            
            // 确保当前生命值不超过新的最大生命值
            double newMaxHealth = entity.getAttributeValue(Attributes.MAX_HEALTH);
            if (entity.getHealth() > newMaxHealth) {
                entity.setHealth((float) newMaxHealth);
            }
            
            // 保存数据
            healthReduction.put(entity, newReduction);
            entity.getPersistentData().putDouble(ATROPHY_REDUCTION_TAG, newReduction);
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, net.minecraft.world.entity.ai.attributes.AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        // 只有在效果完全消失时才清除数据
        if (!entity.hasEffect(this)) {
            // 移除最大生命值修改器
            if (entity.getAttribute(Attributes.MAX_HEALTH) != null) {
                entity.getAttribute(Attributes.MAX_HEALTH).removeModifier(HEALTH_MODIFIER_UUID);
            }
            
            // 清理存储的数据
            originalMaxHealth.remove(entity);
            healthReduction.remove(entity);
            // 清理NBT数据
            entity.getPersistentData().remove(ATROPHY_REDUCTION_TAG);
        }
      
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}