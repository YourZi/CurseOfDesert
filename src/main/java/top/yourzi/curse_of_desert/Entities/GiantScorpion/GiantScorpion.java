package top.yourzi.curse_of_desert.Entities.GiantScorpion;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.concurrent.atomic.AtomicReference;

import org.jetbrains.annotations.NotNull;
import top.yourzi.curse_of_desert.AttackEvent.CurseOfDesertEvent;
import top.yourzi.curse_of_desert.Entities.ai.GiantScorpionAttackGoal;
import top.yourzi.curse_of_desert.Entities.ai.GiantScorpionPickupWeaponGoal;
import top.yourzi.curse_of_desert.Events.CurseOfDesertEventHandler;

/**
 * 巨蝎实体类
 * 实现了两种攻击方式：近战夹击和尾部刺击
 * 能够捡起武器并使用
 * 免疫中毒，在狂沙之咒事件范围内时免疫火焰
 */
public class GiantScorpion extends Monster {
    // 实体数据同步器
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(GiantScorpion.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TAIL_ATTACKING =
            SynchedEntityData.defineId(GiantScorpion.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TAIL_ATTACKING_DECISION = // 新增：用于尾部攻击决策的标志
            SynchedEntityData.defineId(GiantScorpion.class, EntityDataSerializers.BOOLEAN);

    // 动画状态
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState tailAttackAnimationState = new AnimationState();
    public final AnimationState tailIdleAnimationState = new AnimationState();
    
    // 动画超时计时器
    public int attackAnimationTimeout = 0;
    public int tailAttackAnimationTimeout = 0;
    private int tailAttackCooldown = 0; // 尾部攻击冷却时间
    private int attackDecisionCooldown = 0; // 攻击决策冷却时间，避免频繁切换
    
    // 尾部攻击相关常量
    private static final int TAIL_ATTACK_COOLDOWN = 60; // 尾部攻击冷却时间（3秒）
    private static final int TAIL_ATTACK_DELAY = 12; // 尾部攻击延迟（12刻后造成伤害）
    private static final int POISON_DURATION = 120; // 中毒效果持续时间（6秒）
    private static final int POISON_AMPLIFIER = 1; // 中毒效果等级（2级）
    
    public GiantScorpion(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        xpReward = 10;
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // 在客户端更新动画状态
        if (this.level().isClientSide()) {
            setupAnimationStates();
        } else {
            // 在服务端处理攻击决策和冷却
            if (tailAttackCooldown > 0) {
                tailAttackCooldown--;
            }
            
            if (attackDecisionCooldown > 0) {
                attackDecisionCooldown--;
            }
            
            if (this.getTarget() != null && !this.isAttacking() && !this.isTailAttacking() && attackDecisionCooldown <= 0) {
                // 检查是否可以执行攻击
                LivingEntity target = this.getTarget();
                double distanceToTargetSqr = this.distanceToSqr(target);

                // 决定使用哪种攻击，并设置决策冷却避免频繁切换
                if (this.random.nextFloat() < 0.4f && tailAttackCooldown <= 0 && distanceToTargetSqr < 25.0) {
                    // 40% 概率使用尾部攻击，且满足距离和冷却条件
                    this.setTailAttackingDecision(true);
                    tailAttackCooldown = TAIL_ATTACK_COOLDOWN + this.random.nextInt(20);
                    attackDecisionCooldown = 40; // 2秒决策冷却
                } else if (!this.isTailAttackingDecision()) {
                    // 只有在当前不是尾部攻击决策时才清除，避免频繁切换
                    this.clearTailAttackingDecision();
                    attackDecisionCooldown = 20; // 1秒决策冷却
                }
            } else if (this.getTarget() == null) {
                // 如果没有目标，清除所有攻击决策
                this.clearTailAttackingDecision();
                attackDecisionCooldown = 0;
            }
            
            // 检查是否应该捡起武器
            if (!this.level().isClientSide() && this.getMainHandItem().isEmpty()) {
                checkAndPickupItems();
            }
        }
    }
    
    /**
     * 检查并捡起附近的武器
     */
    private void checkAndPickupItems() {
        // 搜索附近的物品实体
        for (ItemEntity itemEntity : this.level().getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(2.0D))) {
            ItemStack itemStack = itemEntity.getItem();
            
            // 检查物品是否是武器且伤害值大于自身伤害的25%
            if (isWeapon(itemStack)) {
                // 捡起物品
                this.setItemInHand(InteractionHand.MAIN_HAND, itemStack.copy());
                itemEntity.discard(); // 移除世界中的物品实体
                break;
            }
        }
    }
    
    /**
     * 判断物品是否是武器且伤害值大于自身伤害的25%
     */
    private boolean isWeapon(ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        
        if (itemStack.getItem().isEdible()) return false; // 排除食物
        final AtomicReference<Float> itemDamageRef = new AtomicReference<>(0f);
        
        itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND).forEach((attribute, modifier) -> {
            if (attribute.equals(Attributes.ATTACK_DAMAGE)) {
                // 原子性更新值
                itemDamageRef.updateAndGet(v -> v + (float)modifier.getAmount());
            }
        });
        
        // 检查武器伤害是否大于自身伤害的25%
        double myDamage = this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        return itemDamageRef.get() > (myDamage * 0.25);
    }
    
