package top.yourzi.curse_of_desert.Entities.Mummy;


import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import top.yourzi.curse_of_desert.AttackEvent.CurseOfDesertEvent;
import top.yourzi.curse_of_desert.Entities.ai.MummyAttackGoal;
import top.yourzi.curse_of_desert.init.ModSounds;
import top.yourzi.curse_of_desert.init.ModTags;

public class Mummy extends Zombie {
    public Mummy(EntityType<? extends Mummy> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        xpReward = 4;
    }

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(Mummy.class, EntityDataSerializers.BOOLEAN);


    public final AnimationState idle = new AnimationState();
    public final AnimationState walk = new AnimationState();
    public final AnimationState walk_with_bottle = new AnimationState();
    public final AnimationState attack = new AnimationState();
    public final AnimationState throwbottle = new AnimationState();

    private int idleAnimationTimeout = 0;
    public int attackAnimationTimeout = 0;
    public boolean shouldPlayAttackAnimation = false;

    @Override
    public void tick() {
        super.tick();

        if(this.level().isClientSide()) {
            setupAnimationStates();
        }
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, (double)50.0F)
                .add(Attributes.MOVEMENT_SPEED, (double)0.23F)
                .add(Attributes.ATTACK_DAMAGE, (double)5.0F)
                .add(Attributes.ARMOR, (double)2.0F)
                .add(Attributes.MAX_HEALTH, (double)30.0F)
                .add(Attributes.ATTACK_KNOCKBACK, (double)0.5F)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, (double)0.0F);

    }

    @Override
    public boolean canHoldItem(ItemStack pStack) {
        return super.canHoldItem(pStack);
    }

    @Override
    public boolean wantsToPickUp(ItemStack pStack) {
        return pStack.is(Items.GLOW_INK_SAC) ? false : super.wantsToPickUp(pStack);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    protected boolean isSunSensitive() {
        // 检查是否在事件范围内
        if (this.level() instanceof ServerLevel serverLevel) {
            CurseOfDesertEvent event = top.yourzi.curse_of_desert.Events.CurseOfDesertEventHandler.getCurrentEvent();
            if (event != null && event.isActive()) {
                BlockPos eventCenter = event.getCenter();
                // 检查是否在事件范围内（50格范围）
                return this.blockPosition().distSqr(eventCenter) > 2500; // 50 * 50 = 2500
            }
        }
        return true;
    }

    @Override
    public boolean fireImmune() {
        // 检查是否在事件范围内
        if (this.level() instanceof ServerLevel serverLevel) {
            CurseOfDesertEvent event = top.yourzi.curse_of_desert.Events.CurseOfDesertEventHandler.getCurrentEvent();
            if (event != null && event.isActive()) {
                BlockPos eventCenter = event.getCenter();
                // 检查是否在事件范围内（50格范围）
                return this.blockPosition().distSqr(eventCenter) <= 2500; // 50 * 50 = 2500
            }
        }
        return super.fireImmune();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof ZombieAttackGoal);
        updateAttackGoals();
    }

    private void updateAttackGoals() {
        this.goalSelector.addGoal(1, new MummyAttackGoal(this, 1.0D, true));
    }

    private void setupAnimationStates() {
        if(this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 152;
            this.idle.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        if(this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 12;
            attack.start(this.tickCount);
        } else {
            --this.attackAnimationTimeout;
        }
    }

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if(this.getPose() == Pose.STANDING) {
            f = Math.min(pPartialTick * 6F, 1f);
        } else {
            f = 0f;
        }
        this.walkAnimation.update(f, 0.2f);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }


    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
    }


    private void resetAnimations() {
        this.idle.stop();
        this.walk.stop();
        this.attack.stop();
    }

    @Override
    public boolean isUnderWaterConverting() {
        return false;
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public boolean killedEntity(ServerLevel pLevel, LivingEntity pEntity) {
        boolean flag = super.killedEntity(pLevel, pEntity);
        return flag;
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        if (pEntity instanceof LivingEntity livingEntity) {
            // 检查攻击者是否带有沙漠诅咒事件标签
            if (livingEntity.getType().is(ModTags.CURSE_OF_DESERT)) {
                return false; // 如果攻击者带有标签，不进行反击
            }
        }
        return super.doHurtTarget(pEntity);
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void randomizeReinforcementsChance() {
        this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.random.nextDouble() * 0.04);
    }


    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.MUMMY_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return ModSounds.MUMMY_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MUMMY_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getStepSound() {
        return ModSounds.MUMMY_STEP.get();
    }


}
