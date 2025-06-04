package top.yourzi.curse_of_desert.Entities.ExplosionCircle;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import top.yourzi.curse_of_desert.init.ModTags;

import java.util.List;

public class ExplosionCircle extends Entity {
    private static final EntityDataAccessor<Integer> EXPLOSION_TIMER = 
            SynchedEntityData.defineId(ExplosionCircle.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FOLLOWING_TARGET = 
            SynchedEntityData.defineId(ExplosionCircle.class, EntityDataSerializers.BOOLEAN);
    
    private LivingEntity owner;
    private LivingEntity target;
    private final float explosionRadius = 2.0F;
    private final float maxDamage = 10.0F;
    private final int maxLifeTime = 55;
    private final int followTime = 40;
    
    public ExplosionCircle(EntityType<? extends ExplosionCircle> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }
    
    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }
    
    public void setTarget(LivingEntity target) {
        this.target = target;
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.define(EXPLOSION_TIMER, maxLifeTime);
        this.entityData.define(FOLLOWING_TARGET, true);
    }
    
    /**
     * 获取爆炸计时器的当前值
     */
    public int getExplosionTimer() {
        return this.entityData.get(EXPLOSION_TIMER);
    }
    
    /**
     * 获取最大生命周期
     */
    public int getMaxLifeTime() {
        return this.maxLifeTime;
    }
    
    // 添加一个字段来存储当前的目标位置，用于平滑移动
    private Vec3 smoothTargetPos = Vec3.ZERO;
    // 添加一个字段来存储上一帧的移动向量，用于实现惯性
    private Vec3 previousMovement = Vec3.ZERO;
    // 添加一个计数器，用于控制每两刻计算一次移动向量
    private int movementCalculationTicks = 0;
    
    @Override
    public void tick() {
        super.tick();
        
        // 如果拥有者死亡，则实体消失
        if (owner != null && !owner.isAlive()) {
            this.discard();
            return;
        }
        
        // 更新计时器
        int timer = this.entityData.get(EXPLOSION_TIMER);
        this.entityData.set(EXPLOSION_TIMER, timer - 1);
        
        // 生成火焰圆环粒子
        if (this.level() instanceof ServerLevel serverLevel) {
            float radius = 1.5F;
            int particleCount = 16; // 粒子数量，决定圆环的密度
            
            for (int i = 0; i < particleCount; i++) {
                double angle = 2.0 * Math.PI * i / particleCount;
                double x = this.getX() + radius * Math.cos(angle);
                double z = this.getZ() + radius * Math.sin(angle);
                
                // 添加一些随机性，使圆环看起来更自然
                double offsetX = (this.random.nextDouble() - 0.5) * 0.2;
                double offsetY = (this.random.nextDouble() - 0.5) * 0.2;
                double offsetZ = (this.random.nextDouble() - 0.5) * 0.2;
                
                serverLevel.sendParticles(
                    ParticleTypes.FLAME,
                    x, this.getY(), z,
                    1,
                    offsetX, offsetY + 0.1, offsetZ,
                    0.002
                );
            }
        }
        
        // 跟随目标移动
        if (this.entityData.get(FOLLOWING_TARGET) && target != null && target.isAlive()) {
            // 更新移动计算计数器
            movementCalculationTicks++;
            
            // 每两刻计算一次移动向量
            if (movementCalculationTicks >= 2) {
                movementCalculationTicks = 0;
                
                // 获取目标位置
                Vec3 targetPos = new Vec3(target.getX(), target.getY(), target.getZ());
                Vec3 currentPos = this.position();
                
                // 平滑插值计算目标位置
                if (smoothTargetPos.equals(Vec3.ZERO)) {
                    // 第一次初始化
                    smoothTargetPos = targetPos;
                } else {
                    // 平滑插值，使目标位置逐渐接近实际目标位置
                    float smoothFactor = 0.2F; // 平滑因子，值越小移动越平滑
                    smoothTargetPos = smoothTargetPos.lerp(targetPos, smoothFactor);
                }
                
                // 计算移动向量
                Vec3 direction = smoothTargetPos.subtract(currentPos);
                double distance = direction.length();
                
                // 获取目标当前移速的80%
                Vec3 targetMovement = target.getDeltaMovement();
                double targetSpeed = targetMovement.length();
                
                // 如果目标几乎不动，设置一个最小速度
                if (targetSpeed < 0.05) {
                    targetSpeed = 0.05;
                }
                
                Vec3 movement;
                
                if (distance > 0.05) { // 只有当距离足够大时才移动
                    movement = direction.normalize().scale(targetSpeed);
                    
                    // 添加惯性，保留一部分之前的速度
                    float inertiaFactor = 0.4F; // 惯性因子
                    movement = movement.scale(1 - inertiaFactor).add(previousMovement.scale(inertiaFactor));
                    
                    this.setDeltaMovement(movement);
                    this.move(MoverType.SELF, this.getDeltaMovement());
                    
                    // 保存当前移动向量用于下一帧
                    previousMovement = movement;
                } else {
                    // 距离很小时，减小移动速度
                    this.setDeltaMovement(Vec3.ZERO);
                    previousMovement = previousMovement.scale(0.5);
                }
            } else {
                // 非计算帧，继续使用之前的移动向量
                this.move(MoverType.SELF, this.getDeltaMovement());
            }
        } else {
            this.setDeltaMovement(Vec3.ZERO);
            previousMovement = Vec3.ZERO;
        }
        
        // 2秒后停止跟随
        if (timer <= maxLifeTime - followTime && this.entityData.get(FOLLOWING_TARGET)) {
            this.entityData.set(FOLLOWING_TARGET, false);
        }
        
        // 爆炸
        if (timer <= 0) {
            explode();
            this.discard();
        }
    }
    
    private void explode() {
        if (this.level().isClientSide()) {
            return;
        }
        
        // 播放爆炸音效
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), 
                SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0F, 1.0F);
        
        // 生成爆炸粒子
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                ParticleTypes.EXPLOSION_EMITTER,
                this.getX(), this.getY() + 0.2, this.getZ(),
                1, 0.0D, 0.0D, 0.0D, 0.0D
            );
        }
        
        // 对范围内的实体造成伤害
        List<LivingEntity> entities = this.level().getEntitiesOfClass(
            LivingEntity.class,
            this.getBoundingBox().inflate(explosionRadius),
            entity -> entity != owner && !entity.getType().is(ModTags.CURSE_OF_DESERT)
        );
        
        for (LivingEntity entity : entities) {
            // 计算距离
            double distance = entity.distanceTo(this);
            
            // 根据距离计算伤害衰减
            if (distance <= explosionRadius) {
                float damageMultiplier = 1.0F - (float)(distance / explosionRadius);
                float damage = maxDamage * damageMultiplier;
                
                // 造成伤害
                entity.hurt(this.damageSources().indirectMagic(this, owner), damage);
            }
        }
    }
    
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("ExplosionTimer")) {
            this.entityData.set(EXPLOSION_TIMER, tag.getInt("ExplosionTimer"));
        }
        if (tag.contains("FollowingTarget")) {
            this.entityData.set(FOLLOWING_TARGET, tag.getBoolean("FollowingTarget"));
        }
    }
    
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("ExplosionTimer", this.entityData.get(EXPLOSION_TIMER));
        tag.putBoolean("FollowingTarget", this.entityData.get(FOLLOWING_TARGET));
    }
}