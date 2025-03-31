package top.yourzi.curse_of_desert.Entities.BitumenBottle;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import top.yourzi.curse_of_desert.Entities.Bitumen.Bitumen;
import top.yourzi.curse_of_desert.init.ModEntities;
import top.yourzi.curse_of_desert.init.ModSounds;

public class BitumenBottle extends ThrowableItemProjectile {
    public BitumenBottle(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BitumenBottle(Level pLevel, LivingEntity pShooter) {
        super(ModEntities.BITUMEN_BOTTLE.get(), pShooter, pLevel);
    }

    public BitumenBottle(Level pLevel, double pX, double pY, double pZ) {
        super(ModEntities.BITUMEN_BOTTLE.get(), pX, pY, pZ, pLevel);
    }

    @Override
    protected Item getDefaultItem() {
        return null;
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        // 播放沥青瓶破碎音效
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
            ModSounds.BOTTLE_BREAK.get(),
            net.minecraft.sounds.SoundSource.NEUTRAL, 1.0F, 1.0F);

        if (pResult.getType() == HitResult.Type.ENTITY) {
            net.minecraft.world.phys.EntityHitResult entityHitResult = (net.minecraft.world.phys.EntityHitResult) pResult;
            if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                livingEntity.hurt(this.damageSources().thrown(this, this.getOwner()), 8.0F);
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1, true, false));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 1, true, false));
            }
        }

        // 在碰撞位置生成沥青实体
        if (!this.level().isClientSide) {
            Bitumen bitumen = new Bitumen(ModEntities.BITUMEN.get(), this.level());
            bitumen.setPos(this.getX(), this.getY(), this.getZ());
            this.level().addFreshEntity(bitumen);
        }

        // 生成玻璃破碎和黑色粒子效果
                    for(int i = 0; i < 50; ++i) {
                        this.level().addParticle(
                            new net.minecraft.core.particles.BlockParticleOption(
                                net.minecraft.core.particles.ParticleTypes.BLOCK,
                                net.minecraft.world.level.block.Blocks.GLASS.defaultBlockState()),
                            this.getX(), this.getY() + 1.0D, this.getZ(),
                            ((double)this.random.nextFloat() - 0.5D) * 0.8D,
                            ((double)this.random.nextFloat() - 0.5D) * 0.6D,
                            ((double)this.random.nextFloat() - 0.5D) * 0.8D);
                    }
                    for(int i = 0; i < 40; ++i) {
                        this.level().addParticle(
                            net.minecraft.core.particles.ParticleTypes.SMOKE,
                            this.getX() + ((double)this.random.nextFloat() - 0.5D) * 0.8D,
                            this.getY() + 1.0D + ((double)this.random.nextFloat() - 0.5D) * 0.4D,
                            this.getZ() + ((double)this.random.nextFloat() - 0.5D) * 0.8D,
                            ((double)this.random.nextFloat() - 0.5D) * 0.2D,
                            ((double)this.random.nextFloat() - 0.5D) * 0.1D,
                            ((double)this.random.nextFloat() - 0.5D) * 0.2D);
                    }

        if (!this.level().isClientSide) {
            this.discard();
        }
    }
}

