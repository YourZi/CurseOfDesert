package top.yourzi.curse_of_desert.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;
import top.yourzi.curse_of_desert.Entities.Bitumen.Bitumen;
import top.yourzi.curse_of_desert.init.ModEntities;
import top.yourzi.curse_of_desert.init.ModSounds;

public class BitumenBottle extends FallingBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 8, 12);

    public BitumenBottle(Properties pProperties) {
        super(BlockBehaviour.Properties.of()
            .copy(Blocks.GLASS)
            .sound(new SoundType(1.0F, 1.0F,
                ModSounds.BOTTLE_BREAK.get(),
                SoundEvents.GLASS_STEP,
                ModSounds.BOTTLE_PLACED.get(),
                SoundEvents.GLASS_HIT,
                SoundEvents.GLASS_FALL))
            .noOcclusion()
            .isRedstoneConductor((bs, br, bp) -> false)
            );

        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    private void spawnBitumen(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            Bitumen bitumen = new Bitumen(ModEntities.BITUMEN.get(), level);
            bitumen.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
            level.addFreshEntity(bitumen);
            serverLevel.sendParticles(new net.minecraft.core.particles.BlockParticleOption(
                net.minecraft.core.particles.ParticleTypes.BLOCK,
                net.minecraft.world.level.block.Blocks.GLASS.defaultBlockState()), pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 64, 0.5, 0.5, 0.5, 0.008);
            serverLevel.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 80, 0.3, 0.4, 0.3, 0.01);
        }
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock) {
        super.onLand(level, pos, state, replaceableState, fallingBlock);
        spawnBitumen(level, pos);
        level.destroyBlock(pos, false);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity.fallDistance > 3.0F) {
            spawnBitumen(level, pos);
            level.destroyBlock(pos, false);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
	public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
		if (!EnchantmentHelper.getEnchantments(entity.getMainHandItem()).containsKey(Enchantments.SILK_TOUCH)) {
			spawnBitumen(world, pos);
		}
		boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
		return retval;
	}


    @Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        return SHAPE;
    }
}
