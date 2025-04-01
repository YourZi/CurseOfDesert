package top.yourzi.curse_of_desert.Entities.Bitumen;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import top.yourzi.curse_of_desert.init.ModItems;

public class Bitumen extends Entity {
    public static final String TRANSLATION_KEY = "entity.curse_of_desert.bitumen";
    private static final EntityDataAccessor<Integer> LIFETIME_TICKS_ID = SynchedEntityData.defineId(Bitumen.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TEXTURE_ROTATION_ID = SynchedEntityData.defineId(Bitumen.class, EntityDataSerializers.INT);
    
    // 存储纹理旋转角度（0, 90, 180, 270度）
    private int textureRotation;

    @Override
    public Component getDisplayName() {
        return Component.translatable(TRANSLATION_KEY);
    }
    public Bitumen(EntityType<?> type, Level level) {
        super(type, level);
        
        if (!level.isClientSide) {
            // 随机选择一个旋转角度：0, 90, 180, 270度
            int[] rotations = {0, 90, 180, 270};
            this.textureRotation = rotations[level.getRandom().nextInt(rotations.length)];
            this.entityData.set(TEXTURE_ROTATION_ID, this.textureRotation);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LIFETIME_TICKS_ID, 0);
        this.entityData.define(TEXTURE_ROTATION_ID, 0);
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        if (tag.contains("LifetimeTicks")) {
            this.lifetimeTicks = tag.getInt("LifetimeTicks");
        }
    }

    @Override
    protected void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        tag.putInt("LifetimeTicks", this.lifetimeTicks);
    }

    private int lifetimeTicks = 0;
    private static final int LIFETIME_TICKS = 20 * 20; // 20秒 (20 ticks/秒)
    private static final int STRONG_SLOW_END = 10 * 20; // 前10秒强减速
    private static final int WEAK_SLOW_END = 15 * 20; // 后5秒弱减速

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            lifetimeTicks++;
            this.entityData.set(LIFETIME_TICKS_ID, lifetimeTicks);
        } else {
            lifetimeTicks = this.entityData.get(LIFETIME_TICKS_ID);
        }

        // 检查是否应该消失
        if (lifetimeTicks >= LIFETIME_TICKS) {
            this.discard();
            return;
        }

        // 应用重力
        if (!this.isNoGravity()) {
            Vec3 movement = this.getDeltaMovement();
            this.setDeltaMovement(movement.x, movement.y - 0.05D, movement.z);
        }
        this.move(MoverType.SELF, this.getDeltaMovement());

        // 检测与实体的碰撞
        if (!this.level().isClientSide) {
            AABB boundingBox = this.getBoundingBox();
            this.level().getEntities(this, boundingBox).forEach(this::entityInside);
        }
    }

    public int getLifetimeTicks() {
        return lifetimeTicks;
    }
    
    // 获取纹理旋转角度
    public int getTextureRotation() {
        return this.entityData.get(TEXTURE_ROTATION_ID);
    }

    // 处理生物踩踏效果
    public void entityInside(Entity entity) {
        if (entity == null) return;
        
        // 只对生物实体应用效果
        if (entity instanceof LivingEntity livingEntity) {
            // 根据时间段应用不同程度的减速
            if (lifetimeTicks <= STRONG_SLOW_END) {
                // 前10秒减速50%
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().multiply(0.5D, 0.5D, 0.5D));
            } else if (lifetimeTicks <= WEAK_SLOW_END) {
                // 后5秒减速25%
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().multiply(0.75D, 0.75D, 0.75D));
            }
            // 最后5秒不减速
        }
    }

    @Override
    protected AABB makeBoundingBox() {
        // 设置碰撞箱大小为1.8x1.8x0.1
        return new AABB(
            this.getX() - 0.9D, this.getY(), this.getZ() - 0.9D,
            this.getX() + 0.9D, this.getY() + 0.1D, this.getZ() + 0.9D
        );
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
    
    @Override
    public void playerTouch(Player player) {
        entityInside(player);
    }


    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);

        if (itemInHand.is(Items.GLASS_BOTTLE)) {
            if (level().isClientSide) {
                player.swing(hand);
                player.playSound(SoundEvents.BOTTLE_FILL, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }

            // 增加100tick的生命值
            this.setLifetimeTicks(this.getLifetimeTicks() + 100);

            // 消耗玻璃瓶
            if (!player.getAbilities().instabuild) {
                itemInHand.shrink(1);
            }

            // 给予玩家瓶装沥青物品
            ItemStack bitumenBottle = new ItemStack(ModItems.BITUMEN_BOTTLE_ITEM.get());
            if (!player.getInventory().add(bitumenBottle)) {
                player.drop(bitumenBottle, false);
            }

            this.gameEvent(GameEvent.ENTITY_INTERACT, player);
            return InteractionResult.SUCCESS;
        }

        this.gameEvent(GameEvent.ENTITY_INTERACT, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean mayInteract(Level pLevel, BlockPos pPos) {
        return true;
    }

    public void setLifetimeTicks(int i) {
        this.lifetimeTicks = i;
    }
}