package top.yourzi.curse_of_desert.AttackEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import top.yourzi.curse_of_desert.init.ModEffect;
import top.yourzi.curse_of_desert.init.ModEntities;
import top.yourzi.curse_of_desert.init.ModTags;

import java.util.ArrayList;
import java.util.List;

/**
 * 沙漠诅咒事件类
 * 负责管理沙漠诅咒事件的整个生命周期，包括初始化、波次管理、实体生成和状态更新
 */
public class CurseOfDesertEvent {
    private final ServerLevel level;
    private final BlockPos center;
    private final List<ServerPlayer> players;
    private final ServerBossEvent bossEvent;
    
    // 事件状态相关
    private int currentWave;
    private final int totalWaves;
    private boolean isActive;
    private float maxHealthRecord;
    private boolean isFinishing;
    private boolean finishSuccess;
    private boolean delayFinishing;
    
    // 初始化相关
    private boolean isInitializing;
    private long initStartTime;
    private long finishStartTime;
    private static final long INIT_DURATION = 7000;
    
    // 事件范围常量
    private static final int EVENT_RADIUS = 50;
    private static final int SPAWN_MIN_DISTANCE = 2;
    private static final int SPAWN_MAX_DISTANCE = 10;
    private static final int SPAWN_HEIGHT_SEARCH_RANGE = 30;
    
    /**
     * 构造函数
     * @param level 服务器世界实例
     * @param center 事件中心点位置
     */
    public CurseOfDesertEvent(ServerLevel level, BlockPos center) {
        this.level = level;
        this.center = center;
        this.players = new ArrayList<>();
        this.bossEvent = new ServerBossEvent(
            Component.translatable("event.curse_of_desert.name"),
            BossEvent.BossBarColor.YELLOW,
            BossEvent.BossBarOverlay.PROGRESS
        );
        this.currentWave = 0;
        this.totalWaves = 7;
        this.isActive = true;
        this.maxHealthRecord = 0;
        this.isInitializing = true;
        this.initStartTime = System.currentTimeMillis();

        // 检查范围内已存在的事件生物
        AABB area = new AABB(center).inflate(50);
        List<LivingEntity> existingEntities = level.getEntitiesOfClass(LivingEntity.class, area,
            entity -> entity.getType().is(ModTags.CURSE_OF_DESERT));

        if (!existingEntities.isEmpty()) {
            // 如果有已存在的事件生物，直接开始第一波
            this.currentWave = 1;
            spawnWaveEntities();
            // 计算已存在事件生物的最大生命值总和
            for (LivingEntity entity : existingEntities) {
                if (entity.isAlive()) {
                    this.maxHealthRecord += entity.getMaxHealth();
                }
            }
        }
    }

    private int tickCounter = 0;
    private static final int TICK_INTERVAL = 2;

