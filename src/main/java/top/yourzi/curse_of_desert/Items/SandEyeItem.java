package top.yourzi.curse_of_desert.Items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.tags.BiomeTags;
import top.yourzi.curse_of_desert.AttackEvent.CurseOfDesertEvent;
import top.yourzi.curse_of_desert.Events.CurseOfDesertEventHandler;

public class SandEyeItem extends Item {
    private static final int USE_DURATION = 60; // 3秒使用时间
    private static final int CHECK_RADIUS = 32; // 检查生物群系的半径
    private static final int EVENT_CHECK_RADIUS = 100; // 检查事件的半径

    private final AnimationState useAnimation = new AnimationState();
    private int animationTick = 0;

    public SandEyeItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseTicks) {
        if (entity.level().isClientSide() && entity instanceof Player player) {

            // 在客户端处理发光效果
            if (remainingUseTicks == 30) {
                if (isValidLocation(entity.level(), player.blockPosition())) {
                    stack.getOrCreateTag().putBoolean("glowing", true);
                } else {
                    player.getCooldowns().addCooldown(stack.getItem(), 80);
                    player.displayClientMessage(Component.translatable("message.curse_of_desert.sand_eye.invalid_location").withStyle(style -> style.withColor(0x8B3A3A).withBold(true)), false);
                }
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (level.isClientSide()) {
            Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
            useAnimation.stop();
            animationTick = 0;
        }
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && entity instanceof Player player) {
            BlockPos pos = player.blockPosition();
            if (isValidLocation(level, pos)) {
                stack.getOrCreateTag().putBoolean("glowing", true);
                // 创建新的沙漠诅咒事件
                CurseOfDesertEvent event = new CurseOfDesertEvent(serverLevel, pos);
                CurseOfDesertEventHandler.setCurrentEvent(event);
                ServerPlayer serverPlayer = (ServerPlayer) player;
                stack.getOrCreateTag().putBoolean("glowing", false);
                // 消耗物品
                stack.shrink(serverPlayer.gameMode.isCreative() ? 0 : 1);
                return stack;
            }
        }
        return stack;
    }

    private boolean isValidLocation(Level level, BlockPos pos) {
        // 检查是否有活跃的事件
        if (hasActiveEventNearby(pos, EVENT_CHECK_RADIUS)) {
            return false;
        }

        // 检查周围生物群系
        return isDesertBiomeArea(level, pos, CHECK_RADIUS);
    }

    private boolean hasActiveEventNearby(BlockPos pos, int radius) {
        CurseOfDesertEvent currentEvent = CurseOfDesertEventHandler.getCurrentEvent();
        if (currentEvent != null && currentEvent.isActive()) {
            BlockPos eventCenter = currentEvent.getCenter();
            return pos.distSqr(eventCenter) <= radius * radius;
        }
        return false;
    }

    private boolean isDesertBiomeArea(Level level, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // 只检查圆形范围内的点
                if (x * x + z * z <= radius * radius) {
                    BlockPos checkPos = center.offset(x, 0, z);
                    if (!level.getBiome(checkPos).is(BiomeTags.HAS_DESERT_PYRAMID)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.curse_of_desert.sand_eye.desc").withStyle(style -> style.withColor(0xEEDC82)));
    }
}