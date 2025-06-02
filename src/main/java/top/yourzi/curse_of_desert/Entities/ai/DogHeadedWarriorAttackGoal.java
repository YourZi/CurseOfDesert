package top.yourzi.curse_of_desert.Entities.ai;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import top.yourzi.curse_of_desert.Entities.DogHeadedWarrior.DogHeadedWarrior;

public class DogHeadedWarriorAttackGoal extends DelayAttackGoal {
    private final DogHeadedWarrior dogHeadedWarrior;
    private int attackAnimationTicks = 0;
    private boolean isAttacking = false;

    private static final int DAMAGE_TICK = 8;

    public DogHeadedWarriorAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        this.dogHeadedWarrior = ((DogHeadedWarrior) pMob);
    }

    @Override
    protected int getDefaultAttackDelay() {
        return 11;
    }
    
    @Override
    protected void setEntityAttacking(boolean attacking) {
        this.dogHeadedWarrior.setAttacking(attacking);
        this.isAttacking = attacking;
        if (!attacking) {
            this.attackAnimationTicks = 0;
        }
    }
    
    @Override
    protected void resetEntityAnimationTimeout() {
        this.dogHeadedWarrior.attackAnimationTimeout = 0;
    }

    @Override
    protected double getAttackReachSqr(LivingEntity pAttackTarget) {
        return this.mob.getBbWidth() * 3.0F * this.mob.getBbWidth() * 4.0F + pAttackTarget.getBbWidth();
    }

    @Override
    protected void performAttack(LivingEntity pEnemy) {}

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.dogHeadedWarrior.getTarget();

        if (this.isAttacking) {
            this.attackAnimationTicks++;

            if (this.attackAnimationTicks == DAMAGE_TICK && target != null && target.isAlive()) {
                if (this.mob.getSensing().hasLineOfSight(target)) {
                     this.mob.doHurtTarget(target);
                     this.mob.level().playSound(null, this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                         SoundEvents.PLAYER_ATTACK_SWEEP,
                         SoundSource.HOSTILE, 1.0F, 1.0F);
                }
            }

            if (this.attackAnimationTicks >= getDefaultAttackDelay()) {
                setEntityAttacking(false);
                this.resetAttackCooldown();
            }
        } else {
            if (this.ticksUntilNextAttack <= 0 && target != null && this.mob.isWithinMeleeAttackRange(target)) {
            }
        }
    }
}
