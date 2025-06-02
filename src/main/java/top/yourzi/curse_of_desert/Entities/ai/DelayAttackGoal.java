package top.yourzi.curse_of_desert.Entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

/**
 * 延迟攻击AI目标的抽象基类
 * 提供了带有动画控制的延迟攻击机制
 */
public abstract class DelayAttackGoal extends MeleeAttackGoal {
    // 攻击动画开始的延迟时间
    protected int attackDelay;
    // 距离下一次攻击的剩余时间
    protected int ticksUntilNextAttack;
    // 实体对象
    protected final PathfinderMob entity;

    public DelayAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        this.entity = pMob;
        this.attackDelay = getDefaultAttackDelay();
        this.ticksUntilNextAttack = getDefaultAttackDelay();
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else if (!this.mob.canAttack(livingentity)) {
            return false;
        } else {
            this.mob.getNavigation().moveTo(livingentity, 1.0);
            return true;
        }
    }

    /**
     * 获取默认的攻击延迟时间
     * @return 默认攻击延迟（游戏刻）
     */
    protected abstract int getDefaultAttackDelay();

    /**
     * 设置实体的攻击状态
     * @param attacking 是否正在攻击
     */
    protected abstract void setEntityAttacking(boolean attacking);

    /**
     * 重置实体的攻击动画超时
     */
    protected abstract void resetEntityAnimationTimeout();

    @Override
    public void start() {
        super.start();
        this.attackDelay = getDefaultAttackDelay();
        this.ticksUntilNextAttack = getDefaultAttackDelay();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr) {
        if (isEnemyWithinAttackDistance(pEnemy, pDistToEnemySqr)) {
            if (this.ticksUntilNextAttack <= 0) { // 攻击冷却结束
                // 触发攻击动画
                setEntityAttacking(true); 
                // 实际的伤害判定和动画结束后的状态重置将由子类的tick方法或者特定的伤害判定tick处理
                // 基类只负责触发攻击状态和重置冷却
                this.mob.getLookControl().setLookAt(pEnemy.getX(), pEnemy.getEyeY(), pEnemy.getZ());
                // 移动到子类的tick中，在动画的特定帧执行
        // 移动到子类的tick中，在动画结束后执行
                resetAttackCooldown(); // 重置攻击冷却
            }
        } else {
            // 当敌人离开攻击范围时，重置攻击状态和动画
            if (this.mob.isAlive()) { // 确保实体存活才重置
                setEntityAttacking(false);
                resetEntityAnimationTimeout();
            }
            // 即使敌人离开范围，攻击冷却也应该继续减少，而不是立即重置，除非AI目标被重置
            // 移除此处的冷却重置，让tick方法自然减少
        }
    }

    /**
     * 判断敌人是否在攻击范围内
     * @param pEnemy 目标实体
     * @param pDistToEnemySqr 到目标的距离平方
     * @return 是否在攻击范围内
     */
    protected boolean isEnemyWithinAttackDistance(LivingEntity pEnemy, double pDistToEnemySqr) {
        double attackReachSqr = this.getAttackReachSqr(pEnemy);
        return pDistToEnemySqr <= attackReachSqr;
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

    // 移除 isTimeToStartAttackAnimation，因为攻击动画的开始现在由 isTimeToAttack 触发


    /**
     * 执行实际的攻击
     * 重置攻击冷却并对目标造成伤害
     * @param pEnemy 目标实体
     */
    protected abstract void performAttack(LivingEntity pEnemy);

    @Override
    public void tick() {
        // 先检查并执行攻击，再递减冷却时间
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            this.checkAndPerformAttack(livingentity, this.mob.distanceToSqr(livingentity));
        }

        super.tick(); // 调用父类的tick方法，处理移动等逻辑
        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
    }

    /**
     * 当AI目标停止时调用
     * 重置攻击状态
     */
    @Override
    public void stop() {
        setEntityAttacking(false);
        super.stop();
    }
}