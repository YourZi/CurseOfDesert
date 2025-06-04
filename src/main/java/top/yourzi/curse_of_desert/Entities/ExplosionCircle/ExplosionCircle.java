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
    private final float maxDamage = 16.0F;
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
        
        // 跟随目标移动
        if (this.entityData.get(FOLLOWING_TARGET) && target != null && target.isAlive()) {
            Vec3 targetPos = new Vec3(target.getX(), target.getY(), target.getZ());
            Vec3 currentPos = this.position();
            Vec3 movement = targetPos.subtract(currentPos).normalize().scale(0.3);
            this.setDeltaMovement(movement);
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }
        
        // 2秒后停止跟随
        if (timer <= maxLifeTime - followTime && this.entityData.get(FOLLOWING_TARGET)) {
            this.entityData.set(FOLLOWING_TARGET, false);
        }
        
        // 生成粒子效果
        if (this.level() instanceof ServerLevel serverLevel) {
            double circleRadius = 1.0;
            int particleCount = 16;
            
            for (int i = 0; i < particleCount; i++) {
                double angle = 2 * Math.PI * i / particleCount;
                double offsetX = Math.cos(angle) * circleRadius;
                double offsetZ = Math.sin(angle) * circleRadius;
                
                serverLevel.sendParticles(
                    ParticleTypes.FLAME,
                    this.getX() + offsetX, 
                    this.getY() + 0.1, 
                    this.getZ() + offsetZ,
                    1, 0.0D, 0.0D, 0.0D, 0.0D
                );
            }
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
                this.getX(), this.getY() + 0.5, this.getZ(),
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