    /**
     * 设置实体属性
     */
    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.MAX_HEALTH, 80.0);
    }
    
    /**
     * 设置实体类型为节肢生物
     */
    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }
    
    /**
     * 处理实体受伤逻辑
     * 免疫中毒，在狂沙之咒事件范围内时免疫火焰
     */
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        // 在狂沙之咒事件范围内免疫火焰伤害
        if (pSource.is(DamageTypeTags.IS_FIRE)) {
            // 检查是否在事件范围内
            if (this.level() instanceof ServerLevel) {
                CurseOfDesertEvent event = CurseOfDesertEventHandler.getCurrentEvent();
                if (event != null && event.isActive()) {
                    BlockPos eventCenter = event.getCenter();
                    // 在事件范围内免疫火焰伤害
                    if (this.blockPosition().distSqr(eventCenter) <= 2500) { // 50 * 50 = 2500
                        return false;
                    }
                }
            }
        }
        
        return super.hurt(pSource, pAmount);
    }
    
    /**
     * 注册AI目标
     */
    @Override
    protected void registerGoals() {
        // 添加巨蝎专用攻击目标
        this.goalSelector.addGoal(1, new GiantScorpionAttackGoal(this, 1.0, false));
        
        // 添加武器拾取目标
        this.goalSelector.addGoal(2, new GiantScorpionPickupWeaponGoal(this, 1.2));
        
        // 添加基本移动和观察目标
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        
        // 添加目标选择器
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }
    
    /**
     * 设置动画状态
     */
    private void setupAnimationStates() {
        // 设置尾部待机动画，始终播放
        if (!this.tailIdleAnimationState.isStarted()) {
            this.tailIdleAnimationState.start(this.tickCount);
        }
        
        // 处理攻击动画 - 根据是否有武器选择不同动画
        if (this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 14; // 攻击动画持续时间
            // 根据主手是否有武器选择动画
            if (this.getMainHandItem().isEmpty()) {
                // 无武器攻击动画 (attack_noweapon)
                attackAnimationState.start(this.tickCount);
            } else {
                // 有武器攻击动画 (attack_weapon)
                attackAnimationState.start(this.tickCount);
            }
        } else {
            --this.attackAnimationTimeout;
        }
        
        if (!this.isAttacking()) {
            attackAnimationState.stop();
        }
        
        // 处理尾部攻击动画
        if (this.isTailAttacking() && tailAttackAnimationTimeout <= 0) {
            tailAttackAnimationTimeout = 18; // 尾部攻击动画持续时间
            tailAttackAnimationState.start(this.tickCount);
        } else {
            --this.tailAttackAnimationTimeout;
        }
        
        if (!this.isTailAttacking()) {
            tailAttackAnimationState.stop();
        }
    }
    
    /**
     * 更新行走动画
     */
    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if (this.getPose() == Pose.STANDING) {
            f = Math.min(pPartialTick * 6F, 1f);
        } else {
            f = 0f;
        }
        this.walkAnimation.update(f, 0.2f);
    }
    
    /**
     * 判断实体是否正在移动
     */
    public boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
    }
    
    /**
     * 设置攻击状态
     */
    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }
    
    /**
     * 获取攻击状态
     */
    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }
    
    /**
     * 设置尾部攻击状态
     */
    public void setTailAttacking(boolean attacking) {
        this.entityData.set(TAIL_ATTACKING, attacking);
    }
    
    /**
     * 获取尾部攻击状态
     */
    public boolean isTailAttacking() {
        return this.entityData.get(TAIL_ATTACKING);
    }

    /**
     * 设置尾部攻击决策状态
     */
    public void setTailAttackingDecision(boolean attacking) {
        this.entityData.set(TAIL_ATTACKING_DECISION, attacking);
    }

    /**
     * 获取尾部攻击决策状态
     */
    public boolean isTailAttackingDecision() {
        return this.entityData.get(TAIL_ATTACKING_DECISION);
    }

    /**
     * 清除尾部攻击决策状态
     */
    public void clearTailAttackingDecision() {
        this.entityData.set(TAIL_ATTACKING_DECISION, false);
    }
    
    /**
     * 执行尾部攻击
     */
    public void performTailAttack() {
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            // 造成37.5%的基础伤害（原75%的一半）
            float damage = (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.375);
            if (target.hurt(this.damageSources().mobAttack(this), damage)) {
                // 添加中毒效果
                target.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION, POISON_AMPLIFIER));
            }
        }
    }
    
    /**
     * 执行近战攻击
     */
    @Override
    public boolean doHurtTarget(Entity pEntity) {
        if (!(pEntity instanceof LivingEntity)) {
            return false;
        }
        
        LivingEntity target = (LivingEntity) pEntity;
        
        // 检查是否有武器
        ItemStack weapon = this.getMainHandItem();
        if (!weapon.isEmpty()) {
            // 有武器时造成37.5%基础伤害+武器伤害的一半
            float baseDamage = (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.375);
            
            // 使用 AtomicReference 包装武器伤害值
            final AtomicReference<Float> weaponDamageRef = new AtomicReference<>(0f);

            weapon.getAttributeModifiers(EquipmentSlot.MAINHAND).forEach((attribute, modifier) -> {
            if (attribute.equals(Attributes.ATTACK_DAMAGE)) {
                weaponDamageRef.updateAndGet(currentDamage -> currentDamage + (float)modifier.getAmount());
    }
});

// 获取最终武器伤害值并减半
float weaponDamage = weaponDamageRef.get() * 0.5f;
            // 造成总伤害
            return target.hurt(this.damageSources().mobAttack(this), baseDamage + weaponDamage);
        } else {
            // 无武器时造成50%伤害
            float damage = (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5);
            return target.hurt(this.damageSources().mobAttack(this), damage);
        }
    }
    
    /**
     * 决定攻击方式
     * 在攻击前调用此方法来决定使用哪种攻击方式
     */
    public void decideAttackType() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }
        
        double distanceToTarget = this.distanceToSqr(target);
        
        // 攻击方式决策逻辑
        // 尾部攻击优先级较低，只在特定条件下使用
        boolean shouldUseTailAttack = false;
        
        // 如果目标距离较远，或者随机概率（20%），使用尾部攻击
        if (distanceToTarget > 9.0 || (this.random.nextFloat() < 0.2f && distanceToTarget > 4.0)) {
            shouldUseTailAttack = true;
        }
        
        // 设置攻击决策
        this.setTailAttackingDecision(shouldUseTailAttack);
    }
    
    /**
     * 定义同步数据
     */
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
        this.entityData.define(TAIL_ATTACKING, false);
        this.entityData.define(TAIL_ATTACKING_DECISION, false); // 初始化新的同步数据
    }
    
    /**
     * 免疫中毒效果
     */
    @Override
    public boolean canBeAffected(MobEffectInstance pEffectInstance) {
        return pEffectInstance.getEffect() != MobEffects.POISON && super.canBeAffected(pEffectInstance);
    }
}