package top.yourzi.curse_of_desert.Entities.ai;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import top.yourzi.curse_of_desert.Entities.DogHeadedPriests.DogHeadedPriests;
import top.yourzi.curse_of_desert.Entities.ExplosionCircle.ExplosionCircle;
import top.yourzi.curse_of_desert.init.ModEntities;

import java.util.EnumSet;

public class DogHeadedPriestsAttackCircleGoal extends Goal {
    private final DogHeadedPriests entity;
    private LivingEntity target;
    private int attackCooldown = 0;
    private int castingTime = 0;
    private final int maxCastingTime = 30; // 1.5秒的施法时间
    private final int cooldownTime = 100; // 5秒冷却时间
    private boolean isCasting = false;
    private static final double ATTACK_RANGE = 16.0D; // 攻击范围
    
    public DogHeadedPriestsAttackCircleGoal(DogHeadedPriests pMob) {
        this.entity = pMob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }
    
    @Override
    public boolean canUse() {
        // 如果正在治疗或者冷却中，则不能使用
        if (this.entity.isHealing() || this.entity.isAttacking() || this.attackCooldown > 0) {
            return false;
        }
        
        // 获取攻击目标
        this.target = this.entity.getTarget();
        if (this.target == null || !this.target.isAlive()) {
            return false;
        }
        
        // 检查目标是否在攻击范围内
        return this.entity.distanceToSqr(this.target) <= ATTACK_RANGE * ATTACK_RANGE;
    }
    
    @Override
    public boolean canContinueToUse() {
        return this.isCasting && this.castingTime < this.maxCastingTime && this.entity.isAttacking();
    }
    
    @Override
    public void start() {
        this.isCasting = true;
        this.castingTime = 0;
        this.entity.setAttacking(true);
        this.entity.getNavigation().stop();
        
        // 播放施法音效
        this.entity.level().playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(),
                SoundEvents.EVOKER_PREPARE_ATTACK, SoundSource.HOSTILE, 1.0F, 1.0F);
    }
    
    @Override
    public void stop() {
        this.entity.setAttacking(false);
        this.isCasting = false;
        this.attackCooldown = this.cooldownTime;
    }
    
    @Override
    public void tick() {
        if (this.target == null || !this.target.isAlive()) {
            this.stop();
            return;
        }

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
            return;
        }
        
        // 让狗头人祭司面向目标
        this.entity.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        
        // 增加施法时间
        this.castingTime++;
        
        // 在施法过程中生成粒子效果
        if (this.entity.level() instanceof ServerLevel serverLevel) {
            double entityX = this.entity.getX();
            double entityY = this.entity.getY() + 1.5D;
            double entityZ = this.entity.getZ();
            
            // 生成施法粒子
            for (int i = 0; i < 5; i++) {
                double offsetX = (this.entity.getRandom().nextDouble() - 0.5D) * 0.5D;
                double offsetY = (this.entity.getRandom().nextDouble() - 0.5D) * 0.5D;
                double offsetZ = (this.entity.getRandom().nextDouble() - 0.5D) * 0.5D;
                
                serverLevel.sendParticles(
                    ParticleTypes.FLAME,
                    entityX + offsetX,
                    entityY + offsetY,
                    entityZ + offsetZ,
                    1, 0.0D, 0.0D, 0.0D, 0.0D
                );
            }
        }
        
        // 施法完成，释放爆炸圈
        if (this.castingTime >= this.maxCastingTime) {
            releaseExplosionCircle();
            this.stop();
        }
    }
    
    private void releaseExplosionCircle() {
        if (this.target == null || !this.target.isAlive() || !(this.entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        // 检查目标与地面的距离
        double distanceFromGround = this.target.getY() - this.target.getBlockY();
        
        // 播放释放音效
        this.entity.level().playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(),
                SoundEvents.EVOKER_CAST_SPELL, SoundSource.HOSTILE, 1.0F, 1.0F);
        
        if (distanceFromGround > 4.0D) {
            // 如果目标距离地面超过4格，直接在目标位置释放爆炸
            serverLevel.sendParticles(
                ParticleTypes.EXPLOSION_EMITTER,
                this.target.getX(), this.target.getY(), this.target.getZ(),
                1, 0.0D, 0.0D, 0.0D, 0.0D
            );
            
            // 对目标造成伤害
            this.target.hurt(this.entity.damageSources().indirectMagic(this.entity, this.entity), 16.0F);
        } else {
            // 如果目标距离地面不超过4格，生成爆炸圈实体
            ExplosionCircle explosionCircle = new ExplosionCircle(ModEntities.EXPLOSION_CIRCLE.get(), this.entity.level());
            
            // 设置爆炸圈的位置为目标脚下
            Vec3 targetPos = this.target.position();
            explosionCircle.setPos(targetPos.x, targetPos.y, targetPos.z);
            
            // 设置爆炸圈的所有者和目标
            explosionCircle.setOwner(this.entity);
            explosionCircle.setTarget(this.target);
            
            // 将爆炸圈添加到世界中
            this.entity.level().addFreshEntity(explosionCircle);
        }
    }
    
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}