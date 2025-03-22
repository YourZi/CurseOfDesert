package top.yourzi.curse_of_desert.Entities.BitumenBottle;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import top.yourzi.curse_of_desert.Entities.Mummy.Mummy;
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
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), net.minecraft.sounds.SoundEvents.GLASS_BREAK, net.minecraft.sounds.SoundSource.NEUTRAL, 1.0F, 1.0F);
        if (!this.level().isClientSide) {
            if (pResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) pResult;
                // 在碰撞点生成木乃伊
                Mummy mummy = new Mummy(ModEntities.MUMMY.get(), this.level());
                mummy.setPos(blockHitResult.getLocation());
                this.level().addFreshEntity(mummy);
            }
            this.discard(); // 移除投掷物实体
        }
    }
}