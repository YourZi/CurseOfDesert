package top.yourzi.curse_of_desert.Entities.ai;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.TridentItem;
import top.yourzi.curse_of_desert.Entities.GiantScorpion.GiantScorpion;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 巨蝎武器拾取AI目标
 * 当周围有武器且伤害值大于25%自身伤害值时会去捡起武器
 */
public class GiantScorpionPickupWeaponGoal extends Goal {
    private final GiantScorpion giantScorpion;
    private final double speedModifier;
    private ItemEntity targetWeapon;
    private int cooldownTicks = 0;
    
    private static final int PICKUP_COOLDOWN = 60; // 拾取冷却时间（3秒）
    private static final double PICKUP_RANGE = 8.0; // 拾取范围
    private static final double WEAPON_DAMAGE_THRESHOLD = 0.25; // 武器伤害阈值（25%）

    public GiantScorpionPickupWeaponGoal(GiantScorpion giantScorpion, double speedModifier) {
        this.giantScorpion = giantScorpion;
        this.speedModifier = speedModifier;
    }

    @Override
    public boolean canUse() {
        // 冷却时间未结束
        if (this.cooldownTicks > 0) {
            this.cooldownTicks--;
            return false;
        }
        
        // 如果已经有武器且武器足够好，不需要拾取新武器
        ItemStack currentWeapon = this.giantScorpion.getMainHandItem();
        if (!currentWeapon.isEmpty() && isWeaponWorthKeeping(currentWeapon)) {
            return false;
        }
        
        // 如果正在攻击，不要拾取武器
        if (this.giantScorpion.isAttacking() || this.giantScorpion.isTailAttacking()) {
            return false;
        }
        
        // 寻找附近的武器
        this.targetWeapon = findBestWeapon();
        return this.targetWeapon != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetWeapon != null && 
               this.targetWeapon.isAlive() && 
               this.giantScorpion.distanceToSqr(this.targetWeapon) < PICKUP_RANGE * PICKUP_RANGE &&
               !this.giantScorpion.isAttacking() && 
               !this.giantScorpion.isTailAttacking();
    }

    @Override
    public void start() {
        if (this.targetWeapon != null) {
            this.giantScorpion.getNavigation().moveTo(this.targetWeapon, this.speedModifier);
        }
    }

    @Override
    public void tick() {
        if (this.targetWeapon != null) {
            // 移动到武器位置
            this.giantScorpion.getNavigation().moveTo(this.targetWeapon, this.speedModifier);
            
            // 如果足够接近，拾取武器
            if (this.giantScorpion.distanceToSqr(this.targetWeapon) < 2.0) {
                pickupWeapon();
            }
        }
    }

    @Override
    public void stop() {
        this.targetWeapon = null;
        this.giantScorpion.getNavigation().stop();
        this.cooldownTicks = PICKUP_COOLDOWN; // 设置冷却时间
    }

    /**
     * 寻找最佳武器
     */
    private ItemEntity findBestWeapon() {
        List<ItemEntity> nearbyItems = this.giantScorpion.level().getEntitiesOfClass(
            ItemEntity.class,
            this.giantScorpion.getBoundingBox().inflate(PICKUP_RANGE)
        );
        
        ItemEntity bestWeapon = null;
        double bestDamage = 0;
        double minRequiredDamage = this.giantScorpion.getAttributeValue(Attributes.ATTACK_DAMAGE) * WEAPON_DAMAGE_THRESHOLD;
        
        for (ItemEntity itemEntity : nearbyItems) {
            ItemStack itemStack = itemEntity.getItem();
            
            // 检查是否是武器
            if (isWeapon(itemStack)) {
                double weaponDamage = getWeaponDamage(itemStack);
                
                // 检查武器伤害是否达到阈值
                if (weaponDamage >= minRequiredDamage && weaponDamage > bestDamage) {
                    bestWeapon = itemEntity;
                    bestDamage = weaponDamage;
                }
            }
        }
        
        return bestWeapon;
    }

    /**
     * 检查物品是否是武器
     */
    private boolean isWeapon(ItemStack itemStack) {
        return itemStack.getItem() instanceof SwordItem ||
               itemStack.getItem() instanceof AxeItem ||
               itemStack.getItem() instanceof TridentItem;
    }

    /**
     * 获取武器伤害值
     */
    private double getWeaponDamage(ItemStack weapon) {
        AtomicReference<Double> damage = new AtomicReference<>(0.0);
        
        weapon.getAttributeModifiers(EquipmentSlot.MAINHAND).forEach((attribute, modifier) -> {
            if (attribute.equals(Attributes.ATTACK_DAMAGE)) {
                damage.updateAndGet(currentDamage -> currentDamage + modifier.getAmount());
            }
        });
        
        return damage.get();
    }

    /**
     * 检查当前武器是否值得保留
     */
    private boolean isWeaponWorthKeeping(ItemStack weapon) {
        if (!isWeapon(weapon)) {
            return false;
        }
        
        double weaponDamage = getWeaponDamage(weapon);
        double minRequiredDamage = this.giantScorpion.getAttributeValue(Attributes.ATTACK_DAMAGE) * WEAPON_DAMAGE_THRESHOLD;
        
        return weaponDamage >= minRequiredDamage;
    }

    /**
     * 拾取武器
     */
    private void pickupWeapon() {
        if (this.targetWeapon != null && this.targetWeapon.isAlive()) {
            ItemStack weaponStack = this.targetWeapon.getItem();
            
            // 如果当前有武器，丢弃它
            ItemStack currentWeapon = this.giantScorpion.getMainHandItem();
            if (!currentWeapon.isEmpty()) {
                this.giantScorpion.spawnAtLocation(currentWeapon);
            }
            
            // 装备新武器
            this.giantScorpion.setItemSlot(EquipmentSlot.MAINHAND, weaponStack.copy());
            
            // 移除地面上的物品
            this.targetWeapon.discard();
            
            this.targetWeapon = null;
        }
    }
}