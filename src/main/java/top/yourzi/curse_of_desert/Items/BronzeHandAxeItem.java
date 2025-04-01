package top.yourzi.curse_of_desert.Items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BronzeHandAxeItem extends AxeItem {
    private static final Tier BRONZE_TIER = new Tier() {
        
        @Override
        public int getUses() {
            return 470;
        }

        @Override
        public float getSpeed() {
            return 4F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 7F;
        }

        @Override
        public int getLevel() {
            return 2;
        }

        @Override
        public int getEnchantmentValue() {
            return 14;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    };

    public BronzeHandAxeItem(Properties properties) {
        super(BRONZE_TIER, 1F, -3.1F, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
            // 检查是否在沙漠生物群系
            if (level.getBiome(pos).is(BiomeTags.HAS_DESERT_PYRAMID)) {
                // 50%概率不消耗耐久
                if (level.random.nextFloat() >= 0.5F) {
                    stack.setDamageValue(stack.getDamageValue());
                    return true;
                }
            }
        }
        return super.mineBlock(stack, level, state, pos, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.curse_of_desert.bronze_hand_axe.desc").withStyle(style -> style.withColor(0xEEDC82)));
    }
}