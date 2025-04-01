package top.yourzi.curse_of_desert.Entities.BitumenBottle;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import top.yourzi.curse_of_desert.Entities.Bitumen.Bitumen;
import top.yourzi.curse_of_desert.init.ModEntities;

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
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
        if (this.level() instanceof ServerLevel serverLevel) {
            if (pResult instanceof EntityHitResult entityHitResult) {
                entityHitResult.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 6.0F);
                Bitumen bitumen = new Bitumen(ModEntities.BITUMEN.get(), this.level());
                bitumen.setPos(entityHitResult.getLocation());
                this.level().addFreshEntity(bitumen);
            } else if (pResult instanceof BlockHitResult blockHitResult) {
                Bitumen bitumen = new Bitumen(ModEntities.BITUMEN.get(), this.level());
                bitumen.setPos(blockHitResult.getLocation());
                this.level().addFreshEntity(bitumen);
            }
            this.discard();
            serverLevel.sendParticles(new BlockParticleOption(
                ParticleTypes.BLOCK,
                Blocks.GLASS.defaultBlockState()), this.getX() + 0.5D, this.getY(), this.getZ() + 0.5D, 64, 0.5, 0.5, 0.5, 0.008);
            serverLevel.sendParticles(ParticleTypes.SMOKE, this.getX() + 0.5D, this.getY(), this.getZ() + 0.5D, 80, 0.3, 0.4, 0.3, 0.01);
        }
    }
}