    /**
     * 事件主循环更新
     * 处理事件的所有逻辑，包括初始化、波次更新和状态检查
     */
    public void tick() {
        if (!isActive) {
            bossEvent.setVisible(false);
            return;
        };
        bossEvent.setVisible(true);
    
        // 每2tick执行一次更新
        if (++tickCounter >= 2) {
            tickCounter = 0;
            // 更新玩家列表
            updatePlayers();
    
            // 处理初始化阶段
            if (isInitializing) {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - initStartTime;
                
                if (elapsedTime >= INIT_DURATION) {
                    // 初始化完成
                    isInitializing = false;
                    bossEvent.setProgress(1.0F);
                } else {
                    // 更新初始化进度条
                    float progress = (float) elapsedTime / INIT_DURATION;
                    bossEvent.setProgress(progress);
                    return; // 在初始化阶段不执行其他逻辑
                }
            }
    
            if (isActive && currentWave <= 0){
                startNextWave();
            }
    
            // 检查当前波次的怪物是否全部被击败
            if (isCurrentWaveCleared()) {
                if (currentWave < totalWaves) {
                    float currentProgress = bossEvent.getProgress();
                    if (currentProgress < 1.0F) {
                        // 增加进度条
                        bossEvent.setProgress(Math.min(1.0F, currentProgress + 0.030F));
                    } else {
                        // 进度条满了，开始下一波
                        startNextWave();
                    }
                } else {
                    // 所有波次完成，事件结束
                    bossEvent.setProgress(0);
                    try {
                        if (!delayFinishing){
                            finishSuccess = true;
                            delayFinishing = true;
                            Thread.sleep(1700);
                            finish(true);
                        }else return;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            if (!isCurrentWaveCleared()){
                updateBossBar();
            }
        }
    }

    /**
     * 更新Boss栏进度
     * 根据当前存活实体的生命值计算并更新Boss栏进度
     */
    private void updateBossBar() {
        AABB area = new AABB(center).inflate(50);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area,
            entity -> entity.getType().is(ModTags.CURSE_OF_DESERT));

        float maxHealthSum = 0;
        float currentHealthSum = 0;

        for (LivingEntity entity : entities) {
            if(entity.isAlive()){
                maxHealthSum += entity.getMaxHealth();
                currentHealthSum += entity.getHealth();
            }
        }
        
        // 更新历史最大生命值记录，只增不减
        if (maxHealthSum > maxHealthRecord) {
            maxHealthRecord = maxHealthSum;
        }
        
        // 使用历史最大生命值记录作为分母
        if (maxHealthRecord > 0) {
            bossEvent.setProgress(currentHealthSum / maxHealthRecord);
        } else {
            bossEvent.setProgress(0);
        }
    }

    /**
     * 更新玩家列表并处理玩家状态
     * 检查玩家存活状态，更新Boss栏显示，应用法老凝视效果
     */
    private void updatePlayers() {
        // 获取范围内的所有玩家
        AABB box = new AABB(center).inflate(50);
        List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(ServerPlayer.class, box);

        // 如果范围内没有玩家，结束事件并标记为失败
        if (nearbyPlayers.isEmpty()) {
            finish(false);
            return;
        }

        // 检查范围内玩家的生命状态
        for (ServerPlayer player : nearbyPlayers) {
            if (!player.isAlive()) {
                // 如果发现有玩家死亡，立即结束事件并标记为失败
                try {
                    if (!delayFinishing){
                        finishSuccess = false;
                        delayFinishing = true;
                        Thread.sleep(1700);
                        finish(false);
                    }else return;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return;
            }
        }

        // 移除所有不在范围内的玩家的Boss栏显示
        for (ServerPlayer player : new ArrayList<>(players)) {
            if (!nearbyPlayers.contains(player)) {
                bossEvent.removePlayer(player);
                players.remove(player);
            }
        }

        // 为新进入范围的玩家添加Boss栏显示
        for (ServerPlayer player : nearbyPlayers) {
            if (!players.contains(player)) {
                bossEvent.addPlayer(player);
                players.add(player);
            }
        }

        // 处理范围内的所有生物
        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, box);
        int effectLevel = level.getDifficulty() == Difficulty.HARD ? 1 : 0; // 困难模式为2级效果（索引为1），其他为1级效果（索引为0）

        for (LivingEntity entity : nearbyEntities) {
            // 跳过事件相关的生物
            if (entity.getType().is(ModTags.CURSE_OF_DESERT)) {
                continue;
            }

            // 为非事件生物添加或更新法老凝视效果
            if (isActive) {
                entity.addEffect(new MobEffectInstance(ModEffect.PHARAOH_GAZE.get(), 20, effectLevel, false, false));
            }
        }
    }

    /**
     * 结束事件
     * @param success 事件是否成功完成
     */
    public void finish(boolean success) {
        isFinishing = true;
        finishSuccess = success;
        finishStartTime = System.currentTimeMillis();
        isActive = false;

        try {
            // 延迟0.2秒执行
            Thread.sleep(200);
            // 移除所有玩家的Boss栏显示
            for (ServerPlayer player : new ArrayList<>(players)) {
                bossEvent.removePlayer(player);
            }
            players.clear();
            maxHealthRecord = 0;
        } catch (InterruptedException e) {
            // 处理中断异常
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 检查当前波次是否已清理完毕
     * @return 如果当前波次的所有实体都已被击败则返回true
     */
    private boolean isCurrentWaveCleared() {
        // 获取事件区域内所有事件生物
        AABB area = new AABB(center).inflate(50);
        List<LivingEntity> allEntities = level.getEntitiesOfClass(LivingEntity.class, area,
            entity -> entity.getType().is(ModTags.CURSE_OF_DESERT));
        
        // 检查所有事件生物是否死亡
        return allEntities.stream().noneMatch(LivingEntity::isAlive);
    }

    /**
     * 开始下一波次
     * 重置相关状态并生成新的实体
     */
    private void startNextWave() {
        currentWave++;
        maxHealthRecord = 0; // 重置历史最大生命值记录
        spawnWaveEntities();
    }

    /**
     * 生成指定类型和数量的实体
     * @param entityType 实体类型
     * @param number 生成数量
     */
    private void spawnEntities(EntityType<?> entityType, int number) {
        for (int i = 0; i < number; i++) {
            // 在中心点附近随机选择一个位置
            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = SPAWN_MIN_DISTANCE + level.random.nextDouble() * (SPAWN_MAX_DISTANCE - SPAWN_MIN_DISTANCE);
            double x = center.getX() + Math.cos(angle) * distance;
            double z = center.getZ() + Math.sin(angle) * distance;
            
            BlockPos spawnPos = findValidSpawnPosition(new BlockPos((int)x, center.getY(), (int)z));
            if (spawnPos != null) {
                spawnEntityWithEffects(entityType, spawnPos);
            }
        }
    }

    /**
     * 查找有效的生成位置
     * @param startPos 起始搜索位置
     * @return 有效的生成位置，如果未找到则返回null
     */
    private BlockPos findValidSpawnPosition(BlockPos startPos) {
        startPos = new BlockPos(startPos.getX(), Math.max(level.getMinBuildHeight(), startPos.getY() - 20), startPos.getZ());
        
        for (int searchHeight = 0; searchHeight < SPAWN_HEIGHT_SEARCH_RANGE; searchHeight++) {
            BlockPos currentPos = startPos.above(searchHeight);
            if (isValidSpawnPosition(currentPos)) {
                return currentPos;
            }
        }
        return null;
    }

    /**
     * 检查位置是否适合生成实体
     * @param pos 待检查的位置
     * @return 如果位置有效则返回true
     */
    private boolean isValidSpawnPosition(BlockPos pos) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), net.minecraft.core.Direction.UP) &&
               level.getBlockState(pos).isAir() &&
               level.getBlockState(pos.above()).isAir();
    }

    /**
     * 在指定位置生成实体并添加特效
     * @param entityType 实体类型
     * @param pos 生成位置
     */
    private void spawnEntityWithEffects(EntityType<?> entityType, BlockPos pos) {
        Entity entity = entityType.create(level);
        if (entity != null) {
            entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            level.addFreshEntity(entity);
            spawnSandParticles(pos);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 生成沙子粒子效果
     * @param pos 粒子生成位置
     */
    private void spawnSandParticles(BlockPos pos) {
        for (int i = 0; i < 20; i++) {
            double offsetX = level.random.nextDouble() * 2 - 1;
            double offsetY = level.random.nextDouble() * 2;
            double offsetZ = level.random.nextDouble() * 2 - 1;
            double speed = 0.8;
            level.sendParticles(
                new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState()),
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                5,
                offsetX * speed,
                offsetY * speed,
                offsetZ * speed,
                1.0
            );
        }
    }

    // Getter方法
    public boolean isActive() { return isActive; }
    public boolean isFinishing() { return isFinishing; }
    public boolean delayFinishing() { return delayFinishing; }
    public boolean getFinishSuccess() { return finishSuccess; }
    public int getCurrentWave() { return currentWave; }
    public BlockPos getCenter() { return center; }
    public int getTotalWaves() { return totalWaves; }


    private void spawnWaveEntities() {
        switch (currentWave) {
            case 1:
                spawnEntities(ModEntities.MUMMY.get(), 4);
                spawnEntities(ModEntities.SCARAB_BEETLE.get(), 3);
                break;
            case 2:
                spawnEntities(ModEntities.MUMMY.get(), 3);
                break;
            case 3:
                spawnEntities(ModEntities.MUMMY.get(), 1);
                break;
            case 4:
                spawnEntities(ModEntities.MUMMY.get(), 1);
                break;
            case 5:
                spawnEntities(ModEntities.MUMMY.get(), 1);
                break;
            case 6:
                spawnEntities(ModEntities.MUMMY.get(), 1);
                break;
            case 7:
                spawnEntities(ModEntities.MUMMY.get(), 1);
                break;
        }

    }
}