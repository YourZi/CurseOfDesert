package top.yourzi.curse_of_desert.Entities.DogHeadedPriests;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import top.yourzi.curse_of_desert.Entities.ai.DogHeadedPriestsAttackGoal;
import top.yourzi.curse_of_desert.Events.CurseOfDesertEventHandler;
import top.yourzi.curse_of_desert.AttackEvent.CurseOfDesertEvent;

public class DogHeadedPriests extends Zombie {
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(DogHeadedPriests.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HEALING =
            SynchedEntityData.defineId(DogHeadedPriests.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState idle = new AnimationState();
    public final AnimationState walk = new AnimationState();
    public final AnimationState attack = new AnimationState();
    public final AnimationState heal = new AnimationState();

    public int attackAnimationTimeout = 0;
    public int healAnimationTimeout = 0;

    public DogHeadedPriests(EntityType<? extends Zombie> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        xpReward = 6;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.level().isClientSide()) {
            setupAnimationStates();
        }
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, 16.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.3F)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof ZombieAttackGoal);
        this.goalSelector.addGoal(1, new DogHeadedPriestsAttackGoal(this, 1.0D, true));
    }


    @Override
    public boolean fireImmune() {
        CurseOfDesertEvent currentEvent = CurseOfDesertEventHandler.getCurrentEvent();
        if (currentEvent != null && currentEvent.isActive()) {
            return this.blockPosition().distSqr(currentEvent.getCenter()) <= 2500;
        }
        return false;
    }

    private void setupAnimationStates() {
        if(this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 20;
            attack.start(this.tickCount);
        } else {
            --this.attackAnimationTimeout;
        }

        if(this.isHealing() && healAnimationTimeout <= 0) {
            healAnimationTimeout = 30;
            heal.start(this.tickCount);
        } else {
            --this.healAnimationTimeout;
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

    public void setHealing(boolean healing) {
        this.entityData.set(HEALING, healing);
    }

    public boolean isHealing() {
        return this.entityData.get(HEALING);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
        this.entityData.define(HEALING, false);
    }
}