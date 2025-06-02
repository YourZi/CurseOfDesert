package top.yourzi.curse_of_desert.Entities.ai;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import top.yourzi.curse_of_desert.Entities.GiantScorpion.GiantScorpion;

/**
 * 巨蝎攻击AI目标
 * 实现两种攻击方式：近战夹击和尾部刺击
 */
public class GiantScorpionAttackGoal extends DelayAttackGoal {
    private final GiantScorpion giantScorpion;
    private int attackAnimationTicks = 0;
    private int tailAttackAnimationTicks = 0;
    private boolean isAttacking = false;
    private boolean isTailAttacking = false;

    private static final int MELEE_DAMAGE_TICK = 10;
    private static final int TAIL_DAMAGE_TICK = 12;
    private static final int MELEE_ATTACK_DURATION = 14;
    private static final int TAIL_ATTACK_DURATION = 18;

    public GiantScorpionAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        this.giantScorpion = ((GiantScorpion) pMob);
    }

    @Override
    protected int getDefaultAttackDelay() {
        return 20;
    }
    
    @Override
    protected void setEntityAttacking(boolean attacking) {
        if (attacking) {
            this.giantScorpion.decideAttackType();
            
            if (this.giantScorpion.isTailAttackingDecision()) {
                this.giantScorpion.setTailAttacking(true);
                this.isTailAttacking = true;
                this.tailAttackAnimationTicks = 0;
            } else {
                this.giantScorpion.setAttacking(true);
                this.isAttacking = true;
                this.attackAnimationTicks = 0;
            }
        } else {
            this.giantScorpion.setAttacking(false);
            this.giantScorpion.setTailAttacking(false);
            this.isAttacking = false;
            this.isTailAttacking = false;
            this.attackAnimationTicks = 0;
            this.tailAttackAnimationTicks = 0;
        }
    }
    
    @Override
    protected void resetEntityAnimationTimeout() {
        this.giantScorpion.attackAnimationTimeout = 0;
        this.giantScorpion.tailAttackAnimationTimeout = 0;
    }

    @Override
    protected double getAttackReachSqr(LivingEntity pAttackTarget) {
        if (this.giantScorpion.isTailAttackingDecision()) {
            double reach = this.mob.getBbWidth() * 2.0F + 3.5F + pAttackTarget.getBbWidth();
            return reach * reach;
        } else {
            double reach = this.mob.getBbWidth() * 2.0F + 2.5F + pAttackTarget.getBbWidth();
            return reach * reach;
        }
    }

    @Override
    protected void performAttack(LivingEntity pEnemy) {
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.giantScorpion.getTarget();

        if (this.isAttacking) {
            this.attackAnimationTicks++;

            if (this.attackAnimationTicks == MELEE_DAMAGE_TICK && target != null && target.isAlive()) {
                if (this.mob.getSensing().hasLineOfSight(target)) {
                    this.giantScorpion.doHurtTarget(target);
                    this.mob.level().playSound(null, this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP,
                        SoundSource.HOSTILE, 1.0F, 1.0F);
                }
            }

            if (this.attackAnimationTicks >= MELEE_ATTACK_DURATION) {
                setEntityAttacking(false);
                this.resetAttackCooldown();
            }
        }
        
        if (this.isTailAttacking) {
            this.tailAttackAnimationTicks++;

            if (this.tailAttackAnimationTicks == TAIL_DAMAGE_TICK && target != null && target.isAlive()) {
                if (this.mob.getSensing().hasLineOfSight(target)) {
                    this.giantScorpion.performTailAttack();
                    this.mob.level().playSound(null, this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                        SoundEvents.SPIDER_HURT,
                        SoundSource.HOSTILE, 1.0F, 0.8F);
                }
            }

            if (this.tailAttackAnimationTicks >= TAIL_ATTACK_DURATION) {
                setEntityAttacking(false);
                this.giantScorpion.clearTailAttackingDecision();
                this.resetAttackCooldown();
            }
        }
    }

    @Override
    public boolean canUse() {
        if (this.isAttacking || this.isTailAttacking) {
            return false;
        }
        return super.canUse();
    }

    @Override
    public void stop() {
        super.stop();
        setEntityAttacking(false);
        this.giantScorpion.clearTailAttackingDecision();
    }
}