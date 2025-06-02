package top.yourzi.curse_of_desert.Entities.ai;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import top.yourzi.curse_of_desert.Entities.Mummy.Mummy;
import top.yourzi.curse_of_desert.init.ModEffect;

public class MummyAttackGoal extends DelayAttackGoal {
    private final Mummy mummy;
    private int attackAnimationTicks = 0;
    private boolean isAttacking = false;

    private static final int ANIMATION_DURATION = 12;
    private static final int DAMAGE_TICK = 6;

    public MummyAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        this.mummy = (Mummy) pMob;
    }

    @Override
    protected int getDefaultAttackDelay() {
        return ANIMATION_DURATION;
    }
    
    @Override
    protected void setEntityAttacking(boolean attacking) {
        this.mummy.setAttacking(attacking);
        this.isAttacking = attacking;
        if (!attacking) {
            this.attackAnimationTicks = 0;
        }
    }

    @Override
    protected void resetEntityAnimationTimeout() {
        this.mummy.attackAnimationTimeout = 0;
    }

    @Override
    protected boolean isEnemyWithinAttackDistance(LivingEntity pEnemy, double pDistToEnemySqr) {
        double attackReach = this.mummy.getMeleeAttackRangeSqr(pEnemy);
        return pDistToEnemySqr <= attackReach * attackReach;
    }

    @Override
    public void tick() {
        super.tick();


        if (isAttacking) {
            attackAnimationTicks++;


            if (attackAnimationTicks == DAMAGE_TICK) {
                LivingEntity target = this.mummy.getTarget();
                if (target != null) {
                    performAttack(target);
                }
            }


            if (attackAnimationTicks >= ANIMATION_DURATION) {
                setEntityAttacking(false);
            }
        }
    }

    @Override
    protected double getAttackReachSqr(LivingEntity pAttackTarget) {
        return this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 3.0F + pAttackTarget.getBbWidth();
    }

    @Override
    protected void performAttack(LivingEntity target) {
        if (target.isAlive()) {
            this.mummy.doHurtTarget(target);


            if (this.mummy.getMainHandItem().isEmpty()) {
                int difficultyValue = this.mummy.level().getDifficulty().getId();
                int duration = difficultyValue * 60;
                if (duration == 0) duration = 60;

                target.addEffect(new MobEffectInstance(ModEffect.ATROPHY.get(), duration, 0, false, true));
            }
        }
    }
}
