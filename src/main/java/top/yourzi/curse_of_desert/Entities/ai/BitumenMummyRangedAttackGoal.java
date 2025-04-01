package top.yourzi.curse_of_desert.Entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import top.yourzi.curse_of_desert.Entities.BitumenBottle.BitumenBottle;
import top.yourzi.curse_of_desert.Entities.BitumenMummy.BitumenMummy;

public class BitumenMummyRangedAttackGoal extends RangedAttackGoal {
    private final BitumenMummy mummy;
    private int attackDelay = 11;
    private int ticksUntilNextAttack = 11;
    
    public BitumenMummyRangedAttackGoal(RangedAttackMob pRangedAttackMob, double pSpeedModifier, int pAttackInterval, float pAttackRadius) {
        super(pRangedAttackMob, pSpeedModifier, pAttackInterval, pAttackRadius);
        this.mummy = (BitumenMummy) pRangedAttackMob;
    }
    
    @Override
    public void start() {
        super.start();
        attackDelay = 11;
        ticksUntilNextAttack = 11;
        mummy.setThrowingBottle(false);
    }
    
    @Override
    public void tick() {
        super.tick();
        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        
        LivingEntity target = this.mummy.getTarget();
        if (target != null && mummy.hasBottle()) {
            double distanceToTargetSqr = this.mummy.distanceToSqr(target);
            checkAndPerformAttack(target, distanceToTargetSqr);
        }
    }
    
    @Override
    public void stop() {
        mummy.setThrowingBottle(false);
        super.stop();
    }
    
    protected void checkAndPerformAttack(LivingEntity pTarget, double pDistanceToTargetSqr) {
        double attackRange = 20.0F;
        if (pDistanceToTargetSqr <= attackRange * attackRange && mummy.hasBottle()) {
            if (isTimeToStartAttackAnimation()) {
                mummy.setThrowingBottle(true);
            }
            if (isTimeToAttack()) {
                performRangedAttack(pTarget);
                resetAttackCooldown();
                mummy.setThrowingBottle(false);
            }
        } else {
            resetAttackCooldown();
            mummy.setThrowingBottle(false);
        }
    }
    
    protected void performRangedAttack(LivingEntity target) {
        BitumenBottle bottle = new BitumenBottle(mummy.level(), mummy);
        double targetY = target.getEyeY() - 1.1D;
        double dx = target.getX() - mummy.getX();
        double dy = targetY - (mummy.getY() + 1.6D); // 从实体眼睛位置发射
        double dz = target.getZ() - mummy.getZ();
        
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double g = 0.05D; // Minecraft中的重力加速度
        
        // 根据目标距离动态调整速度和角度
        double velocity = 0.8D; // 降低基础速度
        double angle = Math.atan2(dy, horizontalDistance);
        
        // 限制角度范围
        angle = Math.max(Math.min(angle, Math.PI / 3), -Math.PI / 3); // 限制在60度范围内
        
        // 根据距离微调速度
        if (horizontalDistance > 10) {
            velocity *= 1.2D;
        }
        
        // 分解速度
        double velocityXZ = velocity * Math.cos(angle);
        double velocityY = velocity * Math.sin(angle);
        
        // 应用速度分量
        bottle.shoot(dx / horizontalDistance * velocityXZ, velocityY,
                    dz / horizontalDistance * velocityXZ, 1.0F, 2.0F);
        
        mummy.level().addFreshEntity(bottle);
        mummy.setHasBottle(false);
    }
    
    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(attackDelay * 2);
    }
    
    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }
    
    protected boolean isTimeToStartAttackAnimation() {
        return this.ticksUntilNextAttack <= attackDelay;
    }
    
    public Mob getMob() {
        return this.mummy;
    }
    }