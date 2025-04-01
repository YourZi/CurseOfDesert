package top.yourzi.curse_of_desert.Entities.DogHeadedWarrior;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import top.yourzi.curse_of_desert.Entities.ai.DogHeadedWarriorAttackGoal;
import top.yourzi.curse_of_desert.init.ModItems;

public class DogHeadedWarrior extends Zombie {
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(DogHeadedWarrior.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState idle = new AnimationState();
    public final AnimationState walk = new AnimationState();
    public final AnimationState attack = new AnimationState();

    public int attackAnimationTimeout = 0;

    public DogHeadedWarrior(EntityType<? extends Zombie> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        xpReward = 4;
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.BRONZE_HAND_AXE.get()));
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
                .add(Attributes.FOLLOW_RANGE, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ATTACK_KNOCKBACK, (double)0.5F)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, (double)0.0F);
    }

    @Override
    public boolean fireImmune() {
        return false;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof ZombieAttackGoal);
        this.goalSelector.addGoal(1, new DogHeadedWarriorAttackGoal(this, 1.0D, true));
    }

    private void setupAnimationStates() {
        if(this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 22;
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
}