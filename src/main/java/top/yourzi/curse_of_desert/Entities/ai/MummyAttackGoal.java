package top.yourzi.curse_of_desert.Entities.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import top.yourzi.curse_of_desert.Entities.Mummy.Mummy;
import top.yourzi.curse_of_desert.init.ModEffect;

public class MummyAttackGoal extends MeleeAttackGoal {
    private final Mummy entity;
    private int attackAnimationTicks = 0; // 攻击动画已播放的刻数
    private int attackCooldown = 0;       // 攻击冷却剩余刻数
    private boolean isAttacking = false;  // 是否处于攻击动画中

    private static final int ANIMATION_DURATION = 16; // 动画总时长（16刻）
    private static final int DAMAGE_TICK = 6;         // 动画播放到第6刻时造成伤害
    private static final int COOLDOWN = ANIMATION_DURATION; // 冷却时间与动画时长一致

    public MummyAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        this.entity = (Mummy) pMob;
    }

    @Override
    public void start() {
        super.start();
        resetAttackState();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double distanceToEnemySqr) {
        if (canAttack(target, distanceToEnemySqr)) {
            // 如果不在攻击动画中且冷却结束，启动攻击
            if (!isAttacking && attackCooldown <= 0) {
                startAttackAnimation();
            }
        } else {
            // 目标超出范围时重置攻击状态
            resetAttackState();
        }
    }

    private boolean canAttack(LivingEntity target, double distanceSqr) {
        double attackReach = this.entity.getMeleeAttackRangeSqr(target);
        return distanceSqr <= attackReach * attackReach;
    }

    private void startAttackAnimation() {
        isAttacking = true;
        attackAnimationTicks = 0;
        entity.setAttacking(true); // 触发动画播放
    }

    @Override
    public void tick() {
        super.tick();

        // 更新攻击动画计时
        if (isAttacking) {
            attackAnimationTicks++;

            // 在动画第6刻造成伤害
            if (attackAnimationTicks == DAMAGE_TICK) {
                performAttack();
            }

            // 动画结束后进入冷却
            if (attackAnimationTicks >= ANIMATION_DURATION) {
                resetAttackState();
            }
        }

        // 更新冷却计时
        if (attackCooldown > 0) {
            attackCooldown--;
        }
    }

    private void performAttack() {
        LivingEntity target = this.entity.getTarget();
        if (target != null && target.isAlive()) {
            this.entity.swing(InteractionHand.MAIN_HAND);
            this.entity.doHurtTarget(target);

            // 只有在主手没有物品时才添加萎缩效果
            if (this.entity.getMainHandItem().isEmpty()) {
                // 根据游戏难度计算萎缩效果持续时间
                int difficultyValue = this.entity.level().getDifficulty().getId();
                int duration = 7 * difficultyValue * 20; // 转换为游戏刻

                // 给目标添加萎缩效果
                target.addEffect(new MobEffectInstance(ModEffect.ATROPHY.get(), duration));
            }
        }
    }

    private void resetAttackState() {
        isAttacking = false;
        attackAnimationTicks = 0;
        attackCooldown = COOLDOWN; // 进入冷却
        entity.setAttacking(false); // 停止动画
    }

    @Override
    public void stop() {
        resetAttackState();
        super.stop();
    }
}
