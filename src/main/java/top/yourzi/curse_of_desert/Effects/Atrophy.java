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
    }

    public void onHurt(LivingEntity entity, float amount) {
        if (entity.hasEffect(this)) {
            double currentReduction = healthReduction.getOrDefault(entity, 0.0);
            if (currentReduction == 0.0 && entity.getPersistentData().contains(ATROPHY_REDUCTION_TAG)) {
                currentReduction = entity.getPersistentData().getDouble(ATROPHY_REDUCTION_TAG);
            }
            double newReduction = currentReduction + amount;
            healthReduction.put(entity, newReduction);
            // 保存到NBT数据
            entity.getPersistentData().putDouble(ATROPHY_REDUCTION_TAG, newReduction);

            // 移除旧的修改器
            entity.getAttribute(Attributes.MAX_HEALTH).removeModifier(HEALTH_MODIFIER_UUID);

            // 添加新的修改器
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
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, net.minecraft.world.entity.ai.attributes.AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
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

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}