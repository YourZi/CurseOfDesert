package top.yourzi.curse_of_desert.Entities.BitumenMummy;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import top.yourzi.curse_of_desert.Entities.Bitumen.Bitumen;
import top.yourzi.curse_of_desert.Entities.Mummy.Mummy;
import top.yourzi.curse_of_desert.Entities.ai.BitumenMummyRangedAttackGoal;
import top.yourzi.curse_of_desert.Entities.ai.MummyAttackGoal;
import top.yourzi.curse_of_desert.init.ModEntities;
import top.yourzi.curse_of_desert.init.ModSounds;

public class BitumenMummy extends Mummy implements RangedAttackMob {
    private static final EntityDataAccessor<Boolean> IS_THROWING_BOTTLE =
            SynchedEntityData.defineId(BitumenMummy.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_BOTTLE =
            SynchedEntityData.defineId(BitumenMummy.class, EntityDataSerializers.BOOLEAN);

    public BitumenMummy(EntityType<? extends Mummy> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private int throwbottleAnimationTimeout = 0;

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof MummyAttackGoal);
        this.goalSelector.addGoal(2, new BitumenMummyRangedAttackGoal(this, 1, 15, 20));
        updateAttackGoals();
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        boolean flag = super.doHurtTarget(pEntity);
        return flag;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_BOTTLE, true);
        this.entityData.define(IS_THROWING_BOTTLE, false);
    }

    public boolean hasBottle() {
        return this.entityData.get(HAS_BOTTLE);
    }

    public void setHasBottle(boolean hasBottle) {
        this.entityData.set(HAS_BOTTLE, hasBottle);
    }

    public boolean isThrowingBottle() {
        return this.entityData.get(IS_THROWING_BOTTLE);
    }

    public void setThrowingBottle(boolean throwing) {
        this.entityData.set(IS_THROWING_BOTTLE, throwing);
    }

    private void setupThrowAnimationStates() {
        if(this.isThrowingBottle() && throwbottleAnimationTimeout <= 0 && this.hasBottle()) {
            throwbottleAnimationTimeout = 21;
            throwbottle.start(this.tickCount);
        } else {
            --this.throwbottleAnimationTimeout;
        }
    }

    private void updateAttackGoals() {
            if (!this.hasBottle()) {
                this.goalSelector.addGoal(1, new MummyAttackGoal(this, 1.0D, true));
                this.goalSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof BitumenMummyRangedAttackGoal);
            }
        }

    @Override
    public void tick() {
        super.tick();
        updateAttackGoals();
        setupThrowAnimationStates();
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pVelocity) {

    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.hasBottle()) {
            this.setHasBottle(false);
            // 播放玻璃破碎音效
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.BOTTLE_BREAK.get(),
                net.minecraft.sounds.SoundSource.NEUTRAL, 1.0F, 1.0F);
                // 生成沥青
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
                    ((double)this.random.nextFloat() - 0.5D) * 1.6D,
                    ((double)this.random.nextFloat() - 0.5D) * 1.2D,
                    ((double)this.random.nextFloat() - 0.5D) * 1.6D);
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
            return super.hurt(pSource, Math.max(pAmount - this.getMaxHealth() * 10 , 0.0F));
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("HasBottle", this.hasBottle());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("HasBottle")) {
            this.setHasBottle(pCompound.getBoolean("HasBottle"));
        }
    }
}