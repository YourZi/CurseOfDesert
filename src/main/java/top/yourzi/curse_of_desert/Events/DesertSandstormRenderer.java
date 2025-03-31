package top.yourzi.curse_of_desert.Events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.yourzi.curse_of_desert.Curseofdesert;
import top.yourzi.curse_of_desert.AttackEvent.CurseOfDesertEvent;

import java.util.Random;

/**
 * 沙漠诅咒事件的沙尘暴渲染器
 * 在事件范围内生成沙尘粒子效果
 */
@Mod.EventBusSubscriber(modid = Curseofdesert.MOD_ID, value = Dist.CLIENT)
public class DesertSandstormRenderer {
    private static final Random RANDOM = new Random();
    private static final float PARTICLE_DENSITY = 0.95f; // 提高粒子密度
    private static final int PARTICLE_RANGE = 30; // 扩大粒子生成范围
    private static final int EVENT_RANGE = 50; // 事件范围

    /**
     * 客户端Tick事件处理器
     * 在玩家处于沙漠诅咒事件范围内时生成沙尘粒子
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        Player player = minecraft.player;

        // 确保客户端和玩家已加载
        if (level == null || player == null) return;

        // 获取当前事件
        CurseOfDesertEvent currentEvent = CurseOfDesertEventHandler.getCurrentEvent();
        
        // 检查事件是否为空
        if (currentEvent == null) return;

        // 检查玩家是否在事件范围内
        BlockPos eventCenter = currentEvent.getCenter();
        if (eventCenter == null) return;

        // 计算玩家与事件中心的距离
        double distance = player.position().distanceTo(new Vec3(eventCenter.getX(), eventCenter.getY(), eventCenter.getZ()));

        if (distance <= EVENT_RANGE) {
            spawnSandstormParticles(level, player);
        }
    }
    /**
     * 在玩家周围生成沙尘暴粒子
     */
    private static void spawnSandstormParticles(ClientLevel level, Player player) {
        // 获取玩家位置
        Vec3 playerPos = player.getEyePosition();

        // 在玩家周围随机位置生成粒子
        for (int i = 0; i < 128; i++) { // 增加每tick生成的粒子数量
            if (RANDOM.nextFloat() > PARTICLE_DENSITY) continue; // 根据密度控制粒子数量

            // 计算随机偏移
            double xOffset = (RANDOM.nextDouble() - 0.5) * PARTICLE_RANGE * 2;
            double yOffset = (RANDOM.nextDouble() - 0.2) * PARTICLE_RANGE; // 更多粒子从上方落下
            double zOffset = (RANDOM.nextDouble() - 0.5) * PARTICLE_RANGE * 2;

            // 计算粒子位置
            double x = playerPos.x + xOffset;
            double y = playerPos.y + yOffset;
            double z = playerPos.z + zOffset;

            // 计算粒子速度
            double xSpeed = (RANDOM.nextDouble() - 0.5) * 100.0; // 增加水平速度
            double ySpeed = 0;
            double zSpeed = (RANDOM.nextDouble() - 0.5) * 100.0; // 增加水平速度

            // 生成沙子粒子
            level.addParticle(
                    new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState()),
                    x, y, z,
                    xSpeed, ySpeed, zSpeed
            );

            // 增加灰尘粒子的生成概率
            if (RANDOM.nextFloat() < 0.3) {
                level.addParticle(
                        ParticleTypes.ASH,
                        x, y, z,
                        xSpeed * 0.5, ySpeed * 0.5, zSpeed * 0.5
                );
            }
        }
    }
}