package top.yourzi.curse_of_desert.Entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import top.yourzi.curse_of_desert.Entities.Mummy.Mummy;

public class MummyAttackGoal extends MeleeAttackGoal {
    private final Mummy entity;
    // 攻击动画开始的延迟时间
    private int attackDelay = 6;
    // 距离下一次攻击的剩余时间
    private int ticksUntilNextAttack = 6;

    public MummyAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        entity = ((Mummy) pMob);
    }

    @Override
    public void start() {
        super.start();
        attackDelay = 6;
        ticksUntilNextAttack = 6;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr) {
        if (isEnemyWithinAttackDistance(pEnemy, pDistToEnemySqr)) {

            // 当距离下一次攻击的时间小于等于攻击延迟时，开始播放攻击动画
            if(isTimeToStartAttackAnimation()) {
                entity.setAttacking(true);
            }
            // 当冷却时间结束时，执行实际的攻击
            if(isTimeToAttack()) {
                this.mob.getLookControl().setLookAt(pEnemy.getX(), pEnemy.getEyeY(), pEnemy.getZ());
                performAttack(pEnemy);
                entity.setAttacking(false);
            }
        } else {
            // 当敌人离开攻击范围时，重置所有状态
            resetAttackCooldown();
            entity.setAttacking(false);
            entity.attackAnimationTimeout = 0;
        }
    }

    private boolean isEnemyWithinAttackDistance(LivingEntity pEnemy, double pDistToEnemySqr) {
        double attackReachSqr = this.getAttackReachSqr(pEnemy);
        return pDistToEnemySqr <= attackReachSqr;
    }

    @Override
    protected double getAttackReachSqr(LivingEntity pAttackTarget) {
        return this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 4.0F + pAttackTarget.getBbWidth();
    }

    /**
     * 重置攻击冷却时间
     * 将下一次攻击的等待时间设置为攻击延迟的两倍
     */
    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(attackDelay * 2);
    }


    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }


    protected boolean isTimeToStartAttackAnimation() {
        return this.ticksUntilNextAttack <= attackDelay;
    }


    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }


    /**
     * 执行实际的攻击
     * 重置攻击冷却并对目标造成伤害
     * @param pEnemy 目标实体
     */
    protected void performAttack(LivingEntity pEnemy) {
        this.resetAttackCooldown();
        this.mob.doHurtTarget(pEnemy);
    }


    @Override
    public void tick() {
        super.tick();
        System.out.println("ticksUntilNextAttack: " + this.ticksUntilNextAttack);
        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
    }

    /**
     * 当AI目标停止时调用
     * 重置攻击状态
     */
    @Override
    public void stop() {
            entity.setAttacking(false);
            super.stop();
    }

}
